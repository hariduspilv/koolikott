package ee.hm.dop.service.login;

import ee.hm.dop.dao.AgreementDao;
import ee.hm.dop.model.Agreement;
import ee.hm.dop.model.User;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.List;

import static ee.hm.dop.utils.UserUtil.mustBeAdmin;

public class AgreementService {

    @Inject
    private AgreementDao agreementDao;

    public List<Agreement> findAllValid(User user){
        mustBeAdmin(user);
        return agreementDao.findByFieldList("deleted", false);
    }

    public void save(User user, Agreement agreement) {
        mustBeAdmin(user);
        agreement.setCreatedAt(DateTime.now());
        agreement.setCreatedBy(user);
        agreementDao.createOrUpdate(agreement);
    }

    public void deleteAgreement(long id, User user) {
        mustBeAdmin(user);
        //todo find other agreement with same stuff

    }
}
