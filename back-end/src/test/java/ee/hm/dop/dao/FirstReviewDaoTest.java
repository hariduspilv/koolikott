package ee.hm.dop.dao;

import com.google.inject.Inject;
import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.dao.firstreview.FirstReviewDao;
import ee.hm.dop.dao.firstreview.FirstReviewOldDao;
import ee.hm.dop.model.User;
import org.junit.Test;

public class FirstReviewDaoTest extends DatabaseTestBase{

    @Inject
    private FirstReviewOldDao firstReviewOldDao;
    @Inject
    private UserDao userDao;

    @Test
    public void query_does_not_fail() throws Exception {
        firstReviewOldDao.findAllUnreviewed();
        User moderator = userDao.findById(USER_MODERATOR.id);
        firstReviewOldDao.findAllUnreviewed(moderator);
    }
}