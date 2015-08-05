package ee.hm.dop.oaipmh;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import javax.inject.Inject;

import org.junit.Test;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.Repository;

public class RepositoryManagerTest extends DatabaseTestBase{

    @Inject
    private RepositoryManager repositoryManager;

    @Test
    public void getMaterialsFromWaramu() throws Exception {
        Repository repository = new Repository();
        repository.setSchema("waramu");
        repository.setBaseURL("http://koolitaja.eenet.ee:57219/Waramu3Web/OAIHandler");

        MaterialIterator returnedIterator = repositoryManager.getMaterialsFrom(repository);

        assertNotNull(returnedIterator);
    }

    @Test
    public void getMaterialsWrongSchema() throws Exception {
        Repository repository = new Repository();
        repository.setSchema("randomSchema");
        repository.setBaseURL("http://koolitaja.eenet.ee:57219/Waramu3Web/OAIHandler");
        String errorMessage = "No parser for schema randomSchema or wrong repository URL";

        try {
            repositoryManager.getMaterialsFrom(repository);
            fail("Exception expected.");
        } catch (Exception e) {
            assertEquals(errorMessage, e.getMessage());
        }
    }

    @Test
    public void getMaterialsNullSchema() throws Exception {
        Repository repository = new Repository();
        repository.setSchema(null);
        repository.setBaseURL("http://koolitaja.eenet.ee:57219/Waramu3Web/OAIHandler");

        try {
            repositoryManager.getMaterialsFrom(repository);
            fail("Exception expected.");
        } catch (Exception e) {
            assertEquals(null, e.getMessage());
        }
    }

    @Test
    public void getMaterialsNullURL() throws Exception {
        Repository repository = new Repository();
        repository.setSchema("randomSchema");
        repository.setBaseURL(null);
        String errorMessage = "No parser for schema randomSchema or wrong repository URL";

        try {
            repositoryManager.getMaterialsFrom(repository);
            fail("Exception expected.");
        } catch (Exception e) {
            assertEquals(errorMessage, e.getMessage());
        }
    }
}
