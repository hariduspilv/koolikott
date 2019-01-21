package ee.hm.dop.rest.administration;

import ee.hm.dop.model.AdminLearningObject;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.LearningObjectMiniDto;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.model.enums.ReviewType;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.reviewmanagement.FirstReviewAdminService;
import ee.hm.dop.service.reviewmanagement.ReviewManager;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@RestController
@RequestMapping("admin/firstReview/")
public class FirstReviewAdminResource extends BaseResource {

    @Inject
    private FirstReviewAdminService firstReviewAdminService;
    @Inject
    private ReviewManager reviewManager;

    @GetMapping
    @RequestMapping("unReviewed")
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public List<AdminLearningObject> getUnReviewed() {
        return firstReviewAdminService.getUnReviewed(getLoggedInUser());
    }

    @GetMapping
    @RequestMapping("unReviewed/count")
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public Long getUnReviewedCount() {
        return firstReviewAdminService.getUnReviewedCount(getLoggedInUser());
    }

    @PostMapping
    @RequestMapping("setReviewed")
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public LearningObject setReviewed(@RequestBody LearningObjectMiniDto learningObject) {
        return reviewManager.setEverythingReviewedRefreshLO(getLoggedInUser(), learningObject.convert(), ReviewStatus.ACCEPTED, ReviewType.FIRST);
    }
}
