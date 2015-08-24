package ee.hm.dop.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import ee.hm.dop.dao.AuthenticatedUserDAO;
import ee.hm.dop.exceptions.DuplicateTokenException;
import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.User;

/**
 * Created by mart.laus on 13.08.2015.
 */

@RunWith(EasyMockRunner.class)
public class LoginServiceTest {

    @TestSubject
    private LoginService loginService = new LoginService();

    @Mock
    private UserService userService;

    @Mock
    private AuthenticatedUserDAO authenticatedUserDAO;

    @Test
    public void logInNullUser() {
        String idCode = "idCode";
        String name = "John";
        String surname = "Smith";

        expect(userService.getUserByIdCode(idCode)).andReturn(null).anyTimes();
        expect(userService.getNextAvailableUsername(name, surname)).andReturn(name + "." + surname);
        userService.createUser(EasyMock.anyObject(User.class));
        expectLastCall();

        replay(userService, authenticatedUserDAO);

        AuthenticatedUser authenticatedUser = loginService.logIn(idCode, name, surname);

        verify(userService, authenticatedUserDAO);

        assertEquals(null, authenticatedUser);
    }

    @Test
    public void logInSameTokenThreeTimes() throws NoSuchMethodException {
        String idCode = "idCode";
        String name = "John";
        String surname = "Smith";
        User user = createMock(User.class);

        expect(userService.getUserByIdCode(idCode)).andReturn(user).times(2);
        authenticatedUserDAO.createAuthenticatedUser(EasyMock.anyObject(AuthenticatedUser.class));
        expectLastCall().andThrow(new DuplicateTokenException()).times(2);

        replay(userService, authenticatedUserDAO, user);

        try {
            loginService.logIn(idCode, name, surname);
            fail("Exception expected");
        } catch (DuplicateTokenException e) {
            // Everything ok
        }

        verify(userService, authenticatedUserDAO, user);
    }

    @Test
    public void logInDuplicateToken() throws NoSuchMethodException {
        String idCode = "idCode";
        String name = "John";
        String surname = "Smith";
        User user = createMock(User.class);

        expect(userService.getUserByIdCode(idCode)).andReturn(user).times(2);
        authenticatedUserDAO.createAuthenticatedUser(EasyMock.anyObject(AuthenticatedUser.class));
        expectLastCall().andThrow(new DuplicateTokenException()).times(1);
        authenticatedUserDAO.createAuthenticatedUser(EasyMock.anyObject(AuthenticatedUser.class));

        replay(userService, authenticatedUserDAO, user);

        loginService.logIn(idCode, name, surname);

        verify(userService, authenticatedUserDAO, user);
    }

    @Test
    public void logIn() throws NoSuchMethodException {
        String idCode = "idCode";
        String name = "John";
        String surname = "Smith";
        User user = createMock(User.class);

        expect(userService.getUserByIdCode(idCode)).andReturn(user).times(2);
        authenticatedUserDAO.createAuthenticatedUser(EasyMock.anyObject(AuthenticatedUser.class));
        expectLastCall();

        replay(userService, authenticatedUserDAO, user);

        loginService.logIn(idCode, name, surname);

        verify(userService, authenticatedUserDAO, user);
    }
}
