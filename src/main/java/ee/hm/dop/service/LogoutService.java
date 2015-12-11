package ee.hm.dop.service;

import javax.inject.Inject;

import ee.hm.dop.dao.AuthenticatedUserDAO;
import ee.hm.dop.model.AuthenticatedUser;

public class LogoutService {

    @Inject
    private AuthenticatedUserDAO authenticatedUserDAO;

    public void logout(AuthenticatedUser authenticatedUser) {
        if (authenticatedUser != null) {
            authenticatedUserDAO.delete(authenticatedUser);
        }
    }
}
