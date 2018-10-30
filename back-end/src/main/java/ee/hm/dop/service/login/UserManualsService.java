package ee.hm.dop.service.login;

import ee.hm.dop.dao.UserManualsDao;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserManuals;
import ee.hm.dop.utils.UrlUtil;
import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

import static ee.hm.dop.utils.UserUtil.mustBeAdmin;

public class UserManualsService {

    @Inject
    private UserManualsDao userManualsDao;

    public List<UserManuals> findAllUserManuals(){
        return userManualsDao.findAll();
    }

    public UserManuals save(UserManuals userManuals, User user) {
        mustBeAdmin(user);
        if (userManuals.getTitle()== null || userManuals.getUrl() == null){
            throw new WebApplicationException("missing title or url", Response.Status.BAD_REQUEST);
        }

        userManuals.setTitle(userManuals.getTitle().trim());
        userManuals.setUrl(UrlUtil.processURL(userManuals.getUrl()));
        userManuals.setTextUrl(UrlUtil.processURL(userManuals.getTextUrl()));
        userManuals.setCreatedAt(DateTime.now());
        userManuals.setCreatedBy(user);

        return userManualsDao.createOrUpdate(userManuals);
    }

    public void deleteUserManual(UserManuals userManuals, User user) {
        mustBeAdmin(user);
        UserManuals dbUserManual = userManualsDao.findById(userManuals.getId());
        if (dbUserManual != null){
            userManualsDao.remove(dbUserManual);
        }
    }
}
