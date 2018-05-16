package ee.hm.dop.service.login;

import ee.hm.dop.dao.AgreementDao;
import ee.hm.dop.model.Agreement;
import ee.hm.dop.model.User;
import ee.hm.dop.utils.ValidatorUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.List;

import static ee.hm.dop.utils.UserUtil.mustBeAdmin;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

public class AgreementService {

    @Inject
    private AgreementDao agreementDao;

    public List<Agreement> findAllValid(User user){
        mustBeAdmin(user);
        return agreementDao.findByFieldList("deleted", false);
    }

    public Agreement save(Agreement agreement, User user) {
        mustBeAdmin(user);
        if (agreement.getVersion() == null || agreement.getValidFrom() == null){
            throw new RuntimeException("missing data");
        }
        agreement.setVersion(agreement.getVersion().trim());
        agreement.setCreatedAt(DateTime.now());
        agreement.setCreatedBy(user);
        Agreement dbAgreement = agreementDao.createOrUpdate(agreement);
        if (dbAgreement != null){
            List<Agreement> matchingAgreements = agreementDao.findMatchingAgreements(agreement);
            if (isNotEmpty(matchingAgreements)){
                agreementDao.updateUserAgreementsForUsersWhoAgreedToPreviousVersion();
            }
        }
        return dbAgreement;
    }

    public void deleteAgreement(Agreement agreement, User user) {
        mustBeAdmin(user);
        Agreement dbAgreement = agreementDao.findById(agreement.getId());
        if (dbAgreement != null){
            dbAgreement.setDeleted(true);
            agreementDao.createOrUpdate(dbAgreement);
        }
    }
}
