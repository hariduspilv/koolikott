package ee.hm.dop.service.reviewmanagement;

import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.model.enums.ReviewType;
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
    @Inject
    private ReviewableChangeAdminService reviewableChangeAdminService;

    public void setEverythingReviewedRefreshLO(User user, LearningObject learningObject, ReviewStatus reviewStatus, ReviewType type) {
        UserUtil.mustBeModeratorOrAdmin(user);
        LearningObject originalLearningObject = learningObjectService.validateAndFind(learningObject);
        setEverythingReviewed(user, originalLearningObject, reviewStatus, type);
    }

    public void setEverythingReviewed(User user, LearningObject originalLearningObject, ReviewStatus reviewStatus, ReviewType type) {
        if (type.canReviewFirstReview()) {
            firstReviewAdminService.setReviewed(originalLearningObject, user, reviewStatus);
        }
        if (type.canReviewImproperContent()) {
            improperContentAdminService.setReviewed(originalLearningObject, user, reviewStatus);
        }
        if (type.canReviewBrokenContent() && originalLearningObject instanceof Material) {
            brokenContentService.setMaterialNotBroken((Material) originalLearningObject);
        }
        if (type.canReviewChange()){
            reviewableChangeAdminService.setReviewed(originalLearningObject, user, reviewStatus);
        }
    }
}
