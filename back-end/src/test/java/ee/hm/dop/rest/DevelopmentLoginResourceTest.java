package ee.hm.dop.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.common.test.TestConstants;
import ee.hm.dop.model.AuthenticatedUser;
import org.junit.Test;

/**
 * Created by mart on 18.08.15.
 */
public class DevelopmentLoginResourceTest extends ResourceIntegrationTestBase {

    public static final String NOT_EXISTING_USER = "123";

    @Test
    public void logIn() {
        AuthenticatedUser authenticatedUser  = doGet(DEV_LOGIN + TestConstants.USER_MATI.idCode, new GenericType<AuthenticatedUser>() {
        });
        assertNotNull(authenticatedUser.getToken());
        assertEquals("Mati", authenticatedUser.getUser().getName());
        assertEquals("Maasikas", authenticatedUser.getUser().getSurname());
        assertEquals("mati.maasikas", authenticatedUser.getUser().getUsername());
        assertNull(authenticatedUser.getUser().getIdCode());
    }

    @Test
    public void loginWrongId() {
        Response response = doGet(DEV_LOGIN + NOT_EXISTING_USER);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void loginNullId() {
        Response response = doGet(null);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());

    }
}
