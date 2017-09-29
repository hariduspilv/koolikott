package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import javax.inject.Inject;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.utils.exceptions.DuplicateTokenException;
import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.User;
import org.junit.Test;

public class AuthenticatedUserDaoTest extends DatabaseTestBase {

    @Inject
    private AuthenticatedUserDao authenticatedUserDao;

    @Inject
    private UserDao userDao;

    @Test
    public void createAuthenticatedUser() {
        User user = getUser();

        AuthenticatedUser returnedAuthenticatedUser = createAuthenticatedUser(user, "123123");

        assertEquals(user, returnedAuthenticatedUser.getUser());

        authenticatedUserDao.delete(returnedAuthenticatedUser);
        userDao.delete(user);
    }

    @Test
    public void createAuthenticatedUserSameToken() {
        User user = getUser();

        AuthenticatedUser returnedAuthenticatedUser = createAuthenticatedUser(user, "123123");

        AuthenticatedUser authenticatedUser2 = new AuthenticatedUser();
        authenticatedUser2.setToken("123123");
        authenticatedUser2.setUser(user);

        try {
            authenticatedUserDao.createAuthenticatedUser(authenticatedUser2);
            fail("Exception expected");
        } catch (DuplicateTokenException e) {
            // expected
        }

        authenticatedUserDao.delete(returnedAuthenticatedUser);
        userDao.delete(user);
    }

    @Test
    public void createAuthenticatedUserTwice() {
        User user = getUser();

        AuthenticatedUser authenticatedUser1 = createAuthenticatedUser(user, "token1");

        AuthenticatedUser authenticatedUser2 = createAuthenticatedUser(user, "token2");

        AuthenticatedUser returnedUser1 = authenticatedUserDao.createAuthenticatedUser(authenticatedUser1);
        AuthenticatedUser returnedUser2 = authenticatedUserDao.createAuthenticatedUser(authenticatedUser2);

        assertEquals(authenticatedUser1.getUser(), authenticatedUserDao.findAuthenticatedUserByToken("token1")
                .getUser());
        assertEquals(authenticatedUser2.getUser(), authenticatedUserDao.findAuthenticatedUserByToken("token2")
                .getUser());

        authenticatedUserDao.delete(returnedUser1);
        authenticatedUserDao.delete(returnedUser2);
        userDao.delete(user);
    }

    @Test
    public void findAuthenticatedUserByToken() {
        User user = getUser();

        AuthenticatedUser returnedAuthenticatedUser = createAuthenticatedUser(user, "123123");

        assertEquals("123123", authenticatedUserDao.findAuthenticatedUserByToken("123123").getToken());

        authenticatedUserDao.delete(returnedAuthenticatedUser);
        userDao.delete(user);
    }

    @Test
    public void delete() {
        User user = getUser();
        AuthenticatedUser returnedAuthenticatedUser = createAuthenticatedUser(user, "123123");

        authenticatedUserDao.delete(returnedAuthenticatedUser);
        assertNull(authenticatedUserDao.findAuthenticatedUserByToken("123123"));

        userDao.delete(user);
    }

    private AuthenticatedUser createAuthenticatedUser(User user, String token) {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setToken(token);
        authenticatedUser.setUser(user);
        return authenticatedUserDao.createAuthenticatedUser(authenticatedUser);
    }

    private User getUser() {
        User user = new User();
        user.setName("Mati2");
        user.setSurname("Maasikas2");
        user.setUsername("mati2.maasikas2");
        user.setIdCode("12345678969");
        return userDao.createOrUpdate(user);
    }

}
