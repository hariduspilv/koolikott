package ee.hm.dop.rest.administration;

import ee.hm.dop.dao.UserDao;
import ee.hm.dop.model.User;
import ee.hm.dop.model.administration.DopPage;
import ee.hm.dop.model.administration.PageableQueryUsers;
import ee.hm.dop.model.enums.Role;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.model.enums.UserRole;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.useractions.UserService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

@RestController
@RequestMapping("user")
public class UserAdminResource extends BaseResource {

    @Inject
    private UserService userService;
    @Inject
    private UserDao userDao;

    @GetMapping
    @RequestMapping("all")
    @Secured(RoleString.ADMIN)
    public DopPage getAll(@RequestParam(value = "page", defaultValue = "1") int page,
                          @RequestParam(value = "itemSortedBy", required = false) String itemSortedBy,
                          @RequestParam(value = "query", required = false) String query,
                          @RequestParam(value = "role", required = false) String role,
                          @RequestParam(value = "userRole", required = false) String userRole,
                          @RequestParam(value = "userEducationalContext", required = false) String userEducationalContext,
                          @RequestParam(value = "withEmail", required = false) boolean withEmail,
                          @RequestParam(value = "withoutEmail", required = false) boolean withoutEmail,
                          @RequestParam(value = "language", defaultValue = "est") String languageCode) {
        PageableQueryUsers pageableQuery = new PageableQueryUsers(page, itemSortedBy, query, role, userRole, userEducationalContext, withEmail, withoutEmail, languageCode, 25);
        return userService.getAllUsers(getLoggedInUser(), pageableQuery);
    }

    @GetMapping("allUsers/count")
    @Secured(RoleString.ADMIN)
    public Long getAllUsersCount() {
        return userService.getAllUsersCount();
    }


    @GetMapping("id")
    @Secured(RoleString.ADMIN)
    public User getUserById(@RequestParam("id") Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("roles")
    @Secured(RoleString.ADMIN)
    public List<Role> getUserRoles() {
        return userService.getUserRoles();
    }

    @GetMapping("selectedRoles")
    @Secured(RoleString.ADMIN)
    public List<UserRole> getUserSelectedRoles() {
        return userService.getUserSelectedRoles();
    }

    @PostMapping
    @Secured(RoleString.ADMIN)
    public User updateUser(@RequestBody User user) {
        mustHaveUser(user);
        return userService.update(user, getLoggedInUser());
    }

    @PostMapping
    @RequestMapping("restrictUser")
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public User restrictUser(@RequestBody User user) {
        mustHaveUser(user);
        return userService.restrictUser(user);
    }

    @PostMapping
    @RequestMapping("removeRestriction")
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public User removeRestriction(@RequestBody User user) {
        mustHaveUser(user);
        return userService.removeRestriction(user);
    }

    @GetMapping
    @RequestMapping("getUser")
    @Secured({RoleString.ADMIN})
    public User getUser (@RequestParam("id") Long userId) {
        return userDao.findUserById(userId);
    }

    private void mustHaveUser(User user) {
        if (user == null) throw badRequest("No user received!");
    }
}
