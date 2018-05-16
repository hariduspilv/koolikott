package ee.hm.dop.service.login;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.newCapture;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import javax.xml.soap.SOAPException;

import ee.hm.dop.dao.AuthenticatedUserDao;
import ee.hm.dop.dao.AuthenticationStateDao;
import ee.hm.dop.service.ehis.IEhisSOAPService;
import ee.hm.dop.service.useractions.UserService;
import ee.hm.dop.utils.exceptions.DuplicateTokenException;
import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.AuthenticationState;
import ee.hm.dop.model.User;
import ee.hm.dop.model.ehis.Person;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EasyMockRunner.class)
public class LoginServiceTest {

    public static final String ID_CODE = "idCode";
    public static final String TOKEN = "123";
    @TestSubject
    private LoginService loginService = new LoginService();
    @Mock
    private UserService userService;
    @Mock
    private MobileIDLoginService mobileIDLoginService;
    @Mock
    private AuthenticatedUserDao authenticatedUserDao;
    @Mock
    private AuthenticationStateDao authenticationStateDao;
    @Mock
    private IEhisSOAPService ehisSOAPService;
    @Mock
    private TokenGenerator tokenGenerator;

    @Test
    public void logIn() throws NoSuchMethodException {
        String idCode = ID_CODE;
        User user = createMock(User.class);
        AuthenticatedUser authenticatedUser = createMock(AuthenticatedUser.class);

        expect(userService.getUserByIdCode(idCode)).andReturn(user);
        expect(authenticatedUserDao.createAuthenticatedUser(EasyMock.anyObject(AuthenticatedUser.class))).andReturn(
                authenticatedUser);

        expectTokenGenerator();
        expect(authenticatedUser.getUser()).andReturn(user);
        expect(user.getUsername()).andReturn("username");

        expect(user.getIdCode()).andReturn(idCode);
        expect(user.isNewUser()).andReturn(false);
        expect(ehisSOAPService.getPersonInformation(idCode)).andReturn(null);

        replayAll(user, authenticatedUser);

        loginService.login(idCode, null, null);

        verifyAll(user, authenticatedUser);
    }

    private void expectTokenGenerator() {
        expect(tokenGenerator.secureToken()).andReturn(TOKEN).anyTimes();
    }

    @Test
    public void logInFirstTime() {
        expect(userService.getUserByIdCode(ID_CODE)).andReturn(null);
        User user = createMock(User.class);
        expect(userService.create(ID_CODE, null, null)).andReturn(user);
        expect(userService.getUserByIdCode(ID_CODE)).andReturn(user);

        AuthenticatedUser authenticatedUserMock = createMock(AuthenticatedUser.class);
        expect(authenticatedUserDao.createAuthenticatedUser(anyObject(AuthenticatedUser.class))).andReturn(
                authenticatedUserMock);
        authenticatedUserMock.setFirstLogin(true);
        expect(authenticatedUserMock.getUser()).andReturn(user);
        expect(user.getUsername()).andReturn("firstTimeLoginUser");

        expectTokenGenerator();
        expect(user.isNewUser()).andReturn(true);
        expect(user.getIdCode()).andReturn(ID_CODE);
        Person person = createMock(Person.class);
        expect(ehisSOAPService.getPersonInformation(ID_CODE)).andReturn(person);

        replayAll(user, authenticatedUserMock, person);

        AuthenticatedUser authenticatedUser = loginService.login(ID_CODE, null, null);

        verifyAll(user, authenticatedUserMock, person);

        assertNotNull(authenticatedUser);
        assertSame(authenticatedUserMock, authenticatedUser);
    }

    @Test
    public void logInSameTokenThreeTimes() throws NoSuchMethodException {
        String idCode = ID_CODE;
        User user = createMock(User.class);

        expect(userService.getUserByIdCode(idCode)).andReturn(user);
        expect(authenticatedUserDao.createAuthenticatedUser(EasyMock.anyObject(AuthenticatedUser.class))).andThrow(
                new DuplicateTokenException()).times(2);

        expect(user.getIdCode()).andReturn(idCode);
        expect(user.isNewUser()).andReturn(false);
        expect(ehisSOAPService.getPersonInformation(idCode)).andReturn(null);
        expectTokenGenerator();

        replayAll(user);

        try {
            loginService.login(idCode, null, null);
            fail("Exception expected");
        } catch (DuplicateTokenException e) {
            // Everything ok
        }

        verifyAll(user);
    }

