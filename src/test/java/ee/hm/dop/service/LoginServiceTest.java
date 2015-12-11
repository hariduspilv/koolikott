package ee.hm.dop.service;

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

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.IAnswer;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import ee.hm.dop.dao.AuthenticatedUserDAO;
import ee.hm.dop.dao.AuthenticationStateDAO;
import ee.hm.dop.exceptions.DuplicateTokenException;
import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.AuthenticationState;
import ee.hm.dop.model.User;

@RunWith(EasyMockRunner.class)
public class LoginServiceTest {

    @TestSubject
    private LoginService loginService = new LoginService();

    @Mock
    private UserService userService;

    @Mock
    private MobileIDLoginService mobileIDLoginService;

    @Mock
    private AuthenticatedUserDAO authenticatedUserDAO;

    @Mock
    private AuthenticationStateDAO authenticationStateDAO;

    @Test
    public void logInNullUser() {
        String idCode = "idCode";

        expect(userService.getUserByIdCode(idCode)).andReturn(null).anyTimes();

        replayAll();

        AuthenticatedUser authenticatedUser = loginService.logIn(idCode);

        verifyAll();

        assertNull(authenticatedUser);
    }

    @Test
    public void logInSameTokenThreeTimes() throws NoSuchMethodException {
        String idCode = "idCode";
        User user = createMock(User.class);

        expect(userService.getUserByIdCode(idCode)).andReturn(user);
        expect(authenticatedUserDAO.createAuthenticatedUser(EasyMock.anyObject(AuthenticatedUser.class))).andThrow(
                new DuplicateTokenException()).times(2);

        replayAll(user);

        try {
            loginService.logIn(idCode);
            fail("Exception expected");
        } catch (DuplicateTokenException e) {
            // Everything ok
        }

        verifyAll(user);
    }

    @Test
    public void logInDuplicateToken() throws NoSuchMethodException {
        String idCode = "idCode";
        User user = createMock(User.class);
        AuthenticatedUser authenticatedUser = createMock(AuthenticatedUser.class);

        expect(userService.getUserByIdCode(idCode)).andReturn(user);
        expect(authenticatedUserDAO.createAuthenticatedUser(EasyMock.anyObject(AuthenticatedUser.class))).andThrow(
                new DuplicateTokenException()).times(1);
        expect(authenticatedUserDAO.createAuthenticatedUser(EasyMock.anyObject(AuthenticatedUser.class))).andReturn(
                authenticatedUser);

        replayAll(user);

        loginService.logIn(idCode);

        verifyAll(user);
    }

    @Test
    public void logIn() throws NoSuchMethodException {
        String idCode = "idCode";
        User user = createMock(User.class);
        AuthenticatedUser authenticatedUser = createMock(AuthenticatedUser.class);

        expect(userService.getUserByIdCode(idCode)).andReturn(user);
        expect(authenticatedUserDAO.createAuthenticatedUser(EasyMock.anyObject(AuthenticatedUser.class))).andReturn(
                authenticatedUser);

        replayAll(user);

        loginService.logIn(idCode);

        verifyAll(user);
    }

    @Test
    public void logInNullAuthenticationState() {
        AuthenticationState authenticationState = null;

        replayAll();

        AuthenticatedUser returnedAuthenticatedUser = loginService.logIn(authenticationState);

        verifyAll();

        assertNull(returnedAuthenticatedUser);
    }

    @Test
    public void logInExpiredAuthenticationState() {
        AuthenticationState authenticationState = new AuthenticationState();
        authenticationState.setToken("123123123");
        authenticationState.setCreated(new DateTime("2015-01-01T11:12:13.444"));

        authenticationStateDAO.delete(authenticationState);

        replayAll();

        AuthenticatedUser returnedAuthenticatedUser = loginService.logIn(authenticationState);

        verifyAll();

        assertNull(returnedAuthenticatedUser);
    }

    @Test
    public void isMobileIDAuthenticationValid() throws SOAPException {
        String token = "725abc";

        User user = new User();
        user.setIdCode("66677788899");
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
        expect(authenticationStateDAO.findAuthenticationStateByToken(token)).andReturn(authenticationState);
        expect(userService.getUserByIdCode(user.getIdCode())).andReturn(user);
        expectCreateAuthenticatedUser(capturedAuthenticatedUser);
        authenticationStateDAO.delete(authenticationState);

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
        expect(authenticatedUserDAO.createAuthenticatedUser(EasyMock.capture(capturedAuthenticatedUser))).andAnswer(
                new IAnswer<AuthenticatedUser>() {
                    @Override
                    public AuthenticatedUser answer() throws Throwable {
                        return capturedAuthenticatedUser.getValue();
                    }
                });
    }

    private void replayAll(Object... mocks) {
        replay(userService, mobileIDLoginService, authenticatedUserDAO, authenticationStateDAO);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

    private void verifyAll(Object... mocks) {
        verify(userService, mobileIDLoginService, authenticatedUserDAO, authenticationStateDAO);

        if (mocks != null) {
            for (Object object : mocks) {
                verify(object);
            }
        }
    }
}
