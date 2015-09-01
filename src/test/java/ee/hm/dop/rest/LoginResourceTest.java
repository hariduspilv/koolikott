package ee.hm.dop.rest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.junit.Test;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.AuthenticatedUser;

public class LoginResourceTest extends ResourceIntegrationTestBase {

    @Test
    public void login() {
        AuthenticatedUser authenticatedUser = getTarget("login/idCard", new LoginFilter1()).request()
                .accept(MediaType.APPLICATION_JSON).get(AuthenticatedUser.class);
        assertNotNull(authenticatedUser.getToken());
    }

    @Test
    public void loginAuthenticationFailed() {
        AuthenticatedUser authenticatedUser = getTarget("login/idCard", new LoginFilter2()).request()
                .accept(MediaType.APPLICATION_JSON).get(AuthenticatedUser.class);
        assertNull(authenticatedUser);
    }

    @Provider
    public static class LoginFilter1 implements ClientRequestFilter {

        @Override
        public void filter(ClientRequestContext requestContext) throws IOException {
            List<Object> list1 = new ArrayList<>();
            list1.add("serialNumber=39011220011");
            list1.add("GN=MATI");
            list1.add("SN=MAASIKAS");
            list1.add("CN=MATI,MAASIKAS,39011220011");
            list1.add("OU=authentication");
            list1.add("O=ESTEID");
            list1.add("C=EE");
            requestContext.getHeaders().put("SSL_CLIENT_S_DN", list1);

            List<Object> list2 = new ArrayList<>();
            list2.add("SUCCESS");
            requestContext.getHeaders().put("SSL_AUTH_VERIFY", list2);
        }
    }

    @Provider
    public static class LoginFilter2 implements ClientRequestFilter {

        @Override
        public void filter(ClientRequestContext requestContext) throws IOException {
            List<Object> list1 = new ArrayList<>();
            list1.add("serialNumber=39011220011");
            list1.add("GN=MATI");
            list1.add("SN=MAASIKAS");
            list1.add("CN=MATI,MAASIKAS,39011220011");
            list1.add("OU=authentication");
            list1.add("O=ESTEID");
            list1.add("C=EE");
            requestContext.getHeaders().put("SSL_CLIENT_S_DN", list1);

            List<Object> list2 = new ArrayList<>();
            list2.add("FAILED");
            requestContext.getHeaders().put("SSL_AUTH_VERIFY", list2);
        }
    }
}
