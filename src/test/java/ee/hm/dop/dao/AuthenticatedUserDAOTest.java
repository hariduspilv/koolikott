package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.junit.Test;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.exceptions.DuplicateTokenException;
import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.User;

/**
 * Created by mart.laus on 13.08.2015.
 */
public class AuthenticatedUserDAOTest extends DatabaseTestBase {

    @Inject
    private AuthenticatedUserDAO authenticatedUserDAO;

    @Inject
    private UserDAO userDAO;

    @Test(expected = DuplicateTokenException.class)
    public void createAuthenticatedUser() {
        AuthenticatedUser authenticatedUser1 = new AuthenticatedUser();
        authenticatedUser1.setToken("1");
        AuthenticatedUser authenticatedUser2 = new AuthenticatedUser();
        authenticatedUser2.setToken("1");

        authenticatedUserDAO.createAuthenticatedUser(authenticatedUser1);
        authenticatedUserDAO.createAuthenticatedUser(authenticatedUser2);
    }

    @Test
    public void findAuthenticatedUserByIdCode() {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setToken("token");
        User user = getUser();
        userDAO.createUser(user);
        authenticatedUser.setUser(user);
        authenticatedUserDAO.createAuthenticatedUser(authenticatedUser);

        AuthenticatedUser returnedUser = authenticatedUserDAO.findAuthenticatedUserByIdCode(user.getIdCode());

        assertEquals(authenticatedUser.getToken(), returnedUser.getToken());
        assertEquals(authenticatedUser.getUser().getIdCode(), returnedUser.getUser().getIdCode());

    }

    private User getUser() {
        User user = new User();
        user.setIdCode("1");
        user.setName("name");
        user.setSurname("surname");
        user.setUsername("username");
        return user;
    }
}
