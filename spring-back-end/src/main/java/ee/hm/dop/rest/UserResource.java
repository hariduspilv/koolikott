package ee.hm.dop.rest;

import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.model.user.UserLocation;
import ee.hm.dop.model.user.UserNameDto;
import ee.hm.dop.service.useractions.AuthenticatedUserService;
import ee.hm.dop.service.useractions.UserService;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

@RestController
@RequestMapping("user")
public class UserResource extends BaseResource {

    @Inject
    private UserService userService;
    @Inject
    private AuthenticatedUserService authenticatedUserService;

    @GetMapping
    public UserNameDto get(@RequestParam("username") String username) {
        if (isBlank(username)) {
            throw badRequest("Username parameter is mandatory.");
        }
        return userService.getUserName(username);
    }

    @GetMapping(value = "getSignedUserData", produces = MediaType.TEXT_PLAIN_VALUE)
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.RESTRICTED, RoleString.MODERATOR})
    public String getSignedUserData() {
        return authenticatedUserService.signUserData(getAuthenticatedUser());
    }

    @GetMapping(value = "role", produces = MediaType.TEXT_PLAIN_VALUE)
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.RESTRICTED, RoleString.MODERATOR})
    public String getLoggedInUserRole() {
        return getLoggedInUser().getRole().toString();
    }

    @GetMapping(value = "getLocation", produces = MediaType.TEXT_PLAIN_VALUE)
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.RESTRICTED, RoleString.MODERATOR})
    public String getUserLocation() {
        User loggedInUser = getLoggedInUser();
        if (isBlank(loggedInUser.getLocation())) {
            throw badRequest("Username parameter is mandatory.");
        }
        return loggedInUser.getLocation();
    }

    @PostMapping("saveLocation")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.RESTRICTED, RoleString.MODERATOR})
    public User updateUserLocation(@RequestBody UserLocation userLocation) {
        return userService.updateUserLocation(getLoggedInUser(), userLocation.getLocation());
    }

    @GetMapping("areLicencesAcceptable")
    public boolean areLicencesAcceptable(@RequestParam("id") Long id) {
        return  userService.areLicencesAcceptable(id);
    }

    @PostMapping("setLearningObjectsPrivate")
    public List<Portfolio> setUserLearningObjectsPrivate(@RequestBody User user) {
        return userService.setLearningObjectsPrivate(user);
    }

    @PostMapping("migrateLearningObjectLicences")
    public List<Portfolio> migrateUserLearningObjectLicences(@RequestBody User user) {
        return userService.migrateUserLearningObjectLicences(user);
    }
}