    @Test
    public void logInDuplicateToken() throws NoSuchMethodException {
        String idCode = ID_CODE;
        User user = createMock(User.class);
        AuthenticatedUser authenticatedUser = createMock(AuthenticatedUser.class);

        expect(userService.getUserByIdCode(idCode)).andReturn(user);
        expect(authenticatedUserDao.createAuthenticatedUser(EasyMock.anyObject(AuthenticatedUser.class))).andThrow(
                new DuplicateTokenException()).times(1);
        expect(authenticatedUserDao.createAuthenticatedUser(EasyMock.anyObject(AuthenticatedUser.class))).andReturn(
                authenticatedUser);

        expect(authenticatedUser.getUser()).andReturn(user);
        expect(user.getUsername()).andReturn("username");
        expect(user.isNewUser()).andReturn(false);
        expect(user.getIdCode()).andReturn(idCode);
        expect(ehisSOAPService.getPersonInformation(idCode)).andReturn(null);
        expectTokenGenerator();

        replayAll(user, authenticatedUser);

        loginService.login(idCode, null, null);

        verifyAll(user, authenticatedUser);
    }

    @Test
    public void logInNullAuthenticationState() {
        AuthenticationState authenticationState = null;

        replayAll();

        AuthenticatedUser returnedAuthenticatedUser = loginService.login(authenticationState);

        verifyAll();

        assertNull(returnedAuthenticatedUser);
    }

    @Test
    public void logInExpiredAuthenticationState() {
        AuthenticationState authenticationState = new AuthenticationState();
        authenticationState.setToken("123123123");
        authenticationState.setCreated(new DateTime("2015-01-01T11:12:13.444"));

        authenticationStateDao.delete(authenticationState);

        replayAll();

        AuthenticatedUser returnedAuthenticatedUser = loginService.login(authenticationState);

        verifyAll();

        assertNull(returnedAuthenticatedUser);
    }

    @Test
    public void isMobileIDAuthenticationValid() throws SOAPException {
        String token = "725abc";

        User user = new User();
        String idCode = "66677788899";
        user.setIdCode(idCode);
        user.setName("Jaan");
        user.setSurname("Sepp");

        AuthenticationState authenticationState = new AuthenticationState();
        authenticationState.setToken(token);
        authenticationState.setIdCode(user.getIdCode());
        authenticationState.setName(user.getName());
        authenticationState.setSurname(user.getSurname());
        authenticationState.setCreated(new DateTime());

        Capture<AuthenticatedUser> capturedAuthenticatedUser = newCapture();

        expect(mobileIDLoginService.isAuthenticated(token)).andReturn(true);
        expect(authenticationStateDao.findAuthenticationStateByToken(token)).andReturn(authenticationState);
        expect(userService.getUserByIdCode(user.getIdCode())).andReturn(user);
        expectCreateAuthenticatedUser(capturedAuthenticatedUser);
        authenticationStateDao.delete(authenticationState);

        expect(ehisSOAPService.getPersonInformation(idCode)).andReturn(null);
        expectTokenGenerator();

        replayAll();

        AuthenticatedUser returnedAuthenticatedUser = loginService.validateMobileIDAuthentication(token);

        verifyAll();

        assertSame(capturedAuthenticatedUser.getValue(), returnedAuthenticatedUser);
        assertEquals(user.getIdCode(), returnedAuthenticatedUser.getUser().getIdCode());
        assertEquals(user.getName(), returnedAuthenticatedUser.getUser().getName());
        assertEquals(user.getSurname(), returnedAuthenticatedUser.getUser().getSurname());
        assertNotNull(returnedAuthenticatedUser.getToken());
    }

    @Test
    public void isMobileIDAuthenticationValidInvalid() throws SOAPException {
        String token = "1212abc";

        expect(mobileIDLoginService.isAuthenticated(token)).andReturn(false);

        replayAll();

        AuthenticatedUser returnedAuthenticatedUser = null;
        try {
            returnedAuthenticatedUser = loginService.validateMobileIDAuthentication(token);
        } catch (RuntimeException e) {
            assertEquals("Authentication not valid.", e.getMessage());
        }

        verifyAll();

        assertNull(returnedAuthenticatedUser);
    }

    private void expectCreateAuthenticatedUser(Capture<AuthenticatedUser> capturedAuthenticatedUser) {
        expect(authenticatedUserDao.createAuthenticatedUser(EasyMock.capture(capturedAuthenticatedUser))).andAnswer(
                capturedAuthenticatedUser::getValue);
    }

    private void replayAll(Object... mocks) {
        replay(userService, mobileIDLoginService, authenticatedUserDao, authenticationStateDao, ehisSOAPService, tokenGenerator);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

    private void verifyAll(Object... mocks) {
        verify(userService, mobileIDLoginService, authenticatedUserDao, authenticationStateDao, ehisSOAPService, tokenGenerator);

        if (mocks != null) {
            for (Object object : mocks) {
                verify(object);
            }
        }
    }
}
