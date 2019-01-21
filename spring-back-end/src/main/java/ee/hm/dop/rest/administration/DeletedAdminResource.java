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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@RestController
@RequestMapping("admin/deleted")
public class DeletedAdminResource extends BaseResource {

    @Inject
    private LearningObjectAdministrationService learningObjectAdministrationService;

    @PostMapping
    @RequestMapping("restore")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public LearningObject restore(LearningObjectMiniDto loDto) {
       return learningObjectAdministrationService.restore(loDto.convert(), getLoggedInUser());
    }

    @GetMapping
    @Produces(MediaType.APPLICATION_JSON)
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public List<AdminLearningObject> getDeletedPortfolios() {
        return learningObjectAdministrationService.findByIdDeleted();
    }

    @GetMapping
    @RequestMapping("count")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public Long getDeletedPortfoliosCount() {
        return learningObjectAdministrationService.findCountByIdDeleted();
    }
}