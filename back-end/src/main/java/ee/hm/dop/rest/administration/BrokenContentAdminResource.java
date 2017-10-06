package ee.hm.dop.rest.administration;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.reviewmanagement.BrokenContentService;
import ee.hm.dop.service.content.LearningObjectAdministrationService;
import ee.hm.dop.service.reviewmanagement.ReviewManager;

@Path("admin/brokenContent/")
public class BrokenContentAdminResource extends BaseResource {

    @Inject
    private ReviewManager reviewManager;
    @Inject
    private BrokenContentService brokenContentService;

    @GET
    @Path("getBroken")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    @Produces(MediaType.APPLICATION_JSON)
    public List<BrokenContent> getBrokenMaterial() {
        return brokenContentService.getBrokenMaterials(getLoggedInUser());
    }

    @GET
    @Path("getBroken/count")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    @Produces(MediaType.APPLICATION_JSON)
    public Long getBrokenMaterialCount() {
        return brokenContentService.getBrokenMaterialCount(getLoggedInUser());
    }

    @GET
    @Path("isBroken")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public Boolean isBroken(@QueryParam("materialId") long materialId) {
        return brokenContentService.isBroken(materialId);
    }

    @POST
    @Path("setNotBroken")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public void setNotBroken(Material material) {
        reviewManager.setEverythingReviewedRefreshLO(getLoggedInUser(), material, ReviewStatus.ACCEPTED);
    }
}
