package ee.hm.dop.rest.administration;

import com.google.common.collect.Lists;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.Role;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.useractions.UserService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/moderator")
public class ModeratorAdminResource extends BaseResource {

    @Inject
    private UserService userService;

    @GetMapping
    @Secured({RoleString.ADMIN,RoleString.MODERATOR})
    public List<User> getModerators() {
        User loggedInUser = getLoggedInUser();
        if (loggedInUser.getRole() == Role.MODERATOR) {
            return Lists.newArrayList(loggedInUser);
        } else {
            return userService.getModerators(loggedInUser);
        }
    }

    @GetMapping
    @RequestMapping(value = "count")
    @Secured(RoleString.ADMIN)
    public Long getModeratorsCount() {
        return userService.getModeratorsCount(getLoggedInUser());
    }
}
