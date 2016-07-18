package ee.hm.dop.rest;

import static org.apache.commons.lang3.StringUtils.isBlank;

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
import ee.hm.dop.model.User;
import ee.hm.dop.service.AuthenticatedUserService;
import ee.hm.dop.service.UserService;

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
    @RolesAllowed({"USER", "ADMIN", "RESTRICTED", "MODERATOR"})
    @Produces(MediaType.TEXT_PLAIN)
    public String getSignedUserData() {
        AuthenticatedUser authenticatedUser = getAuthenticatedUser();

        return authenticatedUserService.signUserData(authenticatedUser);
    }

    @GET
    @Path("role")
    @RolesAllowed({"USER", "ADMIN", "RESTRICTED", "MODERATOR"})
    @Produces(MediaType.TEXT_PLAIN)
    public String getLoggedInUserRole() {
        return getLoggedInUser().getRole().toString();
    }

    @POST
    @Path("restrictUser")
    @RolesAllowed({"ADMIN", "MODERATOR"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User restrictUser(User user) {
        if (user == null) throwBadRequestException("No user to restrict received!");
        user = userService.restrictUser(user);
        return user;
    }

    @POST
    @Path("removeRestriction")
    @RolesAllowed({"ADMIN", "MODERATOR"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User removeRestriction(User user) {
        if (user == null) throwBadRequestException("No user received!");
        User v =  userService.removeRestriction(user);

        return v;
    }
}
