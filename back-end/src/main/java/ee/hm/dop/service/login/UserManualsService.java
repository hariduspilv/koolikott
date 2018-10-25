package ee.hm.dop.service.login;

import ee.hm.dop.dao.AgreementDao;
import ee.hm.dop.dao.UserManualsDao;
import ee.hm.dop.model.Agreement;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserManuals;
import ee.hm.dop.rest.BaseResource;
import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

import static ee.hm.dop.utils.UserUtil.mustBeAdmin;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

public class UserManualsService {

    @Inject
    private UserManualsDao userManualsDao;

    public List<UserManuals> findAllUserManuals(){
        return userManualsDao.findAll();
    }

/*    public boolean isValid(Agreement agreement, User user) {
        mustBeAdmin(user);
        if (agreement.getVersion() == null || agreement.getValidFrom() == null){
            return false;
        }
        agreement.setValidFrom(agreement.getValidFrom().withTimeAtStartOfDay());
        return isEmpty(agreementDao.findMatchingAgreements(agreement));
    }*/

    public UserManuals save(UserManuals userManuals, User user) {
        mustBeAdmin(user);
        if (userManuals.getTitle()== null || userManuals.getUrl() == null){
            throw new WebApplicationException("missing title or url", Response.Status.BAD_REQUEST);
        }

        userManuals.setTitle(userManuals.getTitle().trim());
        userManuals.setUrl(userManuals.getUrl());
        userManuals.setTextUrl(userManuals.getTextUrl());
        userManuals.setCreatedAt(DateTime.now());
        userManuals.setCreatedBy(user);
        UserManuals newUserManual = userManualsDao.createOrUpdate(userManuals);

        return newUserManual;
    }

    public void deleteUserManual(UserManuals userManuals, User user) {
        mustBeAdmin(user);
        UserManuals dbUserManual = userManualsDao.findById(userManuals.getId());
        if (dbUserManual != null){
            userManualsDao.remove(dbUserManual);
        }
    }
}
