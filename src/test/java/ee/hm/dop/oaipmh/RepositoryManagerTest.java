package ee.hm.dop.oaipmh;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import ee.hm.dop.common.test.GuiceTestRunner;
import ee.hm.dop.model.Repository;

/**
 * Created by mart.laus on 16.07.2015.
 */
@RunWith(GuiceTestRunner.class)
public class RepositoryManagerTest {

    @TestSubject
    private RepositoryManager repositoryManager = new RepositoryManager();

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
        String errorMessage = null;

        try {
            repositoryManager.getMaterialsFrom(repository);
            fail("Exception expected.");
        } catch (Exception e) {
            assertEquals(errorMessage, e.getMessage());
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
