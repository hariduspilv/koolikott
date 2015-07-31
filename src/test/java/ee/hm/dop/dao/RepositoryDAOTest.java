package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.junit.Test;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.Repository;

/**
 * Created by mart.laus on 22.07.2015.
 */
public class RepositoryDAOTest extends DatabaseTestBase {

    @Inject
    private RepositoryDAO repositoryDAO;

    @Test
    public void findAll() {
        List<Repository> repositories = repositoryDAO.findAll();

        assertEquals(1, repositories.size());
        assertEquals("http://koolitaja.eenet.ee:57219/Waramu3Web/OAIHandler", repositories.get(0).getBaseURL());
        assertNull(repositories.get(0).getLastSynchronization());
    }

    @Test
    public void updateRepositoryData() {
        Repository repository = repositoryDAO.findAll().get(0);
        assertNull(repository.getLastSynchronization());

        repository.setLastSynchronization(new DateTime());
        repositoryDAO.updateRepository(repository);
        assertNotNull(repository.getLastSynchronization());

        repository.setLastSynchronization(null);
        repositoryDAO.updateRepository(repository);
    }
}
