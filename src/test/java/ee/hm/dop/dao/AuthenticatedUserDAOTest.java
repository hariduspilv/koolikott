package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import javax.inject.Inject;

import org.junit.Test;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.exceptions.DuplicateTokenException;
import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.User;

public class AuthenticatedUserDAOTest extends DatabaseTestBase {

    @Inject
    private AuthenticatedUserDAO authenticatedUserDAO;

    @Inject
    private UserDAO userDAO;

    @Test
    public void createAuthenticatedUser() {
        User user = getUser();

        AuthenticatedUser returnedAuthenticatedUser = createAuthenticatedUser(user, "123123");

        assertEquals(user, returnedAuthenticatedUser.getUser());

        authenticatedUserDAO.delete(returnedAuthenticatedUser);
        userDAO.delete(user);
    }

    @Test
    public void createAuthenticatedUserSameToken() {
        User user = getUser();

        AuthenticatedUser returnedAuthenticatedUser = createAuthenticatedUser(user, "123123");

        AuthenticatedUser authenticatedUser2 = new AuthenticatedUser();
        authenticatedUser2.setToken("123123");
        authenticatedUser2.setUser(user);


        try {
            authenticatedUserDAO.createAuthenticatedUser(authenticatedUser2);
            fail("Exception expected");
        } catch (DuplicateTokenException e) {
            //expected
        }

        authenticatedUserDAO.delete(returnedAuthenticatedUser);
        userDAO.delete(user);
    }

    @Test
    public void createAuthenticatedUserTwice() {
        User user = getUser();

        AuthenticatedUser authenticatedUser1 = createAuthenticatedUser(user, "token1");

        AuthenticatedUser authenticatedUser2 = createAuthenticatedUser(user, "token2");

        AuthenticatedUser returnedUser1 = authenticatedUserDAO.createAuthenticatedUser(authenticatedUser1);
        AuthenticatedUser returnedUser2 = authenticatedUserDAO.createAuthenticatedUser(authenticatedUser2);

        assertEquals(authenticatedUser1.getUser(),
                authenticatedUserDAO.findAuthenticatedUserByToken("token1").getUser());
        assertEquals(authenticatedUser2.getUser(),
                authenticatedUserDAO.findAuthenticatedUserByToken("token2").getUser());

        authenticatedUserDAO.delete(returnedUser1);
        authenticatedUserDAO.delete(returnedUser2);
        userDAO.delete(user);
    }

    @Test
    public void findAuthenticatedUserByToken(){
        User user = getUser();

        AuthenticatedUser returnedAuthenticatedUser = createAuthenticatedUser(user, "123123");

        assertEquals("123123", authenticatedUserDAO.findAuthenticatedUserByToken("123123").getToken());

        authenticatedUserDAO.delete(returnedAuthenticatedUser);
        userDAO.delete(user);
    }

    @Test
    public void delete() {
        User user = getUser();
        AuthenticatedUser returnedAuthenticatedUser = createAuthenticatedUser(user, "123123");

        authenticatedUserDAO.delete(returnedAuthenticatedUser);
        assertNull(authenticatedUserDAO.findAuthenticatedUserByToken("123123"));

        userDAO.delete(user);
    }

    private AuthenticatedUser createAuthenticatedUser(User user, String token) {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setToken(token);
        authenticatedUser.setUser(user);
        return authenticatedUserDAO.createAuthenticatedUser(authenticatedUser);
    }

    private User getUser() {
        User user = new User();
        user.setName("Mati2");
        user.setSurname("Maasikas2");
        user.setUsername("mati2.maasikas2");
        user.setIdCode("12345678969");
        return userDAO.update(user);
    }

}
