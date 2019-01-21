package ee.hm.dop.rest.administration;

import ee.hm.dop.model.LearningObjectMiniDto;
import ee.hm.dop.model.Recommendation;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.content.LearningObjectAdministrationService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@RestController
@RequestMapping("admin/learningObject")
public class LearningObjectAdminResource extends BaseResource{

    @Inject
    private LearningObjectAdministrationService learningObjectAdministrationService;

    @PostMapping("recommend")
    @Secured({RoleString.ADMIN})
    public Recommendation recommendMaterial(@RequestBody LearningObjectMiniDto loDto) {
        return learningObjectAdministrationService.addRecommendation(loDto.convert(), getLoggedInUser());
    }

    @PostMapping("removeRecommendation")
    @Secured({RoleString.ADMIN})
    public void removedMaterialRecommendation(@RequestBody LearningObjectMiniDto loDto) {
        learningObjectAdministrationService.removeRecommendation(loDto.convert(), getLoggedInUser());
    }
}
