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
    @Path("all")
    @RolesAllowed(RoleString.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getAll() {
        return userService.getAllUsers(getLoggedInUser());
    }

    @POST
    @RolesAllowed(RoleString.ADMIN)
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

    @POST
    @Path("restrictUser")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User restrictUser(User user) {
        if (user == null) throwBadRequestException("No user to restrict received!");
        return userService.restrictUser(user);
    }

    @POST
    @Path("removeRestriction")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User removeRestriction(User user) {
        if (user == null) throwBadRequestException("No user received!");
        return userService.removeRestriction(user);
    }

    @GET
    @Path("moderator")
    @RolesAllowed(RoleString.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getModerators() {
        return userService.getModerators(getLoggedInUser());
    }

    @GET
    @Path("restrictedUser")
    @RolesAllowed(RoleString.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getRestrictedUsers() {
        return userService.getRestrictedUsers(getLoggedInUser());
    }

    @GET
    @Path("moderator/count")
    @RolesAllowed(RoleString.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    public int getModeratorsCount() {
        return userService.getModerators(getLoggedInUser()).size();
    }

    @GET
    @Path("restrictedUser/count")
    @RolesAllowed(RoleString.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    public int getRestrictedUsersCount() {
        return userService.getRestrictedUsers(getLoggedInUser()).size();
    }
}
