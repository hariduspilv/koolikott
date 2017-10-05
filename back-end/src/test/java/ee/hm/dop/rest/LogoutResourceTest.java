package ee.hm.dop.rest;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.common.test.TestConstants;
import ee.hm.dop.model.AuthenticatedUser;
import org.junit.Test;

public class LogoutResourceTest extends ResourceIntegrationTestBase {

    @Test
    public void testLogout() {
        AuthenticatedUser authenticatedUser = doGet(DEV_LOGIN + USER_MATI.idCode, new GenericType<AuthenticatedUser>() {
        });
        assertNotNull(authenticatedUser.getToken());
        String token = authenticatedUser.getToken();

        getTarget(LOGOUT, new LogoutFilter(token)).request().accept(MediaType.APPLICATION_JSON_TYPE).post(null);
    }

    @Provider
    public static class LogoutFilter implements ClientRequestFilter {
        String token = null;

        public LogoutFilter(String token) {
            this.token = token;
        }

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

            List<Object> list3 = new ArrayList<>();
            list3.add(token);
            requestContext.getHeaders().put("Authentication", list3);

            List<Object> list4 = new ArrayList<>();
            list4.add("mati.maasikas");
            requestContext.getHeaders().put("Username", list4);
        }
    }
}
