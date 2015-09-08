package ee.hm.dop.rest.filter;

import static ee.hm.dop.guice.GuiceInjector.getInjector;

import java.io.IOException;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.service.AuthenticatedUserService;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class SecurityFilter implements ContainerRequestFilter {

    private static final int HTTP_AUTHENTICATION_TIMEOUT = 419;

    private UriInfo uriInfo;
    private HttpServletRequest request;

    public SecurityFilter(@Context UriInfo uriInfo, @Context HttpServletRequest request) {
        this.uriInfo = uriInfo;
        this.request = request;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String token = request.getHeader("Authentication");

        if (token != null) {
            AuthenticatedUserService authenticatedUserService = newAuthenticatedUserService();
            AuthenticatedUser authenticatedUser = authenticatedUserService.getAuthenticatedUserByToken(token);
            if (authenticatedUser != null && isCorrectUser(authenticatedUser)) {
                DopPrincipal principal = new DopPrincipal(authenticatedUser);
                DopSecurityContext securityContext = new DopSecurityContext(principal, uriInfo);
                requestContext.setSecurityContext(securityContext);
            } else {
                requestContext.abortWith(Response.status(HTTP_AUTHENTICATION_TIMEOUT).build());
            }
        }

    }

    protected AuthenticatedUserService newAuthenticatedUserService() {
        return getInjector().getInstance(AuthenticatedUserService.class);
    }

    private boolean isCorrectUser(AuthenticatedUser authenticatedUser) {
        return authenticatedUser.getUser().getUsername().equals(request.getHeader("Username"));
    }

}
