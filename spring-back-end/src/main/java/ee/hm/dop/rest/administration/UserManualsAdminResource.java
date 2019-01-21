package ee.hm.dop.rest.administration;

import ee.hm.dop.model.UserManuals;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.login.UserManualsService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@RestController
@RequestMapping("admin/userManuals")
public class UserManualsAdminResource extends BaseResource {

    @Inject
    private UserManualsService userManualsService;

    @GetMapping
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserManuals> getUserManuals() {
        return userManualsService.findAllUserManuals();
    }

    @PostMapping
    @Secured({RoleString.ADMIN})
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public UserManuals saveUserManual(UserManuals userManuals) {
        return userManualsService.save(userManuals, getLoggedInUser());
    }

    @PostMapping
    @RequestMapping("delete")
    @Secured({RoleString.ADMIN})
    public void deleteUserManual(UserManuals userManuals) {
        userManualsService.deleteUserManual(userManuals, getLoggedInUser());
    }
}
