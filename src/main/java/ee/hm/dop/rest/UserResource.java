package ee.hm.dop.rest;

import ee.hm.dop.model.User;
import ee.hm.dop.service.AuthenticatedUserService;
import ee.hm.dop.service.UserService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Path("user")
public class UserResource {

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

        User user = userService.getUserByUsername(username);
        User newUser = null;

        if (user != null) {
            // Return only some fields
            newUser = new User();
            newUser.setId(user.getId());
            newUser.setUsername(user.getUsername());
            newUser.setName(user.getName());
            newUser.setSurname(user.getSurname());
        }

        return newUser;
    }


    @GET
    @Path("getSignedUserData")
    @Produces(MediaType.APPLICATION_JSON)
    public String getSignedUserData(@QueryParam("token") String token) {
        if (isBlank(token)) {
            throwBadRequestException("Valid authenticated user token parameter is mandatory");
        }

        return authenticatedUserService.getSignedUserData(token);
    }

    private void throwBadRequestException(String message) {
        throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(message).build());
    }
}
