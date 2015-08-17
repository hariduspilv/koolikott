package ee.hm.dop.rest;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.AuthenticatedUser;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by mart on 17.08.15.
 */
public class LoginResourceTest extends ResourceIntegrationTestBase {

    @Test
    public void logIn() {
        Response response = doGet("39011220011");
        AuthenticatedUser authenticatedUser = response.readEntity(new GenericType<AuthenticatedUser>() {
        });
        assertNotNull(authenticatedUser.getToken());
        assertEquals("Mati", authenticatedUser.getUser().getName());
        assertEquals("Maasikas", authenticatedUser.getUser().getSurname());
        assertEquals("mati.maasikas", authenticatedUser.getUser().getUsername());
        assertEquals("39011220011", authenticatedUser.getUser().getIdCode());
    }

    @Test
    public void loginWrongId() {
        Response response = doGet("123");
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void loginNullId() {
        Response response = doGet(null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

    }
}
