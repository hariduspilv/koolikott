package ee.hm.dop.rest.administration;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.Role;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RestrictedUserAdminResourceTest extends ResourceIntegrationTestBase {

    private static final String GET_RESTRICTED_USERS_URL = "admin/restrictedUser/";
    private static final String GET_RESTRICTED_USERS_COUNT_URL = "admin/restrictedUser/count";

    @Test
    public void getRestrictedUsers_returns_restricted_users_to_admin() throws Exception {
        login(USER_ADMIN);
        List<User> restrictedUsers = doGet(GET_RESTRICTED_USERS_URL, new GenericType<List<User>>() {
        });
        long restrictedUsersCount = doGet(GET_RESTRICTED_USERS_COUNT_URL, Long.class);

        assertTrue("Restricted users", restrictedUsers.stream().map(User::getRole).allMatch(role -> role.equals(Role.RESTRICTED)));
        assertEquals("Restricted users list size, restricted users count", restrictedUsers.size(), restrictedUsersCount);
    }

    @Test
    public void not_logged_in_user_is_not_allowed_to_getRestrictedUsers() throws Exception {
        Response response = doGet(GET_RESTRICTED_USERS_URL);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }
}
