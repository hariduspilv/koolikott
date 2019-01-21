package ee.hm.dop.rest;

import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.model.user.UserLocation;
import ee.hm.dop.service.useractions.AuthenticatedUserService;
import ee.hm.dop.service.useractions.UserService;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

import static org.apache.commons.lang3.StringUtils.isBlank;

@RestController
@RequestMapping("user")
public class UserResource extends BaseResource {

    @Inject
    private UserService userService;
    @Inject
    private AuthenticatedUserService authenticatedUserService;

    @GetMapping
    @RequestMapping
    public User get(@RequestParam("username") String username) {
        if (isBlank(username)) {
            throw badRequest("Username parameter is mandatory.");
        }
        return userService.getUserByUsername(username);
    }

    @GetMapping
    @RequestMapping(value = "getSignedUserData", produces = MediaType.TEXT_PLAIN_VALUE)
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.RESTRICTED, RoleString.MODERATOR})
    public String getSignedUserData() {
        return authenticatedUserService.signUserData(getAuthenticatedUser());
    }

    @GetMapping
    @RequestMapping(value = "role", produces = MediaType.TEXT_PLAIN_VALUE)
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.RESTRICTED, RoleString.MODERATOR})
    public String getLoggedInUserRole() {
        return getLoggedInUser().getRole().toString();
    }

    @GetMapping
    @RequestMapping(value = "getLocation", produces = MediaType.TEXT_PLAIN_VALUE)
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.RESTRICTED, RoleString.MODERATOR})
    public String getUserLocation() {
        User loggedInUser = getLoggedInUser();
        if (isBlank(loggedInUser.getLocation())) {
            throw badRequest("User does not have saved location.");
        }
        return loggedInUser.getLocation();
    }

    @PostMapping
    @RequestMapping("saveLocation")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.RESTRICTED, RoleString.MODERATOR})
    public User updateUserLocation(@RequestBody UserLocation userLocation) {
        return userService.updateUserLocation(getLoggedInUser(), userLocation.getLocation());
    }

}
