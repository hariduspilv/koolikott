package ee.hm.dop.service;

import com.google.inject.Inject;

import ee.hm.dop.dao.AuthenticatedUserDAO;
import ee.hm.dop.model.AuthenticatedUser;

public class AuthenticatedUserService {

    @Inject
    private AuthenticatedUserDAO authenticatedUserDAO;

    public AuthenticatedUser getAuthenticatedUserByToken(String token) {
        return authenticatedUserDAO.findAuthenticatedUserByToken(token);
    }
}
