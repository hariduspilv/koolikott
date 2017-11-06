package ee.hm.dop.rest.administration;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.content.LearningObjectAdministrationService;

@Path("admin/deleted")
public class DeletedAdminResource extends BaseResource {

    @Inject
    private LearningObjectAdministrationService learningObjectAdministrationService;

    @POST
    @Path("portfolio/restore")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public LearningObject restore(Portfolio portfolio) {
       return learningObjectAdministrationService.restore(portfolio, getLoggedInUser());
    }

    @POST
    @Path("material/restore")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN})
    public LearningObject restore(Material material) {
        return learningObjectAdministrationService.restore(material, getLoggedInUser());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public List<AdminLearningObject> getDeletedPortfolios() {
        return learningObjectAdministrationService.findByIdDeleted();
    }

    @GET
    @Path("count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public Long getDeletedPortfoliosCount() {
        return learningObjectAdministrationService.findCountByIdDeleted();
    }
}