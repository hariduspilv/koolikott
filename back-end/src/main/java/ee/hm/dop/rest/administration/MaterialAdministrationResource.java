package ee.hm.dop.rest.administration;

import ee.hm.dop.model.Material;
import ee.hm.dop.model.Recommendation;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.content.MaterialAdministrationService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("material")
public class MaterialAdministrationResource extends BaseResource {

    @Inject
    private MaterialAdministrationService materialAdministrationService;

    @POST
    @Path("recommend")
    @RolesAllowed({RoleString.ADMIN})
    public Recommendation recommendMaterial(Material material) {
        return materialAdministrationService.addRecommendation(material, getLoggedInUser());
    }

    @POST
    @Path("removeRecommendation")
    @RolesAllowed({RoleString.ADMIN})
    public void removedMaterialRecommendation(Material material) {
        materialAdministrationService.removeRecommendation(material, getLoggedInUser());
    }
}
