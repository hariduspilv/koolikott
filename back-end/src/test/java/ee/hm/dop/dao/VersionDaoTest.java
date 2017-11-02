package ee.hm.dop.dao;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.Version;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class VersionDaoTest extends DatabaseTestBase {

    @Inject
    private VersionDao versionDao;

    @Test
    public void getAllVersions() {
        List<Version> versions = versionDao.getAllVersions();
        assertTrue(CollectionUtils.isNotEmpty(versions));
    }

    @Test
    public void getNewestVersion() {
        Version latestVersion = versionDao.getLatestVersion();
        if (latestVersion.getId() == 2L){
            assertEquals(2L, (long) latestVersion.getId());
            assertEquals("2.0", latestVersion.getVersion());
        } else {
            //atom adds a version
            assertEquals(3L, (long) latestVersion.getId());
        }
    }
}
