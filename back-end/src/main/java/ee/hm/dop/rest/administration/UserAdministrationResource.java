package ee.hm.dop.rest.administration;

import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.useractions.UserService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;


@Path("user")
public class UserAdministrationResource extends BaseResource {

    @Inject
    private UserService userService;

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
