package ee.hm.dop.rest.administration;

import ee.hm.dop.model.AdminLearningObject;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.LearningObjectMiniDto;
import ee.hm.dop.model.ReviewableChange;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.model.enums.ReviewType;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.content.LearningObjectService;
import ee.hm.dop.service.reviewmanagement.ReviewManager;
import ee.hm.dop.service.reviewmanagement.ReviewableChangeAdminService;
import ee.hm.dop.service.reviewmanagement.ReviewableChangeService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

@RestController
@RequestMapping("admin/changed")
public class ReviewableChangeAdminResource extends BaseResource {

    @Inject
    private ReviewableChangeService reviewableChangeService;
    @Inject
    private ReviewableChangeAdminService reviewableChangeAdminService;
    @Inject
    private ReviewManager reviewManager;
    @Inject
    private LearningObjectService learningObjectService;

    @GetMapping
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public List<AdminLearningObject> getChanged() {
        return reviewableChangeAdminService.getUnReviewed(getLoggedInUser());
    }

    @GetMapping("count")
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public Long getCount() {
        return reviewableChangeAdminService.getUnReviewedCount(getLoggedInUser());
    }

    @GetMapping("{id}")
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public List<ReviewableChange> getChanges(@PathVariable("id") Long learningObjectId) {
        return reviewableChangeService.getAllByLearningObject(learningObjectId);
    }

    @PostMapping("{id}/acceptAll")
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public LearningObject acceptAllChanges(@PathVariable("id") Long learningObjectId) {
        if (learningObjectId == null) {
            throw badRequest("learningObject query param is required.");
        }
        User loggedInUser = getLoggedInUser();
        LearningObject learningObject = learningObjectService.get(learningObjectId, loggedInUser);
        if (learningObject == null) {
            throw notFound();
        }
        reviewManager.setEverythingReviewedRefreshLO(getLoggedInUser(), learningObject, ReviewStatus.ACCEPTED, ReviewType.CHANGE);
        return learningObjectService.get(learningObjectId, loggedInUser);
    }

    @PostMapping("acceptAll")
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public LearningObject acceptAllChanges2(@RequestBody LearningObjectMiniDto learningObject) {
        return reviewManager.setEverythingReviewedRefreshLO(getLoggedInUser(), learningObject.convert(), ReviewStatus.ACCEPTED, ReviewType.CHANGE);
    }

    @PostMapping("{id}/revertAll")
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public LearningObject revertAllChanges(@PathVariable("id") Long learningObjectId) {
        return reviewableChangeAdminService.revertAllChanges(learningObjectId, getLoggedInUser());
    }

    @PostMapping("revertAll")
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public LearningObject revertAllChanges2(@RequestBody LearningObjectMiniDto learningObject) {
        return reviewableChangeAdminService.revertAllChanges(learningObject.getId(), getLoggedInUser());
    }

    @PostMapping("{id}/acceptOne/{changeId}")
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public LearningObject acceptAllChanges(@PathVariable("id") Long learningObjectId, @PathVariable("changeId") Long changeId) {
        return reviewableChangeAdminService.acceptOneChange(learningObjectId, changeId, getLoggedInUser());
    }

    @PostMapping("{id}/revertOne/{changeId}")
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public LearningObject revertAllChanges(@PathVariable("id") Long learningObjectId, @PathVariable("changeId") Long changeId) {
        return reviewableChangeAdminService.revertOneChange(learningObjectId, changeId, getLoggedInUser());
    }

    @PostMapping("acceptOne")
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public LearningObject acceptAllChanges2(@RequestBody ReviewableChange change) {
        return reviewableChangeAdminService.acceptOneChange(change.getLearningObject().getId(), change.getId(), getLoggedInUser());
    }

    @PostMapping("revertOne")
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public LearningObject revertAllChanges2(@RequestBody ReviewableChange change) {
        return reviewableChangeAdminService.revertOneChange(change.getLearningObject().getId(), change.getId(), getLoggedInUser());
    }
}
