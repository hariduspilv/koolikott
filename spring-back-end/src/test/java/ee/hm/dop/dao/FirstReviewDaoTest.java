package ee.hm.dop.dao;

import javax.inject.Inject;
import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.User;
import org.junit.Test;

public class FirstReviewDaoTest extends DatabaseTestBase{

    @Inject
    private FirstReviewDao firstReviewDao;
    @Inject
    private UserDao userDao;

    @Test
    public void query_does_not_fail() throws Exception {
        firstReviewDao.findAllUnreviewed();
        User moderator = userDao.findById(USER_MODERATOR.id);
        firstReviewDao.findAllUnreviewed(moderator);
    }
}