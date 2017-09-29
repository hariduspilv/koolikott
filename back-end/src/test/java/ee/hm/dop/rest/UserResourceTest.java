package ee.hm.dop.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.enums.Role;
import ee.hm.dop.model.User;
import org.junit.Test;

public class UserResourceTest extends ResourceIntegrationTestBase {

    public static final String USER_BILLY = "38211120031";

    @Test
    public void get() {
        User user = getUser("mati.maasikas");
        assertEquals(Long.valueOf(1), user.getId());
        assertEquals("mati.maasikas", user.getUsername());
        assertEquals("Mati", user.getName());
        assertEquals("Maasikas", user.getSurname());
        assertNull(user.getIdCode());

        user = getUser("peeter.paan");
        assertEquals(Long.valueOf(2), user.getId());
        assertEquals("peeter.paan", user.getUsername());
        assertEquals("Peeter", user.getName());
        assertEquals("Paan", user.getSurname());
        assertNull(user.getIdCode());

        user = getUser("voldemar.vapustav");
        assertEquals(Long.valueOf(3), user.getId());
        assertEquals("voldemar.vapustav", user.getUsername());
        assertEquals("Voldemar", user.getName());
        assertEquals("Vapustav", user.getSurname());
        assertNull(user.getIdCode());
    }

    @Test
    public void getUserWithoutUsername() {
        Response response = doGet("user");
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void getUserWithBlankUsername() {
        Response response = doGet("user?username=");
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void getNotExistingUser() {
        String username = "notexisting.user";
        Response response = doGet("user?username=" + username);
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void getSignedUserData() {
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.add("Authentication", "token");
        headers.add("Username", "mati.maasikas");

        Response response2 = doGet("user/getSignedUserData", headers, MediaType.TEXT_PLAIN_TYPE);
        assertEquals(Status.OK.getStatusCode(), response2.getStatus());

        String encryptedUserData = response2.readEntity(new GenericType<String>() {
        });
        assertNotNull(encryptedUserData);
    }

    @Test
    public void getSignedUserDataNotLoggedIn() {
        Response response = doGet("user/getSignedUserData", MediaType.TEXT_PLAIN_TYPE);
        assertEquals(Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    public void getRoleUser() {
        login(USER_SECOND);

        String roleString = doGet("user/role", MediaType.TEXT_PLAIN_TYPE, String.class);
        assertEquals(Role.USER.toString(), roleString);
    }

    @Test
    public void getRoleAdmin() {
        login(USER_ADMIN);

        String roleString = doGet("user/role", MediaType.TEXT_PLAIN_TYPE, String.class);
        assertEquals(Role.ADMIN.toString(), roleString);
    }

    @Test
    public void restrictUserWithModerator() {
        login(USER_BILLY);

        User userToRestrict = doGet("user?username=user.to.be.banned1", user());
        User restrictedUser = doPost("user/restrictUser", userToRestrict, User.class);
        assertEquals(Role.RESTRICTED, restrictedUser.getRole());
    }

    @Test
    public void restrictUserWithAdmin() {
        login(USER_ADMIN);

        User userToRestrict = doGet("user?username=user.to.be.banned2", user());
        User restrictedUser = doPost("user/restrictUser", userToRestrict, User.class);
        assertEquals(Role.RESTRICTED, restrictedUser.getRole());
    }

    @Test
    public void restrictUserNotAllowed() {
        login(USER_MATI);

        User userToRestrict = doGet("user?username=user.to.be.banned", user());
        Response response = doPost("user/restrictUser", userToRestrict);
        assertEquals(Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    @Test
    public void removeRestrictionWithAdmin() {
        login(USER_ADMIN);

        User userToRemoveRestrictionFrom = doGet("user?username=restricted.user2", user());
        User nonRestrictedUser = doPost("user/removeRestriction", userToRemoveRestrictionFrom, User.class);
        assertEquals(Role.USER, nonRestrictedUser.getRole());
    }


    @Test
    public void getAll() {
        login(USER_ADMIN);
        List<User> allUsers = doGet("user/all", new GenericType<List<User>>() {
        });
        assertTrue(allUsers.size() > 14);
    }

    private User getUser(String username) {
        return doGet("user?username=" + username, user());
    }

    private GenericType<User> user() {
        return new GenericType<User>() {
        };
    }
}
