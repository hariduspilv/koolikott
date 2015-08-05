package ee.hm.dop.service;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import ee.hm.dop.model.Repository;
import ee.hm.dop.oaipmh.MaterialIterator;
import ee.hm.dop.oaipmh.RepositoryManager;

@RunWith(EasyMockRunner.class)
public class RepositoryServiceTest {

    @TestSubject
    private RepositoryService repositoryService = new RepositoryService();

    @Mock
    private RepositoryManager repositoryManager;

    @Mock
    private MaterialIterator materials;

    @Test
    public void updateRepositorySyncroNotNull() {
        Repository repository = getRepository();
        repository.setLastSynchronization(new DateTime());

        repositoryService.updateRepository(repository);
    }

    @Test
    public void updateRepositoryErrorGettingMaterials() throws Exception {
        Repository repository = getRepository();

        expect(repositoryManager.getMaterialsFrom(repository)).andThrow(new Exception());

        replay(repositoryManager);

        repositoryService.updateRepository(repository);

        verify(repositoryManager);
    }

    @Test(expected = NullPointerException.class)
    public void updateRepositoryhasNextFalse() throws Exception {
        Repository repository = getRepository();

        expect(repositoryManager.getMaterialsFrom(repository)).andReturn(materials);
        expect(materials.hasNext()).andReturn(false);

        replay(repositoryManager, materials);

        repositoryService.updateRepository(repository);

        verify(repositoryManager, materials);
    }

    private Repository getRepository() {
        Repository repository = new Repository();
        repository.setBaseURL("http://koolitaja.eenet.ee:57219/Waramu3Web/OAIHandler");
        repository.setSchema("waramu");
        repository.setId((long) 1);
        return repository;
    }
}
