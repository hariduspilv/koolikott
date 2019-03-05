package ee.hm.dop.rest;

import ee.hm.dop.model.EmailToCreator;
import ee.hm.dop.model.UserEmail;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.login.UserEmailService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;

@Path("/userEmail")
public class UserEmailResource extends BaseResource {

    @Inject
    private UserEmailService userEmailService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public UserEmail saveUserEmail(UserEmail userEmail) {
        return userEmailService.save(userEmail);
    }

    @POST
    @Path("check")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response validateEmail(UserEmail userEmail) {
        if (userEmailService.hasDuplicateEmail(userEmail))
            return Response.status(HttpURLConnection.HTTP_CONFLICT).build();
        else
            return Response.status(HttpURLConnection.HTTP_OK).build();
    }

    @POST
    @Path("validate")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public UserEmail validatePin(UserEmail userEmail) {
        return userEmailService.validatePin(userEmail);
    }

    @GET
    @Path("getEmail")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public UserEmail userHasEmail(@QueryParam("userId") int userId) {
        return userEmailService.getUserEmail(userId);
    }

    @POST
    @Path("sendEmailToCreator")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public EmailToCreator sendEmailToCreator(EmailToCreator emailToCreator) {
        return userEmailService.sendEmailForCreator(emailToCreator, getLoggedInUser());
    }
}
