package ee.hm.dop.common.test;

import org.junit.After;
import org.junit.runner.RunWith;

import ee.hm.dop.utils.DbUtils;

@RunWith(GuiceTestRunner.class)
public abstract class DatabaseTestBase {

    @After
    public void closeEntityManager() {
        DbUtils.closeEntityManager();
    }
}
