package ee.hm.dop.service;

import static java.lang.String.format;

import java.math.BigInteger;
import java.security.SecureRandom;

import javax.inject.Inject;
import javax.xml.soap.SOAPException;

import ee.hm.dop.dao.AuthenticatedUserDAO;
import ee.hm.dop.dao.AuthenticationStateDAO;
import ee.hm.dop.exceptions.DuplicateTokenException;
import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.AuthenticationState;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.User;
import ee.hm.dop.model.ehis.Person;
import ee.hm.dop.model.mobileid.MobileIDSecurityCodes;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginService {

    private static final int MILLISECONDS_AUTHENTICATIONSTATE_IS_VALID_FOR = 5 * 60 * 1000;

    private static Logger logger = LoggerFactory.getLogger(LoginService.class);

    @Inject
    private UserService userService;

    @Inject
    private MobileIDLoginService mobileIDLoginService;

    @Inject
    private AuthenticatedUserDAO authenticatedUserDAO;

    @Inject
    private AuthenticationStateDAO authenticationStateDAO;

    @Inject
    private EhisSOAPService ehisSOAPService;

    private SecureRandom random = new SecureRandom();

    /**
     * Try to log in with the given id code and if that fails, create a new user
     * and log in with that.
     */
    public AuthenticatedUser logIn(String idCode, String name, String surname) {
        AuthenticatedUser authenticatedUser = login(idCode);

        if (authenticatedUser != null) {
            logger.info(format("User %s with id %s logged in.", authenticatedUser.getUser().getUsername(), idCode));
        } else {
            logger.info(format("User with id %s could not log in, trying to create account. ", idCode));

            // Create new user account
            userService.create(idCode, name, surname);
            authenticatedUser = login(idCode);

            if (authenticatedUser == null) {
                throw new RuntimeException(format(
                        "User with id %s tried to log in after creating account, but failed.", idCode));
            }

            authenticatedUser.setFirstLogin(true);
            logger.info(format("User %s with id %s logged in for the first time.", authenticatedUser.getUser()
                    .getUsername(), idCode));
        }

        return authenticatedUser;
    }

    /**
     * Log in (or create an user and log in) using data in an
     * authenticationState.
     */
    public AuthenticatedUser logIn(AuthenticationState authenticationState) {
        if (authenticationState == null) {
            return null;
        }

        // Make sure the token is not expired
        Interval interval = new Interval(authenticationState.getCreated(), new DateTime());
        Duration duration = new Duration(MILLISECONDS_AUTHENTICATIONSTATE_IS_VALID_FOR);
        if (interval.toDuration().isLongerThan(duration)) {
            authenticationStateDAO.delete(authenticationState);
            return null;
        }

        AuthenticatedUser authenticatedUser = logIn(authenticationState.getIdCode(), authenticationState.getName(),
                authenticationState.getSurname());

        authenticationStateDAO.delete(authenticationState);

        return authenticatedUser;
    }

    private AuthenticatedUser login(String idCode) {
        User user = userService.getUserByIdCode(idCode);
        if (user == null) {
            return null;
        }

        return createAuthenticatedUser(user);
    }

    private AuthenticatedUser createAuthenticatedUser(User user) {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setUser(user);

        Person person = ehisSOAPService.getPersonInformation(user.getIdCode());
        if (person != null) {
            authenticatedUser.setPerson(person);
        }
        authenticatedUser.setPerson(person);

        return createAuthenticatedUser(authenticatedUser);
    }

    private AuthenticatedUser createAuthenticatedUser(AuthenticatedUser authenticatedUser) {
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

    public MobileIDSecurityCodes mobileIDAuthenticate(String phoneNumber, String idCode, Language language)
            throws Exception {
        return mobileIDLoginService.authenticate(phoneNumber, idCode, language);
    }

    public AuthenticatedUser validateMobileIDAuthentication(String token) throws SOAPException {
        if (!mobileIDLoginService.isAuthenticated(token)) {
            logger.info("Authentication not valid.");
            return null;
        }
        AuthenticationState authenticationState = authenticationStateDAO.findAuthenticationStateByToken(token);
        return logIn(authenticationState);
    }
}
