package ee.hm.dop.service.reviewmanagement;

import ee.hm.dop.dao.FirstReviewDao;
import ee.hm.dop.model.AdminLearningObject;
import ee.hm.dop.model.FirstReview;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsFilterDto;
import ee.hm.dop.utils.UserUtil;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

import java.util.Arrays;
import java.util.List;

import static java.time.LocalDateTime.now;

@Service
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
                firstReview.setReviewedAt(LocalDateTime.now());
                firstReview.setReviewedBy(loggedInUser);
                firstReview.setReviewed(true);
                firstReview.setStatus(reviewStatus);
                firstReviewDao.createOrUpdate(firstReview);
                learningObject.setUnReviewed(learningObject.getUnReviewed()-1);
            }
        }
    }
}
