package ee.hm.dop.common.test;

import ee.hm.dop.utils.DbUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

@RunWith(GuiceTestRunner.class)
public abstract class DatabaseTestBase {

    @Before
    public void beginTransaction() {
        DbUtils.getTransaction().begin();
    }

    @After
    public void closeEntityManager() {
        DbUtils.closeTransaction();
        DbUtils.closeEntityManager();
    }
}
