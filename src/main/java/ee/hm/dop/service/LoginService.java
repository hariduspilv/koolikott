package ee.hm.dop.service;

import java.math.BigInteger;
import java.security.SecureRandom;

import javax.inject.Inject;

import ee.hm.dop.dao.AuthenticatedUserDAO;
import ee.hm.dop.exceptions.DuplicateTokenException;
import ee.hm.dop.exceptions.DuplicateUserException;
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

    public AuthenticatedUser logInWithExistingUser(String idCode) {
        User user = getUser(idCode);
        if (user == null) {
            return null;
        }

        AuthenticatedUser authenticatedUser = getAuthenticatedUser(user);

        createAuthenticatedUser(authenticatedUser);
        return authenticatedUser;
    }

    public AuthenticatedUser logIn(String idCode, String name, String surname) {
        User user = getUser(idCode);
        if (user == null) {
            User newUser = getNewUser(idCode, name, surname);
            createUser(newUser);
        }

        return logInWithExistingUser(idCode);
    }

    private User getUser(String idCode) {
        return userService.getUserByIdCode(idCode);
    }

    public void createUser(User user) {
        try {
            userService.createUser(user);
        } catch (DuplicateUserException e) {
            user.setUsername(userService.getNextAvailableUsername(user.getName(), user.getSurname()));
            userService.createUser(user);
        }
    }

    private User getNewUser(String idCode, String name, String surname) {
        User user = new User();
        user.setIdCode(idCode);
        user.setName(name);
        user.setSurname(surname);
        user.setUsername(userService.getNextAvailableUsername(name, surname));
        return user;
    }

    private AuthenticatedUser getAuthenticatedUser(User user) {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setUser(user);
        authenticatedUser.setToken(new BigInteger(130, random).toString(32));
        return authenticatedUser;
    }

    private void createAuthenticatedUser(AuthenticatedUser authenticatedUser) {
        try {
            authenticatedUserDAO.createAuthenticatedUser(authenticatedUser);
        } catch (DuplicateTokenException e) {
            authenticatedUser.setToken(new BigInteger(130, random).toString(32));
            authenticatedUserDAO.createAuthenticatedUser(authenticatedUser);
        }
    }
}
