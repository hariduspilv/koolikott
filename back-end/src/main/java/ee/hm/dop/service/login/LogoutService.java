package ee.hm.dop.service.login;

import javax.inject.Inject;

import ee.hm.dop.dao.AuthenticatedUserDao;
import ee.hm.dop.model.AuthenticatedUser;

public class LogoutService {

    @Inject
    private AuthenticatedUserDao authenticatedUserDao;

    public void logout(AuthenticatedUser authenticatedUser) {
        if (authenticatedUser != null) {
            authenticatedUserDao.delete(authenticatedUser);
        }
    }
}
