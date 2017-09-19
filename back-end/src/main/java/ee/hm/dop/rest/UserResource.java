package ee.hm.dop.rest;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.model.User;
import ee.hm.dop.service.useractions.AuthenticatedUserService;
import ee.hm.dop.service.useractions.UserService;

@Path("user")
public class UserResource extends BaseResource {

    @Inject
    private UserService userService;
    @Inject
    private AuthenticatedUserService authenticatedUserService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public User get(@QueryParam("username") String username) {
        if (isBlank(username)) {
            throwBadRequestException("Username parameter is mandatory.");
        }
        return userService.getUserByUsername(username);
    }

    @GET
    @Path("getSignedUserData")
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.RESTRICTED, RoleString.MODERATOR})
    @Produces(MediaType.TEXT_PLAIN)
    public String getSignedUserData() {
        return authenticatedUserService.signUserData(getAuthenticatedUser());
    }

    @GET
    @Path("role")
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.RESTRICTED, RoleString.MODERATOR})
    @Produces(MediaType.TEXT_PLAIN)
    public String getLoggedInUserRole() {
        return getLoggedInUser().getRole().toString();
    }

}
