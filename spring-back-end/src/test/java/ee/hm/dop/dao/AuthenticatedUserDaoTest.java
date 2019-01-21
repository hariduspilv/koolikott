package ee.hm.dop.dao;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.LoginFrom;
import ee.hm.dop.model.enums.Role;
import ee.hm.dop.utils.exceptions.DuplicateTokenException;
import java.time.LocalDateTime;
import org.junit.Test;

import javax.inject.Inject;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class AuthenticatedUserDaoTest extends DatabaseTestBase {

    public static final String TOKEN = "123123";
    public static final String TOKEN2 = "123122";
    @Inject
    private AuthenticatedUserDao authenticatedUserDao;
    @Inject
    private UserDao userDao;

    @Test
    public void user_can_be_authenticated() {
        User user = user();

        AuthenticatedUser newAuthenticatedUser = authenticate(user, TOKEN);
        assertEquals(user, newAuthenticatedUser.getUser());

        cleanUp(user, newAuthenticatedUser);
    }

    @Test
    public void authenticated_user_must_have_unique_token() {
        User user = user();
        AuthenticatedUser authenticatedUser = authenticate(user, TOKEN);
        try {
            authenticate(user, TOKEN);
            fail("Exception expected");
        } catch (DuplicateTokenException e) {
            // expected
        }
        cleanUp(user, authenticatedUser);
    }

    @Test
    public void authenticated_user_can_be_created_many_times_for_user_with_different_token() {
        User user = user();

        AuthenticatedUser authenticatedUser1 = authenticate(user, TOKEN);
        AuthenticatedUser authenticatedUser2 = authenticate(user, TOKEN2);

        AuthenticatedUser returnedUser1 = authenticatedUserDao.createAuthenticatedUser(authenticatedUser1);
        AuthenticatedUser returnedUser2 = authenticatedUserDao.createAuthenticatedUser(authenticatedUser2);

        assertEquals(authenticatedUser1.getUser(), authenticatedUserDao.findAuthenticatedUserByToken(TOKEN).getUser());
        assertEquals(authenticatedUser2.getUser(), authenticatedUserDao.findAuthenticatedUserByToken(TOKEN2).getUser());

        cleanUp(user, returnedUser1, returnedUser2);
    }

    @Test
    public void user_can_be_found_by_token() {
        User user = user();

        AuthenticatedUser authenticatedUser = authenticate(user, TOKEN);
        assertEquals(TOKEN, authenticatedUserDao.findAuthenticatedUserByToken(TOKEN).getToken());

        cleanUp(user, authenticatedUser);
    }

    @Test
    public void deleted_authenticated_user_cannot_be_found_by_token() {
        User user = user();
        AuthenticatedUser authenticatedUser = authenticate(user, TOKEN);

        authenticatedUserDao.delete(authenticatedUser);
        assertNull(authenticatedUserDao.findAuthenticatedUserByToken(TOKEN));

        cleanUp(user, authenticatedUser);
    }

    private AuthenticatedUser authenticate(User user, String token) {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setToken(token);
        authenticatedUser.setUser(user);
        authenticatedUser.setLoginDate(LocalDateTime.now());
        authenticatedUser.setSessionTime(LocalDateTime.now().plusMinutes(15));
        authenticatedUser.setLoginFrom(LoginFrom.DEV);
        return authenticatedUserDao.createAuthenticatedUser(authenticatedUser);
    }

    private User user() {
        User user = new User();
        user.setName("Mati2");
        user.setSurname("Maasikas2");
        user.setUsername("mati2.maasikas2");
        user.setIdCode("12345678969");
        user.setRole(Role.USER);
        return userDao.createOrUpdate(user);
    }

    private void cleanUp(User user, AuthenticatedUser authenticatedUser) {
        authenticatedUserDao.remove(authenticatedUser);
        userDao.delete(user);
    }

    private void cleanUp(User user, AuthenticatedUser returnedUser1, AuthenticatedUser returnedUser2) {
        authenticatedUserDao.remove(returnedUser1);
        authenticatedUserDao.remove(returnedUser2);
        userDao.delete(user);
    }

}
