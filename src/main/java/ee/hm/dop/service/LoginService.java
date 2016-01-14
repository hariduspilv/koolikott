package ee.hm.dop.service;

import static java.lang.String.format;

import java.math.BigInteger;
import java.security.SecureRandom;

import javax.inject.Inject;
import javax.xml.soap.SOAPException;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
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
import ee.hm.dop.model.mobileid.MobileIDSecurityCodes;

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

    private SecureRandom random = new SecureRandom();

    /**
     * Try to log in with the given id code and if that fails, create a new user
     * and log in with that.
     */
    public AuthenticatedUser logIn(LoginForm loginForm) {
        AuthenticatedUser authenticatedUser = doLogin(loginForm);

        if (authenticatedUser != null) {
            logger.info(format("User %s with id %s logged in.", authenticatedUser.getUser().getUsername(),
                    loginForm.idCode));
        } else {
            logger.info(format("User with id %s could not log in, trying to create account. ", loginForm.idCode));

            // Create new user account
            userService.create(loginForm.idCode, loginForm.name, loginForm.surname);
            authenticatedUser = doLogin(loginForm);

            if (authenticatedUser == null) {
                throw new RuntimeException(format(
                        "User with id %s tried to log in after creating account, but failed.", loginForm.idCode));
            }

            authenticatedUser.setFirstLogin(true);
            logger.info(format("User %s with id %s logged in for the first time.", authenticatedUser.getUser()
                    .getUsername(), loginForm.idCode));
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

        LoginForm loginForm = new LoginForm(authenticationState.getIdCode(), authenticationState.getName(),
                authenticationState.getSurname());

        AuthenticatedUser authenticatedUser = logIn(loginForm);

        authenticationStateDAO.delete(authenticationState);

        return authenticatedUser;
    }

    private AuthenticatedUser doLogin(LoginForm loginForm) {
        User user = userService.getUserByIdCode(loginForm.idCode);
        if (user == null) {
            return null;
        }

        return createAuthenticatedUser(user, loginForm);
    }

    private AuthenticatedUser createAuthenticatedUser(User user, LoginForm loginForm) {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setUser(user);
        authenticatedUser.setAffiliations(loginForm.affiliations);
        authenticatedUser.setHomeOrganization(loginForm.homeOrganization);
        authenticatedUser.setMails(loginForm.mails);
        authenticatedUser.setScopedAffiliations(loginForm.scopedAffiliations);

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

    public static final class LoginForm {

        private String idCode;
        private String name;
        private String surname;
        private String homeOrganization;
        private String mails;
        private String affiliations;
        private String scopedAffiliations;

        public LoginForm(String idCode, String name, String surname) {
            this.idCode = idCode;
            this.name = name;
            this.surname = surname;
        }

        public LoginForm withHomeOrganization(String homeOrganization) {
            this.homeOrganization = homeOrganization;
            return this;
        }

        public LoginForm withMails(String mails) {
            this.mails = mails;
            return this;
        }

        public LoginForm withAffiliations(String affiliations) {
            this.affiliations = affiliations;
            return this;
        }

        public LoginForm withScopedAffiliations(String scopedAffiliations) {
            this.scopedAffiliations = scopedAffiliations;
            return this;
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }

        @Override
        public boolean equals(Object obj) {
            return EqualsBuilder.reflectionEquals(this, obj);
        }
    }
}
