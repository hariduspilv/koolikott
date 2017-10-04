package ee.hm.dop.rest.administration;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.content.LearningObjectAdministrationService;
import ee.hm.dop.service.content.MaterialAdministrationService;

@Path("admin/brokenContent/")
public class BrokenContentAdminResource extends BaseResource {

    @Inject
    private MaterialAdministrationService materialAdministrationService;
    @Inject
    private LearningObjectAdministrationService learningObjectAdministrationService;

    @GET
    @Path("getBroken")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    @Produces(MediaType.APPLICATION_JSON)
    public List<BrokenContent> getBrokenMaterial() {
        return materialAdministrationService.getBrokenMaterials(getLoggedInUser());
    }

    @GET
    @Path("getBroken/count")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    @Produces(MediaType.APPLICATION_JSON)
    public Long getBrokenMaterialCount() {
        return materialAdministrationService.getBrokenMaterialCount(getLoggedInUser());
    }

    @GET
    @Path("isBroken")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public Boolean isBroken(@QueryParam("materialId") long materialId) {
        return materialAdministrationService.isBroken(materialId);
    }

    @POST
    @Path("setNotBroken")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN})
    public void setNotBroken(Material material) {
        learningObjectAdministrationService.setEverythingReviewedRefreshLO(getLoggedInUser(), material, ReviewStatus.ACCEPTED);
    }
}
