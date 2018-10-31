package ee.hm.dop.rest.administration;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.UserManuals;
import org.joda.time.DateTime;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class UserManualsAdminResourceTest extends ResourceIntegrationTestBase {

    private static final String GET_USER_MANUALS_URL = "admin/userManuals";
    private static final String POST_USER_MANUAL = "admin/userManuals";
    private static final String DELETE_USER_MANUAL = "admin/userManuals/delete";

    @Test
    public void user_does_not_have_to_be_logged_in_to_get_user_manuals() {
        assertEquals(Response.Status.OK.getStatusCode(), doGet(GET_USER_MANUALS_URL).getStatus());
    }

    @Test
    public void admin_user_can_save_user_manuals() {
        login(USER_ADMIN);
        UserManuals userManuals = buildManuals("Unit Test Title");
        UserManuals um1 = doPost(POST_USER_MANUAL, userManuals, UserManuals.class);
        validateUserManual(um1, "Unit Test Title");
        List<UserManuals> userManualsList = doGet(GET_USER_MANUALS_URL, new GenericType<List<UserManuals>>() {
        });
        assertTrue(userManualsList.size() > 0);
    }

    @Test
    public void admin_can_delete_user_manuals() {
        login(USER_ADMIN);
        UserManuals userManuals = buildManuals("Unit Test Title2");
        UserManuals savedUserManuals = doPost(POST_USER_MANUAL, userManuals, UserManuals.class);
        validateUserManual(savedUserManuals, "Unit Test Title2");
        Response response = doPost(DELETE_USER_MANUAL, savedUserManuals);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void moderator_cant_add_user_manuals() {
        login(USER_MODERATOR);
        UserManuals um = buildManuals("Moderator manual");
        Response response = doPost(POST_USER_MANUAL, um);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    public UserManuals buildManuals(String s) {
        return new UserManuals(DateTime.now(), getUser(USER_ADMIN), s, "http://www.youtube.com", "www.google.com");
    }

    public void validateUserManual(UserManuals um1, String title) {
        assertNotNull(um1.getId());
        assertEquals(title, um1.getTitle());
        assertEquals("http://www.youtube.com", um1.getUrl());
        assertEquals("http://www.google.com", um1.getTextUrl());
    }
}
