package ee.hm.dop.rest;

import static org.easymock.EasyMock.createMock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.junit.Test;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.rest.filter.DopPrincipal;
import ee.hm.dop.rest.filter.DopSecurityContext;

public class LogoutResourceTest extends ResourceIntegrationTestBase {

    @Test
    public void logout() {
        getTarget("logout", new AuthenticationFilter1(), new LogoutFilter1()).request()
                .accept(MediaType.APPLICATION_JSON).post(null);

    }

    @Provider
    public static class LogoutFilter1 implements ContainerRequestFilter {

        @Override
        public void filter(ContainerRequestContext requestContext) throws IOException {
            DopPrincipal principal = new DopPrincipal(new AuthenticatedUser());
            UriInfo uriInfo = createMock(UriInfo.class);
            DopSecurityContext securityContext = new DopSecurityContext(principal, uriInfo);
            requestContext.setSecurityContext(securityContext);
        }
    }

    @Provider
    public static class AuthenticationFilter1 implements ClientRequestFilter {

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
}
