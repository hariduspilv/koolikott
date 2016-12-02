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
    @Path("all")
    @RolesAllowed("ADMIN")
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getAll() {
        return userService.getAllUsers(getLoggedInUser());
    }

    @POST
    @RolesAllowed("ADMIN")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User updateUser(User user) {
        if (user == null) throwBadRequestException("No user received!");
        return userService.update(user, getLoggedInUser());
    }

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
        return userService.restrictUser(user);
    }

    @POST
    @Path("removeRestriction")
    @RolesAllowed({"ADMIN", "MODERATOR"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User removeRestriction(User user) {
        if (user == null) throwBadRequestException("No user received!");
        return userService.removeRestriction(user);
    }

    @GET
    @Path("moderator")
    @RolesAllowed("ADMIN")
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getModerators() {
        return userService.getModerators(getLoggedInUser());
    }

    @GET
    @Path("restrictedUser")
    @RolesAllowed("ADMIN")
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getRestrictedUsers() {
        return userService.getRestrictedUsers(getLoggedInUser());
    }
}
