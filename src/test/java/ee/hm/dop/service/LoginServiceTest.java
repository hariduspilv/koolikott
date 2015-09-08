package ee.hm.dop.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
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

        expect(userService.getUserByIdCode(idCode)).andReturn(null).anyTimes();

        replay(userService, authenticatedUserDAO);

        AuthenticatedUser authenticatedUser = loginService.logIn(idCode);

        verify(userService, authenticatedUserDAO);

        assertEquals(null, authenticatedUser);
    }

    @Test
    public void logInSameTokenThreeTimes() throws NoSuchMethodException {
        String idCode = "idCode";
        User user = createMock(User.class);

        expect(userService.getUserByIdCode(idCode)).andReturn(user);
        expect(authenticatedUserDAO.createAuthenticatedUser(EasyMock.anyObject(AuthenticatedUser.class)))
                .andThrow(new DuplicateTokenException()).times(2);

        replay(userService, authenticatedUserDAO, user);

        try {
            loginService.logIn(idCode);
            fail("Exception expected");
        } catch (DuplicateTokenException e) {
            // Everything ok
        }

        verify(userService, authenticatedUserDAO, user);
    }

    @Test
    public void logInDuplicateToken() throws NoSuchMethodException {
        String idCode = "idCode";
        User user = createMock(User.class);
        AuthenticatedUser authenticatedUser = createMock(AuthenticatedUser.class);

        expect(userService.getUserByIdCode(idCode)).andReturn(user);
        expect(authenticatedUserDAO.createAuthenticatedUser(EasyMock.anyObject(AuthenticatedUser.class)))
                .andThrow(new DuplicateTokenException()).times(1);
        expect(authenticatedUserDAO.createAuthenticatedUser(EasyMock.anyObject(AuthenticatedUser.class)))
                .andReturn(authenticatedUser);

        replay(userService, authenticatedUserDAO, user);

        loginService.logIn(idCode);

        verify(userService, authenticatedUserDAO, user);
    }

    @Test
    public void logIn() throws NoSuchMethodException {
        String idCode = "idCode";
        User user = createMock(User.class);
        AuthenticatedUser authenticatedUser = createMock(AuthenticatedUser.class);

        expect(userService.getUserByIdCode(idCode)).andReturn(user);
        expect(authenticatedUserDAO.createAuthenticatedUser(EasyMock.anyObject(AuthenticatedUser.class)))
                .andReturn(authenticatedUser);

        replay(userService, authenticatedUserDAO, user);

        loginService.logIn(idCode);

        verify(userService, authenticatedUserDAO, user);
    }
}
