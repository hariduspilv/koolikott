package ee.hm.dop.service.login;

import ee.hm.dop.dao.AgreementDao;
import ee.hm.dop.dao.AuthenticationStateDao;
import ee.hm.dop.dao.UserDao;
import ee.hm.dop.model.Agreement;
import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.AuthenticationState;
import ee.hm.dop.model.User;
import ee.hm.dop.model.ehis.Person;
import ee.hm.dop.service.ehis.IEhisSOAPService;
import ee.hm.dop.service.login.dto.UserStatus;
import ee.hm.dop.service.useractions.AuthenticatedUserService;
import ee.hm.dop.service.useractions.UserService;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static ee.hm.dop.service.login.dto.UserStatus.loggedIn;
import static ee.hm.dop.service.login.dto.UserStatus.missingPermissions;
import static java.lang.String.format;

public class LoginNewService {
    private static final int MILLISECONDS_AUTHENTICATIONSTATE_IS_VALID_FOR = 5 * 60 * 1000;

    private static Logger logger = LoggerFactory.getLogger(LoginService.class);

    @Inject
    private UserService userService;
    @Inject
    private AuthenticationStateService authenticationStateService;
    @Inject
    private AuthenticatedUserService authenticatedUserService;
    @Inject
    private AuthenticationStateDao authenticationStateDao;
    @Inject
    private IEhisSOAPService ehisSOAPService;
    @Inject
    private AgreementDao agreementDao;
    @Inject
    private UserDao userDao;

    public UserStatus login(String idCode, String name, String surname) {
        Agreement latestAgreement = agreementDao.findLatestAgreement();
        if (latestAgreement == null) {
            return loggedIn(finalizeLogin(idCode, name, surname));
        }
        User user = userService.getUserByIdCode(idCode);
        if (user == null) {
            AuthenticationState state = authenticationStateService.save(idCode, name, surname);
            return missingPermissions(state.getToken(), latestAgreement.getId());
        }
        List<Long> agreementIds = user.getAgreements().stream().map(Agreement::getId).collect(Collectors.toList());
        if (!agreementIds.contains(latestAgreement.getId())) {
            AuthenticationState state = authenticationStateService.save(idCode, name, surname);
            return missingPermissions(state.getToken(), latestAgreement.getId());
        }
        return loggedIn(authenticate(user));
    }

    public AuthenticatedUser finalizeLogin(UserStatus userStatus) {
        AuthenticationState state = authenticationStateDao.findAuthenticationStateByToken(userStatus.getToken());
        if (state == null) {
            return null;
        }

        if (hasExpired(state)) {
            authenticationStateDao.delete(state);
            return null;
        }

        User user = getExistingOrNewUser(state);
        if (userStatus.getAgreementId() != null) {
            Agreement agreement = agreementDao.findById(userStatus.getAgreementId());
            user.getAgreements().add(agreement);
            userDao.createOrUpdate(user);
        }
        AuthenticatedUser authenticate = authenticate(user);
        authenticationStateDao.delete(state);
        return authenticate;
    }

    /**
     * used for backwards compitability
     */
    @Deprecated
    private AuthenticatedUser finalizeLogin(String idCode, String name, String surname) {
        return authenticate(getExistingOrNewUser(idCode, name, surname));
    }

    private AuthenticatedUser authenticate(User user) {
        Person person = ehisSOAPService.getPersonInformation(user.getIdCode());
        return authenticatedUserService.save(new AuthenticatedUser(user, person));
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
        newUser.setNewUser(true);
        return newUser;
    }

    private boolean hasExpired(AuthenticationState state) {
        Interval interval = new Interval(state.getCreated(), new DateTime());
        Duration duration = new Duration(MILLISECONDS_AUTHENTICATIONSTATE_IS_VALID_FOR);
        return interval.toDuration().isLongerThan(duration);
    }
}
