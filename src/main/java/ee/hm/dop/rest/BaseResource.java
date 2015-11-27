package ee.hm.dop.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.User;
import ee.hm.dop.rest.filter.DopPrincipal;

public class BaseResource {

    @Context
    private HttpServletRequest request;

    @Context
    private HttpServletResponse response;

    private SecurityContext securityContext;

    @Context
    public void setSecurityContext(SecurityContext securityContext) {
        this.securityContext = securityContext;
    }

    protected User getLoggedInUser() {
        return ((DopPrincipal) securityContext.getUserPrincipal()).getUser();
    }

    protected AuthenticatedUser getAuthenticatedUser() {
        return ((DopPrincipal) securityContext.getUserPrincipal()).getAuthenticatedUser();
    }

    protected HttpServletRequest getRequest() {
        return request;
    }

    protected HttpServletResponse getResponse() {
        return response;
    }
}
