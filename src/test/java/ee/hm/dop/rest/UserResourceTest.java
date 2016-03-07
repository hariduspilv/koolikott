package ee.hm.dop.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.Role;
import ee.hm.dop.model.User;

public class UserResourceTest extends ResourceIntegrationTestBase {

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
        String idCode = "89012378912";
        login(idCode);

        Response response = doGet("user/role", MediaType.TEXT_PLAIN_TYPE);
        String roleString = response.readEntity(String.class);
        assertEquals(Role.USER.toString(), roleString);
    }

    @Test
    public void getRoleAdmin() {
        String idCode = "89898989898";
        login(idCode);

        Response response = doGet("user/role", MediaType.TEXT_PLAIN_TYPE);
        String roleString = response.readEntity(String.class);
        assertEquals(Role.ADMIN.toString(), roleString);
    }

    private User getUser(String username) {
        Response response = doGet("user?username=" + username);
        return response.readEntity(new GenericType<User>() {
        });
    }
}
