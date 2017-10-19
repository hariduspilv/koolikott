package ee.hm.dop.rest.administration;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
<<<<<<< HEAD
import javax.ws.rs.core.Response;

import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.content.MaterialAdministrationService;
=======

import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.reviewmanagement.BrokenContentService;
import ee.hm.dop.service.content.LearningObjectAdministrationService;
import ee.hm.dop.service.reviewmanagement.ReviewManager;
>>>>>>> new-develop

@Path("admin/brokenContent/")
public class BrokenContentAdminResource extends BaseResource {

    @Inject
<<<<<<< HEAD
    private MaterialAdministrationService materialAdministrationService;
=======
    private ReviewManager reviewManager;
    @Inject
    private BrokenContentService brokenContentService;
>>>>>>> new-develop

    @GET
    @Path("getBroken")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    @Produces(MediaType.APPLICATION_JSON)
    public List<BrokenContent> getBrokenMaterial() {
<<<<<<< HEAD
        return materialAdministrationService.getBrokenMaterials(getLoggedInUser());
=======
        return brokenContentService.getBrokenMaterials(getLoggedInUser());
>>>>>>> new-develop
    }

    @GET
    @Path("getBroken/count")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    @Produces(MediaType.APPLICATION_JSON)
    public Long getBrokenMaterialCount() {
<<<<<<< HEAD
        return materialAdministrationService.getBrokenMaterialCount(getLoggedInUser());
=======
        return brokenContentService.getBrokenMaterialCount(getLoggedInUser());
>>>>>>> new-develop
    }

    @GET
    @Path("isBroken")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public Boolean isBroken(@QueryParam("materialId") long materialId) {
<<<<<<< HEAD
        return materialAdministrationService.isBroken(materialId);
=======
        return brokenContentService.isBroken(materialId);
>>>>>>> new-develop
    }

    @POST
    @Path("setNotBroken")
    @Produces(MediaType.APPLICATION_JSON)
<<<<<<< HEAD
    @RolesAllowed({RoleString.ADMIN})
    public void setNotBroken(Material material) {
        materialAdministrationService.setMaterialNotBroken(material, getLoggedInUser());
=======
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public void setNotBroken(Material material) {
        reviewManager.setEverythingReviewedRefreshLO(getLoggedInUser(), material, ReviewStatus.ACCEPTED);
>>>>>>> new-develop
    }
}
