package ee.hm.dop.service.login;

import ee.hm.dop.dao.AgreementDao;
import ee.hm.dop.model.Agreement;
import ee.hm.dop.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static ee.hm.dop.utils.UserUtil.mustBeAdmin;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@Service
@Transactional
public class AgreementService {

    @Inject
    private AgreementDao agreementDao;

    public List<Agreement> findAllValid(User user) {
        mustBeAdmin(user);
        return agreementDao.getValidAgreements();
    }

    public boolean isValid(Agreement agreement, User user) {
        mustBeAdmin(user);
        if (agreement.getVersion() == null || agreement.getValidFrom() == null) {
            return false;
        }
        agreement.setValidFrom(agreement.getValidFrom().truncatedTo(ChronoUnit.DAYS));
        return isEmpty(agreementDao.findMatchingAgreements(agreement));
    }

    public Agreement save(Agreement agreement, User user) {
        mustBeAdmin(user);
        if (agreement.getVersion() == null || agreement.getValidFrom() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "missing version or validFrom");
        }
        agreement.setValidFrom(agreement.getValidFrom().truncatedTo(ChronoUnit.DAYS));
        agreement.setVersion(agreement.getVersion().trim());
        agreement.setCreatedAt(LocalDateTime.now());
        agreement.setCreatedBy(user);
        Agreement newAgreement = agreementDao.createOrUpdate(agreement);
        if (newAgreement != null) {
            Agreement previousDeletedAgreement = agreementDao.findMatchingDeletedAgreement(newAgreement);
            if (previousDeletedAgreement != null) {
                agreementDao.updateUserAgreementsForUsersWhoAgreedToPreviousVersion(previousDeletedAgreement, newAgreement);
            }
        }
        return newAgreement;
    }

    public void deleteAgreement(Agreement agreement, User user) {
        mustBeAdmin(user);
        Agreement dbAgreement = agreementDao.findById(agreement.getId());
        if (dbAgreement != null) {
            dbAgreement.setDeleted(true);
            agreementDao.createOrUpdate(dbAgreement);
        }
    }
}
