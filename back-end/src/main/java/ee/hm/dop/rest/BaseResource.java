package ee.hm.dop.rest;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Encoded;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.User;
import ee.hm.dop.rest.filter.DopPrincipal;
import ee.hm.dop.utils.ConfigurationProperties;
import org.apache.commons.configuration.Configuration;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class BaseResource {

    public static final String UTF_8 = "UTF-8";
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

    protected Response ok() {
        return Response.ok().build();
    }

    protected Response ok(Object data) {
        return Response.ok(data).build();
    }

    protected User getLoggedInUser() {
        AuthenticatedUser authenticatedUser = getAuthenticatedUser();
        return authenticatedUser != null ? authenticatedUser.getUser() : null;
    }

    protected AuthenticatedUser getAuthenticatedUser() {
        DopPrincipal dopPrincipal = (DopPrincipal) securityContext.getUserPrincipal();
        return dopPrincipal != null ? dopPrincipal.getAuthenticatedUser() : null;
    }

    public static String decode(String string) throws UnsupportedEncodingException {
        return URLDecoder.decode(string, UTF_8);
    }

    protected HttpServletRequest getRequest() {
        return request;
    }

    protected HttpServletResponse getResponse() {
        return response;
    }

    public WebApplicationException badRequest(String message) {
        return new WebApplicationException(Response.status(HTTP_BAD_REQUEST).entity(message).build());
    }

    public WebApplicationException notFound() {
        return new WebApplicationException(Response.status(HTTP_NOT_FOUND).build());
    }

    protected String getServerAddress() {
        return configuration.getString(ConfigurationProperties.SERVER_ADDRESS);
    }
}
