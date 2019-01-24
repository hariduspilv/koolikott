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

public class ModeratorAdminResourceTest extends ResourceIntegrationTestBase {

    private static final String GET_MODERATORS_URL = "admin/moderator/";
    private static final String GET_MODERATORS_COUNT_URL = "admin/moderator/count";

    @Test
    public void getModerators_returns_moderator_users_to_admin() throws Exception {
        login(USER_ADMIN);
        List<User> moderators = doGet(GET_MODERATORS_URL, new GenericType<List<User>>() {
        });
        long moderatorsCount = doGet(GET_MODERATORS_COUNT_URL, Long.class);

        assertTrue("Moderators", moderators.stream().map(User::getRole).allMatch(role -> role.equals(Role.MODERATOR)));
        assertEquals("Moderators list size, moderators count", moderators.size(), moderatorsCount);
    }

    @Test
    public void not_logged_in_user_is_not_allowed_to_getModerators() throws Exception {
        Response response = doGet(GET_MODERATORS_URL);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }
}
