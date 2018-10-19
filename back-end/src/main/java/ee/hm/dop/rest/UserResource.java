package ee.hm.dop.rest;

import static org.apache.commons.lang3.StringUtils.isBlank;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.model.User;
import ee.hm.dop.model.taxon.UserLocation;
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
            throw badRequest("Username parameter is mandatory.");
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

    @GET
    @Path("getLocation")
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.RESTRICTED, RoleString.MODERATOR})
    @Produces(MediaType.TEXT_PLAIN)
    public String getUserLocation() {
        if (isBlank(getLoggedInUser().getUserLocation())) {
            throw badRequest("User does not have saved location.");
        }
        return getLoggedInUser().getUserLocation();
    }

    @POST
    @Path("saveLocation")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public User updateUserLocation(UserLocation userLocation) {
        return userService.updateUserLocation(getLoggedInUser(), userLocation.getLocation());
    }

}
