package ee.hm.dop.rest;

import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.User;
import ee.hm.dop.rest.filter.DopPrincipal;
import ee.hm.dop.service.AuthenticatedUserService;
import ee.hm.dop.service.UserService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.net.HttpURLConnection;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Path("user")
public class UserResource {

    @Inject
    private UserService userService;

    @Inject
    private AuthenticatedUserService authenticatedUserService;

    @Context
    private HttpServletRequest request;

    private SecurityContext securityContext;

    @Context
    public void setSecurityContext(SecurityContext securityContext) {
        this.securityContext = securityContext;
    }

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
    @RolesAllowed("USER")
    @Produces(MediaType.TEXT_PLAIN)
    public String getSignedUserData() {
        DopPrincipal dopPrincipal = (DopPrincipal) securityContext.getUserPrincipal();
        if (dopPrincipal == null) {
            throwBadRequestException("User needs to be logged in");
        }
        AuthenticatedUser authenticatedUser = dopPrincipal.getAuthenticatedUser();

        return authenticatedUserService.signUserData(authenticatedUser);
    }

    private void throwBadRequestException(String message) {
        throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(message).build());
    }
}
