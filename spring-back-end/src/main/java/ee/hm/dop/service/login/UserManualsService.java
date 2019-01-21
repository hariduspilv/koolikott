package ee.hm.dop.service.login;

import ee.hm.dop.dao.UserManualsDao;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserManuals;
import ee.hm.dop.utils.UrlUtil;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

import static ee.hm.dop.utils.UserUtil.mustBeAdmin;

@Service
@Transactional
public class UserManualsService {

    @Inject
    private UserManualsDao userManualsDao;

    public List<UserManuals> findAllUserManuals() {
        return userManualsDao.findAll();
    }

    public UserManuals save(UserManuals userManuals, User user) {
        mustBeAdmin(user);
        if (userManuals.getTitle() == null || userManuals.getUrl() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "missing title or url");
        }

        userManuals.setTitle(userManuals.getTitle().trim());
        userManuals.setUrl(UrlUtil.processURL(userManuals.getUrl()));
        userManuals.setTextUrl(UrlUtil.processURL(userManuals.getTextUrl()));
        userManuals.setCreatedAt(LocalDateTime.now());
        userManuals.setCreatedBy(user);

        return userManualsDao.createOrUpdate(userManuals);
    }

    public void deleteUserManual(UserManuals userManuals, User user) {
        mustBeAdmin(user);
        UserManuals dbUserManual = userManualsDao.findById(userManuals.getId());
        if (dbUserManual != null) {
            userManualsDao.remove(dbUserManual);
        }
    }
}
