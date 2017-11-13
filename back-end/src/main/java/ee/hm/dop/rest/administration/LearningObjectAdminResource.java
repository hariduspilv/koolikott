package ee.hm.dop.rest.administration;

import ee.hm.dop.model.LearningObjectMiniDto;
import ee.hm.dop.model.Recommendation;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.content.LearningObjectAdministrationService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("admin/learningObject")
public class LearningObjectAdminResource extends BaseResource{

    @Inject
    private LearningObjectAdministrationService learningObjectAdministrationService;

    @POST
    @Path("recommend")
    @RolesAllowed({RoleString.ADMIN})
    public Recommendation recommendMaterial(LearningObjectMiniDto loDto) {
        return learningObjectAdministrationService.addRecommendation(loDto.convert(), getLoggedInUser());
    }

    @POST
    @Path("removeRecommendation")
    @RolesAllowed({RoleString.ADMIN})
    public void removedMaterialRecommendation(LearningObjectMiniDto loDto) {
        learningObjectAdministrationService.removeRecommendation(loDto.convert(), getLoggedInUser());
    }
}
