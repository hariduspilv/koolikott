package ee.hm.dop.rest.administration;

import ee.hm.dop.model.Material;
import ee.hm.dop.model.Recommendation;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.content.LearningObjectAdministrationService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("material")
public class MaterialAdminResource extends BaseResource {


    @Inject
    private LearningObjectAdministrationService learningObjectAdministrationService;

    @POST
    @Path("recommend")
    @RolesAllowed({RoleString.ADMIN})
    public Recommendation recommendMaterial(Material material) {
        return learningObjectAdministrationService.addRecommendation(material, getLoggedInUser());
    }

    @POST
    @Path("removeRecommendation")
    @RolesAllowed({RoleString.ADMIN})
    public void removedMaterialRecommendation(Material material) {
        learningObjectAdministrationService.removeRecommendation(material, getLoggedInUser());
    }
}
