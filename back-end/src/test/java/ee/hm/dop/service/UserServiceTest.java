package ee.hm.dop.service;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import ee.hm.dop.dao.UserDao;
import ee.hm.dop.model.User;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EasyMockRunner.class)
public class UserServiceTest {

    @TestSubject
    private UserService userService = new UserService();

    @Mock
    private UserDao userDao;

    @Test
    public void create() throws Exception {
        String idCode = "idCode";
        String name = "John";
        String surname = "Smith";
        String username = "john.smith";

        expect(userDao.countUsersWithSameUsername(username)).andReturn(0L);
        expect(userDao.createOrUpdate(EasyMock.anyObject(User.class))).andReturn(new User());

        replay(userDao);

        User user = userService.create(idCode, name, surname);

        verify(userDao);

        assertNotNull(user);
    }

    @Test
    public void generateUsername() {
        String name = " John\tSmith ";
        String surname = " Second  IV ";
        Long count = 0L;
        String username = "john.smith.second.iv";

        expect(userDao.countUsersWithSameUsername(username)).andReturn(count);

        replay(userDao);

        String nextUsername = userService.generateUsername(name, surname);

        verify(userDao);

        assertEquals(username, nextUsername);
    }

    @Test
    public void generateUsernameWhenNameIsTaken() {
        String name = "John";
        String surname = "Smith";
        String usernameWithoutNumber = "john.smith";
        Long count = 2L;
        String expectedUsername = "john.smith3";

        expect(userDao.countUsersWithSameUsername(usernameWithoutNumber)).andReturn(count);

        replay(userDao);

        String nextUsername = userService.generateUsername(name, surname);

        verify(userDao);

        assertEquals(expectedUsername, nextUsername);
    }

    @Test
    public void generateUsernameAndRemoveAccents() {
        String name = "Õnne Ülle Ärni";
        String surname = "Öpik";
        Long count = 0L;
        String username = "onne.ulle.arni.opik";

        expect(userDao.countUsersWithSameUsername(username)).andReturn(count);

        replay(userDao);

        String nextUsername = userService.generateUsername(name, surname);

        verify(userDao);

        assertEquals(username, nextUsername);
    }

}
