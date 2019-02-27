package ee.hm.dop.dao;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.dao.firstreview.FirstReviewDao;
import org.junit.Ignore;
import org.junit.Test;

import javax.inject.Inject;

public class FirstReviewDaoTest extends DatabaseTestBase{

    @Inject
    private FirstReviewDao firstReviewDao;
    @Inject
    private UserDao userDao;

    @Ignore
    @Test
    public void query_does_not_fail() throws Exception {
//        firstReviewDao.findAllUnreviewed();
//        User moderator = userDao.findById(USER_MODERATOR.id);
//        firstReviewDao.findAllUnreviewed(moderator);
    }
}