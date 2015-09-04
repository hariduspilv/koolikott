package ee.hm.dop.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.AuthenticatedUser;

/**
 * Created by mart on 4.09.15.
 */
public class AuthenticatedUserResourceTest extends ResourceIntegrationTestBase {

    @Test
    public void getAuthenticatedUser(){
        String token = "token";
        Response response = doGet("authenticatedUser/getAuthenticatedUser?token=" + token);
        AuthenticatedUser authenticatedUser = response.readEntity(new GenericType<AuthenticatedUser>() {
        });
        assertNotNull(authenticatedUser);
        assertEquals("token", authenticatedUser.getToken());
    }

    @Test
    public void getAuthenticatedUserWrongToken(){
        String token = "wrongToken";
        Response response = doGet("authenticatedUser/getAuthenticatedUser?token=" + token);
        AuthenticatedUser authenticatedUser = response.readEntity(new GenericType<AuthenticatedUser>() {
        });
        assertNull(authenticatedUser);
    }
}
