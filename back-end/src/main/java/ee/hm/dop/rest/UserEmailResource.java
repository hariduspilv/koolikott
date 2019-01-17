package ee.hm.dop.rest;

import ee.hm.dop.model.UserEmail;
import ee.hm.dop.service.login.UserEmailService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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


}
