package ee.hm.dop.rest.login;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.common.test.TestLayer;
import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.service.login.dto.UserStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DevelopmentLoginResourceTest extends ResourceIntegrationTestBase {

    public static final String NOT_EXISTING_USER = "123";

    @Test
    public void existing_user_can_use_dev_login() {
        AuthenticatedUser authenticatedUser  = doGet(DEV_LOGIN + USER_MATI.idCode, UserStatus.class).getAuthenticatedUser();
        assertNotNull(authenticatedUser.getToken());
        validateUser(authenticatedUser.getUser(), USER_MATI, TestLayer.REST);
    }

    @Test
    public void nonexisting_user_can_not_login() {
        Response response = doGet(DEV_LOGIN + NOT_EXISTING_USER);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void cant_log_in_without_idCode() {
        Response response = doGet(DEV_LOGIN);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }
}
