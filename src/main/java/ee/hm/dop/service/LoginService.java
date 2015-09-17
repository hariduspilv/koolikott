package ee.hm.dop.service;

import static java.lang.String.format;

import java.math.BigInteger;
import java.security.SecureRandom;

import javax.inject.Inject;
import javax.xml.soap.SOAPException;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.hm.dop.dao.AuthenticatedUserDAO;
import ee.hm.dop.dao.AuthenticationStateDAO;
import ee.hm.dop.exceptions.DuplicateTokenException;
import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.AuthenticationState;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.User;
import ee.hm.dop.model.mobileid.MobileAuthResponse;

public class LoginService {

    private static final int MINUTES_AUTHENTICATIONSTATE_IS_VALID_FOR = 5;

    private static Logger logger = LoggerFactory.getLogger(LoginService.class);

    @Inject
    private UserService userService;

    @Inject
    private MobileIDLoginService mobileIDLoginService;

    @Inject
    private AuthenticatedUserDAO authenticatedUserDAO;

    @Inject
    private AuthenticationStateDAO authenticationStateDAO;

    private SecureRandom random = new SecureRandom();

    /**
     * Try to log in with the given id code and if that fails, create a new user
     * and log in with that.
     */
    public AuthenticatedUser logInOrCreateUser(String idCode, String name, String surname) {
        AuthenticatedUser authenticatedUser = logIn(idCode);

        if (authenticatedUser != null) {
            logger.info(format("User %s with id %s logged in.", authenticatedUser.getUser().getUsername(), idCode));
        } else {
            logger.info(format("User with id %s could not log in, trying to create account. ", idCode));

            // Create new user account
            userService.create(idCode, name, surname);
            authenticatedUser = logIn(idCode);

            if (authenticatedUser == null) {
                throw new RuntimeException(
                        format("User with id %s tried to log in after creating account, but failed.", idCode));
            }

            authenticatedUser.setFirstLogin(true);
            logger.info(format("User %s with id %s logged in for the first time.",
                    authenticatedUser.getUser().getUsername(), idCode));
        }

        return authenticatedUser;
    }

    /**
     * Convenience method for logging in or creating an user using a token that
     * references user data in an authenticationState.
     */
    public AuthenticatedUser logInOrCreateUser(String authenticationStateToken) {
        AuthenticationState authenticationState = authenticationStateDAO
                .findAuthenticationStateByToken(authenticationStateToken);
        if (authenticationState == null) {
            return null;
        }

        // Make sure the token is not expired
        Interval interval = new Interval(authenticationState.getCreated(), new DateTime());
        Duration duration = new Duration(MINUTES_AUTHENTICATIONSTATE_IS_VALID_FOR * 60 * 1000);
        if (interval.toDuration().isLongerThan(duration)) {
            authenticationStateDAO.delete(authenticationState);
            return null;
        }

        AuthenticatedUser authenticatedUser = logInOrCreateUser(authenticationState.getIdCode(),
                authenticationState.getName(), authenticationState.getSurname());

        authenticationStateDAO.delete(authenticationState);

        return authenticatedUser;
    }

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

    public AuthenticatedUser createAuthenticatedUser(AuthenticatedUser authenticatedUser) {
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

    public MobileAuthResponse mobileIDAuthenticate(String phoneNumber, String idCode, Language language)
            throws Exception {
        return mobileIDLoginService.authenticate(phoneNumber, idCode, language);
    }

    public AuthenticatedUser isMobileIDAuthenticationValid(String token) throws SOAPException {
        if (mobileIDLoginService.isAuthenticated(token)) {
            return logInOrCreateUser(token);
        }
        throw new RuntimeException("Authentication not valid.");
    }

}
