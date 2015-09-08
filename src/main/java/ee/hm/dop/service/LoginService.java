package ee.hm.dop.service;

import java.math.BigInteger;
import java.security.SecureRandom;

import javax.inject.Inject;

import ee.hm.dop.dao.AuthenticatedUserDAO;
import ee.hm.dop.exceptions.DuplicateTokenException;
import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.User;

public class LoginService {

    @Inject
    private UserService userService;

    @Inject
    private AuthenticatedUserDAO authenticatedUserDAO;

    private SecureRandom random = new SecureRandom();

    public AuthenticatedUser logIn(String idCode) {
        User user = getUser(idCode);
        if (user == null) {
            return null;
        }

        return createAuthenticatedUser(user);
    }

    private User getUser(String idCode) {
        return userService.getUserByIdCode(idCode);
    }

    private AuthenticatedUser createAuthenticatedUser(User user) {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setUser(user);

        return createAuthenticatedUser(authenticatedUser);
    }

    public AuthenticatedUser createAuthenticatedUser(AuthenticatedUser authenticatedUser){
        authenticatedUser.setToken(new BigInteger(130, random).toString(32));

        AuthenticatedUser returnedAuthenticatedUser;
        try {
            returnedAuthenticatedUser = authenticatedUserDAO.createAuthenticatedUser(authenticatedUser);
        } catch (DuplicateTokenException e) {
            authenticatedUser.setToken(new BigInteger(130, random).toString(32));
            returnedAuthenticatedUser = authenticatedUserDAO.createAuthenticatedUser(authenticatedUser);
        }

        return returnedAuthenticatedUser;
    }
}
