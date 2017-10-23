package ee.hm.dop.service.reviewmanagement;

import ee.hm.dop.dao.ReviewableChangeDao;
import ee.hm.dop.model.ImproperContent;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.ReviewableChange;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.utils.UserUtil;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.List;

public class ReviewableChangeAdminService {

    @Inject
    private ReviewableChangeDao reviewableChangeDao;

    public List<ReviewableChange> getUnReviewed(User user) {
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
                setReviewed(user, reviewStatus, reviewableChange);
            }
        }
    }

    private void setReviewed(User user, ReviewStatus reviewStatus, ReviewableChange reviewableChange) {
        reviewableChange.setReviewed(true);
        reviewableChange.setReviewedBy(user);
        reviewableChange.setReviewedAt(DateTime.now());
        reviewableChange.setStatus(reviewStatus);
        reviewableChangeDao.createOrUpdate(reviewableChange);
    }
}
