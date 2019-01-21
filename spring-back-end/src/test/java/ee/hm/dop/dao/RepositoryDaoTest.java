package ee.hm.dop.dao;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.Repository;
import java.time.LocalDateTime;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class RepositoryDaoTest extends DatabaseTestBase {

    @Inject
    private RepositoryDao repositoryDao;

    @Test
    public void findAll() {
        List<Repository> repositories = repositoryDao.findAll();

        assertEquals(2, repositories.size());
        assertEquals("http://repo1.ee", repositories.get(0).getBaseURL());
        assertNull(repositories.get(0).getLastSynchronization());
        assertEquals(2, repositories.get(0).getRepositoryURLs().size());
    }

    @Test
    public void updateRepositoryData() {
        Repository repository = repositoryDao.findAll().get(0);
        assertNull(repository.getLastSynchronization());

        repository.setLastSynchronization(LocalDateTime.now());
        repositoryDao.updateRepository(repository);

        Repository repository2 = repositoryDao.findAll().get(0);
        assertNotNull(repository2.getLastSynchronization());

        repository.setLastSynchronization(null);
        repositoryDao.updateRepository(repository);
    }
}
