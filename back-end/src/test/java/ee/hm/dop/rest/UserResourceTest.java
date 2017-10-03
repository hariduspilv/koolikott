package ee.hm.dop.rest;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.common.test.TestConstants;
import ee.hm.dop.common.test.TestUser;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.Role;
import ee.hm.dop.model.taxon.Taxon;
import org.junit.Test;

import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static org.junit.Assert.*;

public class UserResourceTest extends ResourceIntegrationTestBase {

    public static final String USER_BILLY = "38211120031";
    private static final String USER_USERNAME = "my.testuser";
    private static final String GET_TAXON_URL = "learningMaterialMetadata/taxon?taxonId=%s";
    private static final long TEST_TAXON_ID = 10L;

    @Test
    public void get() {
        User user = getUser(TestConstants.USER_MATI.username);
        validateUser(user, TestConstants.USER_MATI);

        user = getUser(TestConstants.USER_PEETER.username);
        validateUser(user, TestConstants.USER_PEETER);

        user = getUser(TestConstants.USER_VOLDERMAR.username);
        validateUser(user, TestConstants.USER_VOLDERMAR);
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
        login(TestConstants.USER_SECOND);

        String roleString = doGet("user/role", MediaType.TEXT_PLAIN_TYPE, String.class);
        assertEquals(Role.USER.toString(), roleString);
    }

    @Test
    public void getRoleAdmin() {
        login(TestConstants.USER_ADMIN);

        String roleString = doGet("user/role", MediaType.TEXT_PLAIN_TYPE, String.class);
        assertEquals(Role.ADMIN.toString(), roleString);
    }

    @Test
    public void restrictUserWithModerator() {
        login(USER_BILLY);

        User userToRestrict = doGet("user?username=user.to.be.banned1", User.class);
        User restrictedUser = doPost("user/restrictUser", userToRestrict, User.class);
        assertEquals(Role.RESTRICTED, restrictedUser.getRole());
    }

    @Test
    public void restrictUserWithAdmin() {
        login(TestConstants.USER_ADMIN);

        User userToRestrict = doGet("user?username=user.to.be.banned2", User.class);
        User restrictedUser = doPost("user/restrictUser", userToRestrict, User.class);
        assertEquals(Role.RESTRICTED, restrictedUser.getRole());
    }

    @Test
    public void restrictUserNotAllowed() {
        login(TestConstants.USER_MATI);

        User userToRestrict = doGet("user?username=user.to.be.banned", User.class);
        Response response = doPost("user/restrictUser", userToRestrict);
        assertEquals(Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    @Test
    public void removeRestrictionWithAdmin() {
        login(TestConstants.USER_ADMIN);

        User userToRemoveRestrictionFrom = doGet("user?username=restricted.user2", User.class);
        User nonRestrictedUser = doPost("user/removeRestriction", userToRemoveRestrictionFrom, User.class);
        assertEquals(Role.USER, nonRestrictedUser.getRole());
    }


    @Test
    public void getAll() {
        login(TestConstants.USER_ADMIN);
        List<User> allUsers = doGet("user/all", new GenericType<List<User>>() {
        });
        assertTrue(allUsers.size() > 14);
    }

    @Test
    public void updating_user_taxons_as_admin_updates_user() throws Exception {
        login(TestConstants.USER_ADMIN);

        List<Taxon> taxons = new ArrayList<>();
        taxons.add(doGet(format(GET_TAXON_URL, TEST_TAXON_ID), Taxon.class));

        User user = getUser(USER_USERNAME);
        user.setUserTaxons(taxons);
        User updatedUser = doPost("user", user, User.class);

        assertEquals(updatedUser.getUserTaxons(), taxons);
    }

    private User getUser(String username) {
        return doGet("user?username=" + username, User.class);
    }

    private void validateUser(User user, TestUser testUser) {
        assertEquals(testUser.id, user.getId());
        assertEquals(testUser.username, user.getUsername());
        assertEquals(testUser.firstName, user.getName());
        assertEquals(testUser.lastName, user.getSurname());
        assertNull(user.getIdCode());
    }
}
