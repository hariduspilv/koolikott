package ee.hm.dop.rest;

import ee.hm.dop.model.EmailToCreator;
import ee.hm.dop.model.UserEmail;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.login.UserEmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

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


    @GetMapping("getEmail")
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public UserEmail userHasEmail(@RequestParam("userId") int userId) {
        return userEmailService.getUserEmail(userId);
    }

    @PostMapping("sendEmailToCreator")
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public EmailToCreator saveEmailForCreator(@RequestBody EmailToCreator emailToCreator) {
        return userEmailService.sendEmailForCreator(emailToCreator, getLoggedInUser());
    }
}