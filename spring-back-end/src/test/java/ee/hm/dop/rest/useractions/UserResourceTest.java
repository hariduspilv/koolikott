package ee.hm.dop.rest.useractions;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.common.test.TestLayer;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.Role;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.model.user.UserSession;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import sun.security.provider.certpath.OCSPResponse;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class UserResourceTest extends ResourceIntegrationTestBase {

    public static final String GET_TAXON_URL = "learningMaterialMetadata/taxon?taxonId=%s";
    public static final String RESTRICT_USER = "user/restrictUser";
    public static final String USER_ROLE = "user/role";
    public static final String SESSION_TIME = "user/sessionTime";
    public static final String UPDATE_SESSION = "user/updateSession";
    public static final String TERMINATE_SESSION = "user/terminateSession";
    public static final String GET_SIGNED_USER_DATA = "user/getSignedUserData";
    public static final String USER_LOCATION = "user/getLocation";
    public static final String REMOVE_USER_RESTRICTION = "user/removeRestriction";

    @Test
    public void getUser_returns_user() {
        validateUser(getUser(USER_MATI), USER_MATI, TestLayer.REST);
        validateUser(getUser(USER_PEETER), USER_PEETER, TestLayer.REST);
        validateUser(getUser(USER_VOLDERMAR), USER_VOLDERMAR, TestLayer.REST);
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
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
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
        Response response = doGet(GET_SIGNED_USER_DATA, MediaType.WILDCARD_TYPE);;
        assertEquals(Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    public void session_is_less_than_120_min() {
        login(USER_ADMIN);
        UserSession session = doGet(SESSION_TIME, UserSession.class);
        assertTrue(120 >= session.getMinRemaining());
        UserSession prolongedSession = doPost(UPDATE_SESSION, continueSession(), UserSession.class);
        assertTrue(120 >= prolongedSession.getMinRemaining());
        UserSession askSessionAgain = doGet(SESSION_TIME, UserSession.class);
        assertTrue(120 >= askSessionAgain.getMinRemaining());
    }

    @Test
    public void terminate_session_terminates_session() {
        login(USER_ADMIN);
        UserSession session = doGet(SESSION_TIME, UserSession.class);
        assertTrue(120 >= session.getMinRemaining());
        doPost(TERMINATE_SESSION, null, UserSession.class);
        authenticationFilter = null; //killing session kills logout
        Response response = doGet(SESSION_TIME);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
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
        assertNotNull(restrictedUser);
        assertEquals(Role.RESTRICTED, restrictedUser.getRole());

        //revert
        doPost(REMOVE_USER_RESTRICTION, restrictedUser);
    }

    @Test
    public void admin_can_restrict_users() {
        login(USER_ADMIN);
        User userToRestrict = getUser(USER_TO_BE_BANNED2);
        User restrictedUser = doPost(RESTRICT_USER, userToRestrict, User.class);
        assertNotNull(restrictedUser);
        assertEquals(Role.RESTRICTED, restrictedUser.getRole());

        //revert
        doPost(REMOVE_USER_RESTRICTION, restrictedUser);
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
        User nonRestrictedUser = doPost(REMOVE_USER_RESTRICTION, userToRemoveRestrictionFrom, User.class);
        assertNotNull(nonRestrictedUser);
        assertEquals(Role.USER, nonRestrictedUser.getRole());

        //revert
        doPost(RESTRICT_USER, nonRestrictedUser);
    }

    @Test
    public void database_has_some_users() {
        login(USER_ADMIN);
        List<User> allUsers = doGet("user/all", new GenericType<List<User>>() {
        });
        assertTrue(CollectionUtils.isNotEmpty(allUsers));
    }

    @Ignore
    //todo doesnt work?
    @Test
    public void user_has_location() {
        login(USER_ADMIN);
        String location = doGet(USER_LOCATION, MediaType.TEXT_PLAIN_TYPE, String.class);
        assertTrue(StringUtils.isNotBlank(location));
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

    private UserSession continueSession() {
        UserSession json = new UserSession();
        json.setContinueSession(true);
        return json;
    }
}
