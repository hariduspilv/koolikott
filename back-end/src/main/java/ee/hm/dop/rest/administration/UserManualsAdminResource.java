package ee.hm.dop.rest.administration;

import ee.hm.dop.model.UserManuals;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.login.UserManualsService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("admin/userManuals")
public class UserManualsAdminResource extends BaseResource {

    @Inject
    private UserManualsService userManualsService;

    @GET
    @RolesAllowed({RoleString.ADMIN})
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserManuals> getUserManuals() {
        return userManualsService.findAllUserManuals();
    }

    @POST
    @RolesAllowed({RoleString.ADMIN})
    @Produces(MediaType.APPLICATION_JSON)
    public UserManuals saveUserManual(UserManuals userManuals) {
        return userManualsService.save(userManuals, getLoggedInUser());
    }

    @POST
    @Path("delete")
    @RolesAllowed({RoleString.ADMIN})
    public void deleteUserManual(UserManuals userManuals) {
        userManualsService.deleteUserManual(userManuals, getLoggedInUser());
    }
}
