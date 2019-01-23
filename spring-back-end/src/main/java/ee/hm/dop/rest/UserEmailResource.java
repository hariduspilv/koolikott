package ee.hm.dop.rest;

import ee.hm.dop.model.UserEmail;
import ee.hm.dop.service.UserEmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;

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


}