package ee.hm.dop.service;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import ee.hm.dop.dao.UserDAO;
import ee.hm.dop.model.User;

@RunWith(EasyMockRunner.class)
public class UserServiceTest {

    @TestSubject
    private UserService userService = new UserService();

    @Mock
    private UserDAO userDAO;

    @Test
    public void create() throws Exception {
        String idCode = "idCode";
        String name = "John";
        String surname = "Smith";

        expect(userDAO.countUsersWithSameFullName(name, surname)).andReturn(0L);
        expect(userDAO.update(EasyMock.anyObject(User.class))).andReturn(new User());

        replay(userDAO);

        User user = userService.create(idCode, name, surname);

        verify(userDAO);

        assertNotNull(user);
    }

    @Test
    public void generateUsername() {
        String name = " John\tSmith ";
        String surname = " Second  IV ";
        Long count = 0L;
        String expectedUsername = "john.smith.second.iv";
        expect(userDAO.countUsersWithSameFullName(name, surname)).andReturn(count);

        replay(userDAO);

        String nextUsername = userService.generateUsername(name, surname);

        verify(userDAO);

        assertEquals(expectedUsername, nextUsername);
    }

    @Test
    public void generateUsernameWhenNameIsTaken() {
        String name = "John";
        String surname = "Smith";
        Long count = 2L;
        String expectedUsername = "john.smith3";
        expect(userDAO.countUsersWithSameFullName(name, surname)).andReturn(count);

        replay(userDAO);

        String nextUsername = userService.generateUsername(name, surname);

        verify(userDAO);

        assertEquals(expectedUsername, nextUsername);
    }
}
