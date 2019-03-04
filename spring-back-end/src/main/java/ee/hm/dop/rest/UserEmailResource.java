package ee.hm.dop.rest;

import ee.hm.dop.model.EmailToCreator;
import ee.hm.dop.model.UserEmail;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.login.UserEmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@RestController
@RequestMapping("/userEmail")
public class UserEmailResource extends BaseResource {

    @Inject
    private UserEmailService userEmailService;

    @PostMapping
    public UserEmail saveUserEmail(@RequestBody UserEmail userEmail) {
        return userEmailService.save(userEmail);
    }

    @PostMapping("check")
    public ResponseEntity<?> validateEmail(@RequestBody UserEmail userEmail) {
        if (userEmailService.hasDuplicateEmail(userEmail)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("validate")
    public UserEmail validatePin(@RequestBody UserEmail userEmail) {
        return userEmailService.validatePin(userEmail);
    }

    @GET
    @Path("getEmail")
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public UserEmail userHasEmail(@QueryParam("userId") int userId) {
        return userEmailService.getUserEmail(userId);
    }

    @POST
    @Path("sendEmailForCreator")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public EmailToCreator saveEmailForCreator(EmailToCreator emailToCreator) {

        return userEmailService.sendEmailForCreator(emailToCreator, getLoggedInUser());

    }


}