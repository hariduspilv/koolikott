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

import static ee.hm.dop.utils.UserUtil.mustBeAdmin;

@Service
@Transactional
public class AgreementService {

    @Inject
    private AgreementDao agreementDao;

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

}
