package ee.hm.dop.rest;

import java.net.HttpURLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
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
        User user = null;

        if (isAuthenticated()) {
            user = getAuthenticatedUser().getUser();
        }

        return user;
    }

    protected AuthenticatedUser getAuthenticatedUser() {
        DopPrincipal dopPrincipal = (DopPrincipal) securityContext.getUserPrincipal();

        AuthenticatedUser authenticatedUser = null;
        if (dopPrincipal != null) {
            authenticatedUser = dopPrincipal.getAuthenticatedUser();
        }

        return authenticatedUser;
    }

    protected boolean isAuthenticated() {
        return getAuthenticatedUser() != null;
    }

    protected HttpServletRequest getRequest() {
        return request;
    }

    protected HttpServletResponse getResponse() {
        return response;
    }

    protected void throwBadRequestException(String message) {
        throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(message).build());
    }
}
