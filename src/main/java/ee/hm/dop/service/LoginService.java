package ee.hm.dop.service;

import java.math.BigInteger;
import java.security.SecureRandom;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.hm.dop.dao.AuthenticatedUserDAO;
import ee.hm.dop.dao.UserDAO;
import ee.hm.dop.exceptions.DuplicateTokenException;
import ee.hm.dop.exceptions.DuplicateUserException;
import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.User;

/**
 * Created by mart.laus on 13.08.2015.
 */
public class LoginService {

    private static final Logger logger = LoggerFactory.getLogger(SolrService.class);

    @Inject
    private UserService userService;

    @Inject
    private AuthenticatedUserDAO authenticatedUserDAO;

    @Inject
    private UserDAO userDAO;

    private SecureRandom random = new SecureRandom();

    public AuthenticatedUser logIn(String idCode) {
        User user = getUser(idCode);
        if (user == null) {
            return null;
        }

        AuthenticatedUser authenticatedUser = getAuthenticatedUser(user);

        createAuthenticatedUser(authenticatedUser);
        return authenticatedUser;
    }

    public synchronized void createUser(String idCode, String name, String surname) {
        User user = new User();
        user.setIdCode(idCode);
        user.setName(name);
        user.setSurname(surname);
        user.setUsername(getNextAvailableUsername(name, surname));

        createUser(user);
    }

    private User getUser(String idCode) {
        return userService.getUserByIdCode(idCode);
    }

    private void createUser(User user) {
        try {
            userService.createUser(user);
        } catch (DuplicateUserException e) {
            logger.error(e.getMessage());
        }
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

    public String getNextAvailableUsername(String name, String surname) {
        Long count = userDAO.countUsersWithSameFullName(name, surname);
        String username = name.toLowerCase() + "." + surname.toLowerCase();
        if (count == 0) {
            return username;
        }
        return username + String.valueOf(count + 1);
    }

}
