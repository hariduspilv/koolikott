package ee.hm.dop.rest;

import ee.hm.dop.model.EmailToCreator;
import ee.hm.dop.model.UserEmail;
import ee.hm.dop.model.administration.DopPage;
import ee.hm.dop.model.administration.PageableQuerySentEmails;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.login.UserEmailService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
    @Path("getEmailOnLogin")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getEmailOnLogin(UserEmail userEmail) {
        return userEmailService.hasEmail(userEmail) ? Response.status(HttpURLConnection.HTTP_OK).build() : Response.status(HttpURLConnection.HTTP_NOT_FOUND).build();
    }

    @POST
    @Path("check")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response validateEmail(UserEmail userEmail) {
        return userEmailService.hasDuplicateEmail(userEmail) ? Response.status(HttpURLConnection.HTTP_CONFLICT).build() : Response.status(HttpURLConnection.HTTP_OK).build();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getUserEmail() {
        return userEmailService.getEmail(getLoggedInUser());
    }


    @POST
    @Path("checkForProfile")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response validateEmailForProfile(UserEmail userEmail) {
        return userEmailService.hasDuplicateEmailForProfile(userEmail, getLoggedInUser()) ? Response.status(HttpURLConnection.HTTP_CONFLICT).build() : Response.status(HttpURLConnection.HTTP_OK).build();
    }

    @POST
    @Path("validate")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public UserEmail validatePin(UserEmail userEmail) {
        return userEmailService.validatePin(userEmail);
    }

    @POST
    @Path("validateFromProfile")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public UserEmail validatePinFromProfile(UserEmail userEmail) {
        return userEmailService.validatePinFromProfile(userEmail);
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

    @GET
    @Path("sentEmails")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    @Produces(MediaType.APPLICATION_JSON)
    public DopPage getSentEmails(@QueryParam("page") int page,
                                 @QueryParam("itemSortedBy") String itemSortedBy,
                                 @QueryParam("query") String query,
                                 @QueryParam("lang") int lang) {
        PageableQuerySentEmails pageableQuery = new PageableQuerySentEmails(page, itemSortedBy, query, lang);
        if (!pageableQuery.isValid()) {
            throw badRequest("Query parameters invalid");
        }
        return userEmailService.getUserEmail(getLoggedInUser(), pageableQuery);
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public Long getSentEmailsCount() {
        return userEmailService.getSentEmailsCount(getLoggedInUser());
    }
}
