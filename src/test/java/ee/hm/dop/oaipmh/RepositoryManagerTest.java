package ee.hm.dop.oaipmh;

import static junit.framework.TestCase.assertSame;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.junit.Test;

import ee.hm.dop.model.Repository;

/**
 * Created by mart.laus on 16.07.2015.
 */
public class RepositoryManagerTest {

    @Test
    public void getMaterials() throws Exception {
        RepositoryManager repositoryManager = createMock(RepositoryManager.class);
        Repository repository = createMock(Repository.class);
        MaterialIterator materialIterator = createMock(MaterialIterator.class);

        expect(repositoryManager.getMaterialsFrom(repository)).andReturn(materialIterator);

        replay(repositoryManager, repository, materialIterator);

        MaterialIterator returnedIterator = repositoryManager.getMaterialsFrom(repository);

        verify(repositoryManager, repository, materialIterator);
        assertSame(returnedIterator, materialIterator);
    }
}
