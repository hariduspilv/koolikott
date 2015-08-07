package ee.hm.dop.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.List;

import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.LogicalOperator;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import ee.hm.dop.dao.RepositoryDAO;
import ee.hm.dop.model.Material;
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

    @Mock
    private MaterialService materialService;

    @Mock
    private RepositoryDAO repositoryDAO;

    @Test
    public void synchronizeErrorGettingMaterials() throws Exception {
        Repository repository = getRepository();

        expect(repositoryManager.getMaterialsFrom(repository)).andThrow(new Exception());

        replay(repositoryManager);

        repositoryService.synchronize(repository);

        verify(repositoryManager);
    }

    @Test
    public void synchronizeHasNextFalse() throws Exception {
        Repository repository = getRepository();

        expect(repositoryManager.getMaterialsFrom(repository)).andReturn(materials);
        expect(materials.hasNext()).andReturn(false);

        replay(repositoryManager, materials);

        repositoryService.synchronize(repository);

        verify(repositoryManager, materials);
    }

    @Test
    public void synchronizeNext() throws Exception {
        Repository repository = getRepository();
        Material material = createMock(Material.class);

        expect(repositoryManager.getMaterialsFrom(repository)).andReturn(materials);

        expect(materials.hasNext()).andReturn(true);
        expect(materials.next()).andReturn(material);

        expect(materials.hasNext()).andReturn(true);
        expect(materials.next()).andThrow(new RuntimeException());

        expect(materials.hasNext()).andReturn(true);
        expect(materials.next()).andReturn(material);

        expect(materials.hasNext()).andReturn(false);

        replay(repositoryManager, materials, material);

        repositoryService.synchronize(repository);

        verify(repositoryManager, materials, material);
    }

    @Test
    public void synchronizeHandleMaterial() throws Exception {
        Repository repository = getRepository();
        Material material = createMock(Material.class);

        expect(repositoryManager.getMaterialsFrom(repository)).andReturn(materials);

        expect(materials.hasNext()).andReturn(true);
        expect(materials.next()).andReturn(null);

        expect(materials.hasNext()).andReturn(true);
        expect(materials.next()).andReturn(material);
        materialService.createMaterial(material);

        expect(materials.hasNext()).andReturn(false);

        replay(repositoryManager, materials, material, materialService);

        repositoryService.synchronize(repository);

        verify(repositoryManager, materials, material, materialService);
    }

    @Test
    public void synchronize() throws Exception {
        Repository repository = getRepository();
        Material material = createMock(Material.class);

        expect(repositoryManager.getMaterialsFrom(repository)).andReturn(materials);

        expect(materials.hasNext()).andReturn(true);
        expect(materials.next()).andReturn(material);
        materialService.createMaterial(material);
        expect(materials.hasNext()).andReturn(false);

        final DateTime before = DateTime.now();
        repositoryDAO.updateRepository(EasyMock.cmp(repository, (o1, o2) -> {
            if (o1 != o2) {
                return -1;
            }
            if (before.getMillis() >= o1.getLastSynchronization().getMillis()) {
                return -1;
            }
            if (o1.getLastSynchronization().getMillis() <= DateTime.now().getMillis()) {
                return 0;
            }

            return -1;
        }, LogicalOperator.EQUAL));

        replay(repositoryManager, materials, material, materialService, repositoryDAO);

        repositoryService.synchronize(repository);
        verify(repositoryManager, materials, material, materialService, repositoryDAO);
    }

    @Test
    public void getAllRepositorys() {
        @SuppressWarnings("unchecked")
        List<Repository> repositories = createMock(List.class);
        expect(repositoryDAO.findAll()).andReturn(repositories);

        replayAll(repositories);

        List<Repository> allRepositorys = repositoryService.getAllRepositorys();

        verifyAll(repositories);

        assertSame(repositories, allRepositorys);
    }

    @Test
    public void getAllRepositorysWhenNoRepositories() {
        expect(repositoryDAO.findAll()).andReturn(null);

        replayAll();

        List<Repository> allRepositorys = repositoryService.getAllRepositorys();

        verifyAll();

        assertNotNull(allRepositorys);
        assertEquals(0, allRepositorys.size());
    }

    private Repository getRepository() {
        Repository repository = new Repository();
        repository.setBaseURL("http://koolitaja.eenet.ee:57219/Waramu3Web/OAIHandler");
        repository.setSchema("waramu");
        repository.setId((long) 1);
        repository.setLastSynchronization(new DateTime());
        return repository;
    }

    private void replayAll(Object... mocks) {
        replay(repositoryManager, materials, materialService, repositoryDAO);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

    private void verifyAll(Object... mocks) {
        verify(repositoryManager, materials, materialService, repositoryDAO);

        if (mocks != null) {
            for (Object object : mocks) {
                verify(object);
            }
        }
    }

}
