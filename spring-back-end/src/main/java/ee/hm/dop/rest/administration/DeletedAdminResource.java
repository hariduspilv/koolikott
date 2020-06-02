package ee.hm.dop.rest.administration;

import ee.hm.dop.model.AdminLearningObject;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.LearningObjectMiniDto;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.content.LearningObjectAdministrationService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

@RestController
@RequestMapping("admin/deleted")
public class DeletedAdminResource extends BaseResource {

    @Inject
    private LearningObjectAdministrationService learningObjectAdministrationService;

    @PostMapping
    @RequestMapping("restore")
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public LearningObject restore(@RequestBody LearningObjectMiniDto loDto) {
       return learningObjectAdministrationService.restore(loDto.convert(), getLoggedInUser());
    }

    @GetMapping
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public List<AdminLearningObject> getDeletedLearningObjects() {
        return learningObjectAdministrationService.findByIdDeleted();
    }

    @GetMapping
    @RequestMapping("count")
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public Long getDeletedLearningObjectsCount() {
        return learningObjectAdministrationService.findCountByIdDeleted();
    }
}