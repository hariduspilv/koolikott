package ee.hm.dop.rest.administration;

import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.useractions.UserService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("admin/restrictedUser/")
public class RestrictedUserAdminResource extends BaseResource{

    @Inject
    private UserService userService;

    @GET
    @RolesAllowed(RoleString.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getRestrictedUsers() {
        return userService.getRestrictedUsers(getLoggedInUser());
    }

    @GET
    @Path("count")
    @RolesAllowed(RoleString.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Long getRestrictedUsersCount() {
        return userService.getRestrictedUsersCount(getLoggedInUser()).longValue();
    }

}
