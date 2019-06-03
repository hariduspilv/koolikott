package ee.hm.dop.rest;

import ee.hm.dop.model.UserProfile;
import ee.hm.dop.service.useractions.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@RestController
@RequestMapping("/userProfile")
public class UserProfileResource extends BaseResource {

    @Inject
    private UserProfileService userProfileService;

    @PostMapping
    public ResponseEntity<?> updateUserProfile(@RequestBody UserProfile userProfile) {
        return userProfileService.update(userProfile, getLoggedInUser());
    }

    @GetMapping
    public ResponseEntity<UserProfile> getUserProfile() {
        return userProfileService.getUserProfile(getLoggedInUser());
    }

}
