package ee.hm.dop.rest.administration;

import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.model.enums.ReviewType;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.reviewmanagement.ImproperContentAdminService;
import ee.hm.dop.service.reviewmanagement.ImproperContentService;
import ee.hm.dop.service.reviewmanagement.ReviewManager;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@RestController
@RequestMapping("admin/improper")
public class ImproperContentAdminResource extends BaseResource {

    @Inject
    private ImproperContentAdminService improperContentAdminService;
    @Inject
    private ReviewManager reviewManager;
    @Inject
    private ImproperContentService improperContentService;

    @GetMapping
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public List<AdminLearningObject> getImproper() {
        return improperContentAdminService.getImproper(getLoggedInUser());
    }

    @GetMapping
    @RequestMapping("/count")
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public Long getImproperCount() {
        return improperContentAdminService.getImproperCount(getLoggedInUser());
    }

    @PostMapping
    @RequestMapping("setProper")
    @Secured({RoleString.MODERATOR, RoleString.ADMIN})
    public LearningObject setProper(@RequestBody LearningObjectMiniDto loDto) {
        return reviewManager.setEverythingReviewedRefreshLO(getLoggedInUser(), loDto.convert(), ReviewStatus.ACCEPTED, ReviewType.IMPROPER);
    }

    @GetMapping
    @RequestMapping("{learningObjectId}")
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public List<ImproperContent> getImproperById(@PathVariable("learningObjectId") Long learningObjectId) {
        return improperContentService.getImproperContent(learningObjectId, getLoggedInUser());
    }
}
