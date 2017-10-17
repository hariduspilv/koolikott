package ee.hm.dop.rest.filter;

import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.service.login.LogoutService;
import ee.hm.dop.service.useractions.AuthenticatedUserService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

import static ee.hm.dop.config.guice.GuiceInjector.getInjector;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class SecurityFilter implements ContainerRequestFilter {

    public static final int HTTP_AUTHENTICATION_TIMEOUT = 419;
    public Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String token = requestContext.getHeaderString("Authentication");

        if (token != null) {
            AuthenticatedUser authenticatedUser = authenticatedUserService().getAuthenticatedUserByToken(token);
            if (authenticatedUser == null) {
                userHasAlreadyLoggedOut(requestContext);
                logger.error("user has already logged out");
                return;
            }
            String username = authenticatedUser.getUser().getUsername();
            if (!username.equals(requestContext.getHeaderString("Username"))) {
                requestHeaderAndUsernameDontMatch(requestContext, authenticatedUser);
                logger.error("user request header and username do not match: " + username);
                return;
            }
            if (!isSessionValid(authenticatedUser)) {
                sessionExpired(requestContext, authenticatedUser);
                logger.error("session has expired" + username);
                return;
            }

            DopPrincipal principal = new DopPrincipal(authenticatedUser);
            DopSecurityContext securityContext = new DopSecurityContext(principal, requestContext.getUriInfo());
            requestContext.setSecurityContext(securityContext);
        }
    }

    public void sessionExpired(ContainerRequestContext requestContext, AuthenticatedUser authenticatedUser) {
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
}
