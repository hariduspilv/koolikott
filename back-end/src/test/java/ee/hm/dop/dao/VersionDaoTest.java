package ee.hm.dop.dao;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.Version;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class VersionDaoTest extends DatabaseTestBase {

    @Inject
    private VersionDao versionDao;

    @Test
    public void getAllVersions() {
        List<Version> versions = versionDao.getAllVersions();
        assertEquals(2, versions.size());
    }

    @Test
    public void getNewestVersion() {
        Version latestVersion = versionDao.getLatestVersion();
        assertEquals(2L, (long) latestVersion.getId());
        assertEquals("2.0", latestVersion.getVersion());
    }
}
