package ee.hm.dop.service.login;

import javax.inject.Inject;

import ee.hm.dop.dao.AuthenticatedUserDao;
import ee.hm.dop.model.AuthenticatedUser;
import org.joda.time.DateTime;

public class LogoutService {

    @Inject
    private AuthenticatedUserDao authenticatedUserDao;

    public void logout(AuthenticatedUser authenticatedUser) {
        if (authenticatedUser != null) {
            authenticatedUser.setSessionTime(DateTime.now());
            authenticatedUser.setLoggedOut(true);
            authenticatedUserDao.delete(authenticatedUser);
        }
    }
}
