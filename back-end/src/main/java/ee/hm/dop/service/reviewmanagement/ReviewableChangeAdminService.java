package ee.hm.dop.service.reviewmanagement;

import ee.hm.dop.dao.LearningObjectDao;
import ee.hm.dop.dao.ReviewableChangeDao;
import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.model.enums.ReviewType;
import ee.hm.dop.service.content.LearningObjectService;
import ee.hm.dop.utils.UserUtil;
import ee.hm.dop.utils.ValidatorUtil;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.List;

public class ReviewableChangeAdminService {

    @Inject
    private ReviewableChangeDao reviewableChangeDao;
    @Inject
    private ReviewManager reviewManager;
    @Inject
    private LearningObjectService learningObjectService;
    @Inject
    private LearningObjectDao learningObjectDao;

    public List<AdminLearningObject> getUnReviewed(User user) {
        UserUtil.mustBeModeratorOrAdmin(user);
        if (UserUtil.isAdmin(user)) {
            return reviewableChangeDao.findAllUnreviewed();
        } else {
            return reviewableChangeDao.findAllUnreviewed(user);
        }
    }

    public long getUnReviewedCount(User user) {
        UserUtil.mustBeModeratorOrAdmin(user);
        if (UserUtil.isAdmin(user)) {
            return reviewableChangeDao.findCountOfUnreviewed();
        } else {
            return reviewableChangeDao.findCountOfUnreviewed(user);
        }
    }

    public void setReviewed(LearningObject learningObject, User user, ReviewStatus reviewStatus) {
        for (ReviewableChange reviewableChange : learningObject.getReviewableChanges()) {
            if (!reviewableChange.isReviewed()) {
                setReviewed(reviewableChange, user, reviewStatus);
                learningObject.setChanged(learningObject.getChanged() - 1);
            }
        }
        learningObject.setReviewableChanges(learningObject.getReviewableChanges());
    }

    public void setReviewed(ReviewableChange reviewableChange, User user, ReviewStatus reviewStatus) {
        reviewableChange.setReviewed(true);
        reviewableChange.setReviewedBy(user);
        reviewableChange.setReviewedAt(DateTime.now());
        reviewableChange.setStatus(reviewStatus);
        reviewableChangeDao.persist(reviewableChange);
    }

    public LearningObject revertAllChanges(Long learningObjectId, User user) {
        UserUtil.mustBeModeratorOrAdmin(user);
        LearningObject learningObject = learningObjectService.get(learningObjectId, user);
        ValidatorUtil.mustHaveEntity(learningObject);

        for (ReviewableChange change : learningObject.getReviewableChanges()) {
            revertOneChange(learningObject, change);
        }

        reviewManager.setEverythingReviewed(user, learningObject, ReviewStatus.REJECTED, ReviewType.CHANGE);
        return learningObjectDao.createOrUpdate(learningObject);
    }

    public LearningObject revertOneChange(Long learningObjectId, Long changeId, User user) {
        UserUtil.mustBeModeratorOrAdmin(user);
        LearningObject learningObject = learningObjectService.get(learningObjectId, user);
        ValidatorUtil.mustHaveEntity(learningObject);

        ReviewableChange change = reviewableChangeDao.findById(changeId);
        revertOneChange(learningObject, change);
        setReviewed(change, user, ReviewStatus.REJECTED);
        learningObject.setChanged(learningObject.getChanged()-1);
        return learningObjectDao.createOrUpdate(learningObject);
    }

    public LearningObject acceptOneChange(Long learningObjectId, Long changeId, User user) {
        UserUtil.mustBeModeratorOrAdmin(user);
        LearningObject learningObject = learningObjectService.get(learningObjectId, user);
        ValidatorUtil.mustHaveEntity(learningObject);

        ReviewableChange change = reviewableChangeDao.findById(changeId);
        setReviewed(change, user, ReviewStatus.ACCEPTED);
        learningObject.setChanged(learningObject.getChanged()-1);
        return learningObjectDao.createOrUpdate(learningObject);
    }

    private void revertOneChange(LearningObject learningObject, ReviewableChange change) {
        if (!change.isReviewed()) {
            if (change.getTaxon() != null) {
                learningObject.getTaxons().removeIf(t -> t.getId().equals(change.getTaxon().getId()));
            } else if (change.getResourceType() != null) {
                if (learningObject instanceof Material) {
                    ((Material) learningObject).getResourceTypes().removeIf(rt -> rt.getId().equals(change.getResourceType().getId()));
                }
            } else if (change.getTargetGroup() != null) {
                learningObject.getTargetGroups().removeIf(tg -> tg.getId().equals(change.getTargetGroup().getId()));
            } else if (change.getMaterialSource() != null) {
                if (learningObject instanceof Material) {
                    ((Material) learningObject).setSource(change.getMaterialSource());
                }
            }
        }
    }
}
