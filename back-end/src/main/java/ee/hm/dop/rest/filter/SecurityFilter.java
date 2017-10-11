package ee.hm.dop.rest.filter;

import static ee.hm.dop.config.guice.GuiceInjector.getInjector;

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
import ee.hm.dop.service.useractions.AuthenticatedUserService;
import ee.hm.dop.service.login.LogoutService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class SecurityFilter implements ContainerRequestFilter {

    private static final int HTTP_AUTHENTICATION_TIMEOUT = 419;
    public Logger logger = LoggerFactory.getLogger(getClass());

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
            AuthenticatedUser authenticatedUser = authenticatedUserService().getAuthenticatedUserByToken(token);
            if (authenticatedUser == null) {
                userHasAlreadyLoggedOut(requestContext);
                logger.error("user has already logged out");
                return;
            }
            String username = authenticatedUser.getUser().getUsername();
            if (!username.equals(request.getHeader("Username"))) {
                requestHeaderAndUsernameDontMatch(requestContext, authenticatedUser);
                logger.error("user request header and username do not match: " + username);
                return;
            }
            if (!isSessionValid(authenticatedUser)) {
                sessionExpired(requestContext, authenticatedUser);
                logger.error("session has expired" + username);
            } else {
                DopPrincipal principal = new DopPrincipal(authenticatedUser);
                DopSecurityContext securityContext = new DopSecurityContext(principal, uriInfo);
                requestContext.setSecurityContext(securityContext);
            }
        }
    }

    public void sessionExpired(ContainerRequestContext requestContext, AuthenticatedUser authenticatedUser) {
//        newLogoutService().logout(authenticatedUser);
        requestContext.abortWith(Response.status(HTTP_AUTHENTICATION_TIMEOUT).build());
    }

    public void requestHeaderAndUsernameDontMatch(ContainerRequestContext requestContext, AuthenticatedUser authenticatedUser) {
        requestContext.abortWith(Response.status(HTTP_AUTHENTICATION_TIMEOUT).build());
    }

    public void userHasAlreadyLoggedOut(ContainerRequestContext requestContext) {
        requestContext.abortWith(Response.status(HTTP_AUTHENTICATION_TIMEOUT).build());
    }

    private boolean isSessionValid(AuthenticatedUser authenticatedUser) {
        DateTime yesterday = DateTime.now().minusDays(1);
        return yesterday.isBefore(authenticatedUser.getLoginDate());
    }

    protected AuthenticatedUserService authenticatedUserService() {
        return getInjector().getInstance(AuthenticatedUserService.class);
    }

    protected LogoutService newLogoutService() {
        return getInjector().getInstance(LogoutService.class);
    }
}
