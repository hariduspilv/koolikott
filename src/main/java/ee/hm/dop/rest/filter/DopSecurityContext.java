package ee.hm.dop.rest.filter;

import java.security.Principal;

import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

public class DopSecurityContext implements SecurityContext {

    private UriInfo uriInfo;
    private DopPrincipal dopPrincipal;

    public DopSecurityContext(DopPrincipal principal, UriInfo uriInfo) {
        this.uriInfo = uriInfo;
        this.dopPrincipal = principal;
    }

    @Override
    public Principal getUserPrincipal() {
        return dopPrincipal;
    }

    @Override
    public boolean isUserInRole(String role) {
        return dopPrincipal != null && dopPrincipal.isUserInRole(role);
    }

    @Override
    public boolean isSecure() {
        return "https".equals(uriInfo.getRequestUri().getScheme());
    }

    @Override
    public String getAuthenticationScheme() {
        return SecurityContext.CLIENT_CERT_AUTH;
    }
}
