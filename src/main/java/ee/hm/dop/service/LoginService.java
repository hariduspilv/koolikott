package ee.hm.dop.service;

import java.math.BigInteger;
import java.security.SecureRandom;

import javax.inject.Inject;

import ee.hm.dop.dao.AuthenticatedUserDAO;
import ee.hm.dop.exceptions.DuplicateTokenException;
import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.User;

/**
 * Created by mart.laus on 13.08.2015.
 */
public class LoginService {

    @Inject
    private UserService userService;

    @Inject
    private AuthenticatedUserDAO authenticatedUserDAO;

    private SecureRandom random = new SecureRandom();

    public AuthenticatedUser logIn(String idCode) {
        User user = getUser(idCode);
        if(user == null) {
            return null;
        }

        AuthenticatedUser authenticatedUser = getAuthenticatedUser(user);

        createAuthenticatedUser(authenticatedUser);
        return authenticatedUser;
    }

    private User getUser(String idCode) {
        return userService.getUserByIdCode(idCode);
    }

    private AuthenticatedUser getAuthenticatedUser(User user) {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setUser(user);
        authenticatedUser.setToken(new BigInteger(130, random).toString(32));
        return authenticatedUser;
    }

    private void createAuthenticatedUser(AuthenticatedUser authenticatedUser) {
        try{
            authenticatedUserDAO.createAuthenticatedUser(authenticatedUser);
        } catch (DuplicateTokenException e) {
            authenticatedUser.setToken(new BigInteger(130, random).toString(32));
            authenticatedUserDAO.createAuthenticatedUser(authenticatedUser);
        }
    }
}
