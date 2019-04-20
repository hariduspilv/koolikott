package ee.hm.dop.service.login;

import ee.hm.dop.dao.AgreementDao;
import ee.hm.dop.dao.AuthenticationStateDao;
import ee.hm.dop.dao.UserAgreementDao;
import ee.hm.dop.dao.UserEmailDao;
import ee.hm.dop.model.Agreement;
import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.AuthenticationState;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserEmail;
import ee.hm.dop.model.User_Agreement;
import ee.hm.dop.model.ehis.Person;
import ee.hm.dop.model.enums.LoginFrom;
import ee.hm.dop.service.ehis.IEhisSOAPService;
import ee.hm.dop.service.login.dto.UserStatus;
import ee.hm.dop.service.useractions.SessionService;
import ee.hm.dop.service.useractions.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static ee.hm.dop.service.login.dto.UserStatus.loggedIn;
import static ee.hm.dop.service.login.dto.UserStatus.missingPermissionsExistingUser;
import static ee.hm.dop.service.login.dto.UserStatus.missingPermissionsNewUser;
import static java.lang.String.format;
import static java.time.LocalDateTime.*;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Service
@Transactional
public class LoginService {
    private static final int MILLISECONDS_AUTHENTICATIONSTATE_IS_VALID_FOR = 5 * 60 * 1000;

    private static Logger logger = LoggerFactory.getLogger(LoginService.class);

    @Inject
    private UserService userService;
    @Inject
    private AuthenticationStateService authenticationStateService;
    @Inject
    private AuthenticationStateDao authenticationStateDao;
    @Inject
    private IEhisSOAPService ehisSOAPService;
    @Inject
    private AgreementDao agreementDao;
    @Inject
    private UserAgreementDao userAgreementDao;
    @Inject
    private SessionService sessionService;
    @Inject
    private UserEmailDao userEmailDao;

    public UserStatus login(String idCode, String name, String surname, LoginFrom loginFrom) {
        Agreement latestAgreement = agreementDao.findLatestAgreement();
        if (latestAgreement == null) {
            return loggedIn(finalizeLogin(idCode, name, surname, loginFrom));
        }
        User user = userService.getUserByIdCode(idCode);
        if (user == null) {
            AuthenticationState state = authenticationStateService.save(idCode, name, surname);
            return missingPermissionsNewUser(state.getToken(), latestAgreement.getId(), loginFrom);
        }
        if (userAgreementDao.agreementDoesntExist(user.getId(), latestAgreement.getId())) {
            AuthenticationState state = authenticationStateService.save(idCode, name, surname);
            logger.info(format("User with id %s doesn't have agreement", user.getId()));
            return missingPermissionsExistingUser(state.getToken(), latestAgreement.getId(), loginFrom);
        }

        logger.info(format("User with id %s logged in", user.getId()));
        return loggedIn(authenticate(user, loginFrom));
    }

    public AuthenticatedUser finalizeLogin(UserStatus userStatus) {
        if (userStatus.getLoginFrom() == null) {
            throw new RuntimeException("No login from for token: " + userStatus.getToken());
        }
        AuthenticationState state = authenticationStateDao.findAuthenticationStateByToken(userStatus.getToken());
        if (state == null) {
            return null;
        }

        if (hasExpired(state)) {
            authenticationStateDao.delete(state);
            return null;
        }
        if (userStatus.getAgreementId() == null) {
            throw new RuntimeException("No agreement for token: " + userStatus.getToken());
        }

        User user = getExistingOrNewUser(state);
        UserEmail dbUserEmail = userEmailDao.findByUser(user);
        Agreement agreement = agreementDao.findById(userStatus.getAgreementId());
        boolean agreementDoesntExist = userAgreementDao.agreementDoesntExist(user.getId(), agreement.getId());
        if (agreementDoesntExist) {
            boolean agreed = dbUserEmail != null && isNotEmpty(dbUserEmail.getEmail());
            userAgreementDao.createOrUpdate(createUserAgreement(user, agreement, agreed));
        }

        AuthenticatedUser authenticate = authenticate(user, userStatus.getLoginFrom());
        authenticationStateDao.delete(state);

        logger.info(format("User with id %s finalized login and logged in", user.getId()));
        return authenticate;
    }

    public void rejectAgreement(UserStatus userStatus) {
        AuthenticationState state = authenticationStateDao.findAuthenticationStateByToken(userStatus.getToken());
        if (state == null) {
            return;
        }

        if (hasExpired(state)) {
            authenticationStateDao.delete(state);
            return;
        }
        if (userStatus.getAgreementId() == null) {
            throw new RuntimeException("No agreement for token: " + userStatus.getToken());
        }
        User user = userService.getUserByIdCode(state.getIdCode());
        if (user == null) {
            return;
        }
        Agreement agreement = agreementDao.findById(userStatus.getAgreementId());
        if (userAgreementDao.agreementDoesntExist(user.getId(), agreement.getId())) {
            userAgreementDao.createOrUpdate(createUserAgreement(user, agreement));
        }
    }

    private AuthenticatedUser finalizeLogin(String idCode, String name, String surname, LoginFrom loginFrom) {
        User user = getExistingOrNewUser(idCode, name, surname);
        return authenticate(user, loginFrom);
    }

    private AuthenticatedUser authenticate(User user, LoginFrom loginFrom) {
        Person person = getPerson(user, loginFrom);
        return sessionService.startSession(user, person, loginFrom);
    }

    private Person getPerson(User user, LoginFrom loginFrom) {
        if (loginFrom.isDev()) {
            return new Person();
        }
        return ehisSOAPService.getPersonInformation(user.getIdCode());
    }

    private User getExistingOrNewUser(AuthenticationState state) {
        return getExistingOrNewUser(state.getIdCode(), state.getName(), state.getSurname());
    }

    private User getExistingOrNewUser(String idCode, String firstname, String surname) {
        User existingUser = userService.getUserByIdCode(idCode);
        if (existingUser != null) {
            return existingUser;
        }
        userService.create(idCode, firstname, surname);
        User newUser = userService.getUserByIdCode(idCode);
        if (newUser == null) {
            throw new RuntimeException(format("User with id %s tried to log in after creating account, but failed.", idCode));
        }
        logger.info("System created new user with id {}", newUser.getId());
        newUser.setNewUser(true);
        if (newUser.getUserAgreements() == null) {
            newUser.setUserAgreements(new ArrayList<>());
        }
        return newUser;
    }

    public boolean hasExpired(AuthenticationState state) {
        Duration between = Duration.between(state.getCreated(), now());
        return !between.minusMillis(MILLISECONDS_AUTHENTICATIONSTATE_IS_VALID_FOR).isNegative();
    }

    private User_Agreement createUserAgreement(User user, Agreement agreement) {
        User_Agreement userAgreement = new User_Agreement();
        userAgreement.setUser(user);
        userAgreement.setAgreement(agreement);
        userAgreement.setCreatedAt(now());
        return userAgreement;
    }

    private User_Agreement createUserAgreement(User user, Agreement agreement, boolean agreed) {
        User_Agreement userAgreement = new User_Agreement();
        userAgreement.setUser(user);
        userAgreement.setAgreement(agreement);
        userAgreement.setAgreed(agreed);
        userAgreement.setCreatedAt(now());
        return userAgreement;
    }
}
