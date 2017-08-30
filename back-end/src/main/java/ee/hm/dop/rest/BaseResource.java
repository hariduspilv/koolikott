package ee.hm.dop.rest;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.User;
import ee.hm.dop.rest.filter.DopPrincipal;
import ee.hm.dop.utils.ConfigurationProperties;
import org.apache.commons.configuration.Configuration;

public class BaseResource {

    @Context
    private HttpServletRequest request;
    @Context
    private HttpServletResponse response;
    @Inject
    private Configuration configuration;
    private SecurityContext securityContext;

    @Context
    public void setSecurityContext(SecurityContext securityContext) {
        this.securityContext = securityContext;
    }

    protected User getLoggedInUser() {
        AuthenticatedUser authenticatedUser = getAuthenticatedUser();
        if (authenticatedUser != null) {
            return authenticatedUser.getUser();
        }
        return null;
    }

    protected AuthenticatedUser getAuthenticatedUser() {
        DopPrincipal dopPrincipal = (DopPrincipal) securityContext.getUserPrincipal();

        AuthenticatedUser authenticatedUser = null;
        if (dopPrincipal != null) {
            authenticatedUser = dopPrincipal.getAuthenticatedUser();
        }

        return authenticatedUser;
    }

    protected HttpServletRequest getRequest() {
        return request;
    }

    protected HttpServletResponse getResponse() {
        return response;
    }

    protected void throwBadRequestException(String message) {
        throw new WebApplicationException(Response.status(HTTP_BAD_REQUEST).entity(message).build());
    }

    protected void throwNotFoundException(String message) {
        throw new WebApplicationException(Response.status(HTTP_NOT_FOUND).entity(message).build());
    }

    protected void throwNotFoundException() {
        throw new WebApplicationException(Response.status(HTTP_NOT_FOUND).build());
    }

    protected String getServerAddress() {
        return configuration.getString(ConfigurationProperties.SERVER_ADDRESS);
    }
}
