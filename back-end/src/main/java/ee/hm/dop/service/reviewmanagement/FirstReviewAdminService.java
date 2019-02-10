package ee.hm.dop.service.reviewmanagement;

import ee.hm.dop.dao.FirstReviewDao;
import ee.hm.dop.model.AdminLearningObject;
import ee.hm.dop.model.FirstReview;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsFilterDto;
import ee.hm.dop.utils.UserUtil;
import org.joda.time.DateTime;

import javax.inject.Inject;

import java.util.Arrays;
import java.util.List;

import static org.joda.time.DateTime.now;

public class FirstReviewAdminService {

    @Inject
    private FirstReviewDao firstReviewDao;

    public List<AdminLearningObject> getUnReviewed(User user) {
        UserUtil.mustBeModeratorOrAdmin(user);
        if (UserUtil.isAdmin(user)) {
            return firstReviewDao.findAllUnreviewed();
        } else {
            return firstReviewDao.findAllUnreviewed(user);
        }
    }

    public List<AdminLearningObject> getUnReviewed(User user,String sortingOrder) {
        UserUtil.mustBeModeratorOrAdmin(user);
        if (UserUtil.isAdmin(user)) {
            return firstReviewDao.findAllUnreviewed(sortingOrder);
        } else {
            return firstReviewDao.findAllUnreviewed(user,sortingOrder);
        }
    }

    public List<AdminLearningObject> getUnReviewed(User user, String direction, String page,String itemToSortedBy) {
        UserUtil.mustBeModeratorOrAdmin(user);
        if (UserUtil.isAdmin(user)) {
            return firstReviewDao.findAllUnreviewed(direction, page, itemToSortedBy);
        } else {
            return firstReviewDao.findAllUnreviewed(user, direction, page,itemToSortedBy);
        }
    }

    public long getUnReviewedCount(User user) {
        UserUtil.mustBeModeratorOrAdmin(user);
        if (UserUtil.isAdmin(user)) {
            return firstReviewDao.findCountOfUnreviewed();
        } else {
            return firstReviewDao.findCountOfUnreviewed(user);
        }
    }

    public FirstReview save(LearningObject learningObject) {
        learningObject.setUnReviewed(learningObject.getUnReviewed() + 1);
        FirstReview firstReview = new FirstReview();
        firstReview.setLearningObject(learningObject);
        firstReview.setReviewed(false);
        firstReview.setCreatedAt(now());

        return firstReviewDao.createOrUpdate(firstReview);
    }

    public void setReviewed(LearningObject learningObject, User loggedInUser, ReviewStatus reviewStatus) {
        for (FirstReview firstReview : learningObject.getFirstReviews()) {
            if (!firstReview.isReviewed()) {
                firstReview.setReviewedAt(DateTime.now());
                firstReview.setReviewedBy(loggedInUser);
                firstReview.setReviewed(true);
                firstReview.setStatus(reviewStatus);
                firstReviewDao.createOrUpdate(firstReview);
                learningObject.setUnReviewed(learningObject.getUnReviewed()-1);
            }
        }
    }
}
