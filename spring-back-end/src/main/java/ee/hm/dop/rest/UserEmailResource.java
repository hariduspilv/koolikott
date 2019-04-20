package ee.hm.dop.rest;

import ee.hm.dop.model.EmailToCreator;
import ee.hm.dop.model.SearchResult;
import ee.hm.dop.model.UserEmail;
import ee.hm.dop.model.administration.PageableQuery;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.login.UserEmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("getEmailOnLogin")
    public ResponseEntity<?> getEmailOnLogin(@RequestBody UserEmail userEmail) {
        if (userEmailService.hasEmail(userEmail)) {
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("check")
    public ResponseEntity<?> validateEmail(@RequestBody UserEmail userEmail) {
        if (userEmailService.hasDuplicateEmail(userEmail)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("checkForProfile")
    public ResponseEntity<?> validateEmailForProfile(@RequestBody UserEmail userEmail) {
        if (userEmailService.hasDuplicateEmailForProfile(userEmail, getLoggedInUser())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("validate")
    public UserEmail validatePin(@RequestBody UserEmail userEmail) {
        return userEmailService.validatePin(userEmail);
    }

    @PostMapping("validateFromProfile")
    public UserEmail validatePinFromProfile(@RequestBody UserEmail userEmail) {
        return userEmailService.validatePinFromProfile(userEmail);
    }

    @GetMapping
    public String getUserEmail() {
        return userEmailService.getEmail(getLoggedInUser());
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

    @GetMapping("sentEmails")
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public SearchResult getSentEmails(@RequestParam("page") int page,
                                      @RequestParam("itemSortedBy") String itemSortedBy,
                                      @RequestParam("query") String query,
                                      @RequestParam("lang") int lang) {
        PageableQuery pageableQuery = new PageableQuery(page, itemSortedBy, query, lang);
        if (!pageableQuery.isValid()) {
            throw badRequest("Query parameters invalid");
        }
        return userEmailService.getUserEmail(getLoggedInUser(),pageableQuery);
    }

    @GetMapping("/count")
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public Long getSentEmailsCount() {
        return userEmailService.getSentEmailsCount(getLoggedInUser());
    }
}