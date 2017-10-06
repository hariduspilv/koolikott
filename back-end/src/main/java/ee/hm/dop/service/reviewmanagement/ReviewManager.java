package ee.hm.dop.service.reviewmanagement;

import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.service.content.LearningObjectService;
import ee.hm.dop.utils.UserUtil;

import javax.inject.Inject;

public class ReviewManager {

    @Inject
    private FirstReviewAdminService firstReviewAdminService;
    @Inject
    private ImproperContentAdminService improperContentAdminService;
    @Inject
    private BrokenContentService brokenContentService;
    @Inject
    private LearningObjectService learningObjectService;

    public void setEverythingReviewedRefreshLO(User user, LearningObject learningObject, ReviewStatus reviewStatus) {
        UserUtil.mustBeModeratorOrAdmin(user);
        LearningObject originalLearningObject = learningObjectService.validateAndFind(learningObject);
        setEverythingReviewed(user, originalLearningObject, reviewStatus);
    }

    public void setEverythingReviewed(User user, LearningObject originalLearningObject, ReviewStatus reviewStatus) {
        firstReviewAdminService.setReviewed(originalLearningObject, user, reviewStatus);
        improperContentAdminService.setReviewed(originalLearningObject, user, reviewStatus);
        if (originalLearningObject instanceof Material) {
            brokenContentService.setMaterialNotBroken((Material) originalLearningObject);
        }
    }
}
