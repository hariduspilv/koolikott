package ee.hm.dop.service.reviewmanagement;

import ee.hm.dop.dao.ReviewableChangeDao;
import ee.hm.dop.model.ReviewableChange;
import ee.hm.dop.model.User;
import ee.hm.dop.utils.UserUtil;

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
}
