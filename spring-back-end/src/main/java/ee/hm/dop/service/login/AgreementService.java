package ee.hm.dop.service.login;

import ee.hm.dop.dao.AgreementDao;
import ee.hm.dop.model.Agreement;
import ee.hm.dop.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDateTime;

import static ee.hm.dop.utils.UserUtil.mustBeAdmin;

@Service
@Transactional
public class AgreementService {

    @Inject
    private AgreementDao agreementDao;

    public Agreement save(Agreement agreement, User user) {
        mustBeAdmin(user);
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
