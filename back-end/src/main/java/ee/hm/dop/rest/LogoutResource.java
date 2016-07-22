package ee.hm.dop.rest;

import static java.lang.String.format;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.service.LogoutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("logout")
@RolesAllowed({ "USER", "ADMIN", "RESTRICTED", "MODERATOR" })
public class LogoutResource extends BaseResource {

    private static Logger logger = LoggerFactory.getLogger(LogoutResource.class);

    @Inject
    private LogoutService logoutService;

    @POST
    public void logout() {
        AuthenticatedUser authenticatedUser = getAuthenticatedUser();
        logoutService.logout(authenticatedUser);
        logger.info(format("User %s is logged out", authenticatedUser.getUser().getUsername()));
    }
}
