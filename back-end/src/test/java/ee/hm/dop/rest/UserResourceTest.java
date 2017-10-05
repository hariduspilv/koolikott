package ee.hm.dop.rest;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.common.test.TestConstants;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.Role;
import ee.hm.dop.model.taxon.Taxon;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static org.junit.Assert.*;

public class UserResourceTest extends ResourceIntegrationTestBase {

    private static final String GET_TAXON_URL = "learningMaterialMetadata/taxon?taxonId=%s";
    public static final String RESTRICT_USER = "user/restrictUser";
    public static final String USER_ROLE = "user/role";
    public static final String GET_SIGNED_USER_DATA = "user/getSignedUserData";

    @Test
    public void getUser_returns_user() {
        validateUser(getUser(USER_MATI), USER_MATI);
        validateUser(getUser(USER_PEETER), USER_PEETER);
        validateUser(getUser(USER_VOLDERMAR), USER_VOLDERMAR);
    }

    @Test
    public void getUser_fails_with_no_parameter() {
        Response response = doGet("user");
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void getUser_fails_with_no_parameter_value() {
        Response response = doGet("user?username=");
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void getUser_returns_nothing_when_user_is_not_found() {
        String username = "notexisting.user";
        Response response = doGet("user?username=" + username);
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void logged_in_user_can_see_signedUserData() {
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.add("Authentication", "token");
        headers.add("Username", "mati.maasikas");

        Response response2 = doGet(GET_SIGNED_USER_DATA, headers, MediaType.TEXT_PLAIN_TYPE);
        assertEquals(Status.OK.getStatusCode(), response2.getStatus());
        String encryptedUserData = response2.readEntity(new GenericType<String>() {
        });
        assertNotNull(encryptedUserData);
    }

    @Test
    public void user_must_be_logged_in_to_get_signedUserData() {
        Response response = doGet(GET_SIGNED_USER_DATA, MediaType.TEXT_PLAIN_TYPE);
        assertEquals(Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    public void user_role_is_user() {
        login(USER_SECOND);
        String roleString = doGet(USER_ROLE, MediaType.TEXT_PLAIN_TYPE, String.class);
        assertEquals(Role.USER.toString(), roleString);
    }

    @Test
    public void admin_role_is_admin() {
        login(USER_ADMIN);
        String roleString = doGet(USER_ROLE, MediaType.TEXT_PLAIN_TYPE, String.class);
        assertEquals(Role.ADMIN.toString(), roleString);
    }

    @Test
    public void moderator_can_restrict_users() {
        login(USER_MODERATOR);
        User userToRestrict = getUser(USER_TO_BE_BANNED1);
        User restrictedUser = doPost(RESTRICT_USER, userToRestrict, User.class);
        assertEquals(Role.RESTRICTED, restrictedUser.getRole());
    }

    @Test
    public void admin_can_restrict_users() {
        login(USER_ADMIN);
        User userToRestrict = getUser(USER_TO_BE_BANNED2);
        User restrictedUser = doPost(RESTRICT_USER, userToRestrict, User.class);
        assertEquals(Role.RESTRICTED, restrictedUser.getRole());
    }

    @Test
    public void regular_user_can_not_restrict_user() {
        login(USER_MATI);
        User userToRestrict = getUser(USER_TO_BE_BANNED1);
        Response response = doPost(RESTRICT_USER, userToRestrict);
        assertEquals(Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    @Test
    public void admin_can_unrestrict_user() {
        login(USER_ADMIN);
        User userToRemoveRestrictionFrom = getUser(USER_RESTRICTED2);
        User nonRestrictedUser = doPost("user/removeRestriction", userToRemoveRestrictionFrom, User.class);
        assertEquals(Role.USER, nonRestrictedUser.getRole());
    }


    @Test
    public void database_has_some_users() {
        login(USER_ADMIN);
        List<User> allUsers = doGet("user/all", new GenericType<List<User>>() {
        });
        assertTrue(CollectionUtils.isNotEmpty(allUsers));
    }

    @Test
    public void updating_user_taxons_as_admin_updates_user() throws Exception {
        login(USER_ADMIN);

        List<Taxon> taxons = new ArrayList<>();
        taxons.add(doGet(format(GET_TAXON_URL, TAXON_MATHEMATICS_DOMAIN.id), Taxon.class));

        User user = getUser(USER_MYTESTUSER);
        user.setUserTaxons(taxons);
        User updatedUser = doPost("user", user, User.class);

        assertEquals(updatedUser.getUserTaxons(), taxons);
    }


}
