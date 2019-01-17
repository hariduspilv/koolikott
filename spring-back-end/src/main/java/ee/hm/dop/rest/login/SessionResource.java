package ee.hm.dop.rest.login;

import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.model.user.UserSession;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.useractions.SessionService;
import org.apache.commons.configuration2.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static ee.hm.dop.service.login.SessionUtil.minRemaining;
import static ee.hm.dop.utils.ConfigurationProperties.SESSION_ALERT_MINS;
import static java.lang.String.format;

@Path("user")
@RolesAllowed({ RoleString.USER, RoleString.ADMIN, RoleString.RESTRICTED, RoleString.MODERATOR })
public class SessionResource extends BaseResource {

    private static Logger logger = LoggerFactory.getLogger(SessionResource.class);

    @Inject
    private SessionService sessionService;
    @Inject
    private Configuration configuration;

    @GET
    @Path("sessionTime")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.RESTRICTED, RoleString.MODERATOR})
    public UserSession getSessionTime() {
        AuthenticatedUser user = getAuthenticatedUser();
        return new UserSession(minRemaining(user), !user.isDeclined());
    }

    @GET
    @Path("sessionAlertTime")
    public String getSessionAlertTime() {
        return configuration.getString(SESSION_ALERT_MINS);
    }

    @POST
    @Path("updateSession")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.RESTRICTED, RoleString.MODERATOR})
    public UserSession updateSessionTime(UserSession userSession) {
        AuthenticatedUser user = sessionService.updateSession(userSession, getAuthenticatedUser());
        return new UserSession(minRemaining(user), !user.isDeclined());
    }

    @POST
    @Path("terminateSession")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.RESTRICTED, RoleString.MODERATOR})
    public void terminateSession() {
        sessionService.terminateSession(getAuthenticatedUser());
    }

    @POST
    @Path("logout")
    public void logout() {
        AuthenticatedUser authenticatedUser = getAuthenticatedUser();
        sessionService.logout(authenticatedUser);
        logger.info(format("User %s is logged out", authenticatedUser.getUser().getUsername()));
    }
}
