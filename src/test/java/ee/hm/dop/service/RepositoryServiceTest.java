package ee.hm.dop.service;

import static org.easymock.EasyMock.cmp;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.List;

import org.easymock.EasyMockRunner;
import org.easymock.LogicalOperator;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import ee.hm.dop.dao.MaterialDAO;
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
    private MaterialIterator materialIterator;

    @Mock
    private MaterialService materialService;

    @Mock
    private RepositoryDAO repositoryDAO;

    @Mock
    private MaterialDAO materialDao;

    @Mock
    private SearchEngineService searchEngineService;

    @Test
    public void synchronizeErrorGettingMaterials() throws Exception {
        Repository repository = getRepository();

        expect(repositoryManager.getMaterialsFrom(repository)).andThrow(new Exception());

        replayAll();

        repositoryService.synchronize(repository);

        verifyAll();
    }

    @Test
    public void synchronizeNoMaterials() throws Exception {
        Repository repository = getRepository();

        expect(repositoryManager.getMaterialsFrom(repository)).andReturn(materialIterator);
        expect(materialIterator.hasNext()).andReturn(false);
        expectUpdateRepository(repository);

        searchEngineService.updateIndex();

        replayAll();

        repositoryService.synchronize(repository);

        verifyAll();
    }

    @Test
    public void synchronizeRecoverFromErrorGettingNextMaterial() throws Exception {
        Repository repository = getRepository();
        Material material1 = createMock(Material.class);
        Material material2 = createMock(Material.class);

        expect(repositoryManager.getMaterialsFrom(repository)).andReturn(materialIterator);

        expect(materialIterator.hasNext()).andReturn(true);
        expect(materialIterator.next()).andReturn(material1);
        String repositoryIdentifier1 = "123456Identifier";
        expect(material1.getRepositoryIdentifier()).andReturn(repositoryIdentifier1);
        expect(material1.isDeleted()).andReturn(false);
        material1.setRepository(repository);
        expect(materialDao.findByRepositoryAndRepositoryIdentifier(repository, repositoryIdentifier1)).andReturn(null);
        expect(materialService.createMaterial(material1, null, false)).andReturn(new Material());

        expect(materialIterator.hasNext()).andReturn(true);
        expect(materialIterator.next()).andThrow(new RuntimeException());

        expect(materialIterator.hasNext()).andReturn(true);
        expect(materialIterator.next()).andReturn(material2);
        String repositoryIdentifier2 = "123456Identifier2";
        expect(material2.getRepositoryIdentifier()).andReturn(repositoryIdentifier2);
        expect(material2.isDeleted()).andReturn(false);
        material2.setRepository(repository);
        expect(materialDao.findByRepositoryAndRepositoryIdentifier(repository, repositoryIdentifier2)).andReturn(null);
        expect(materialService.createMaterial(material2, null, false)).andReturn(new Material());

        expectUpdateRepository(repository);

        expect(materialIterator.hasNext()).andReturn(false);

        searchEngineService.updateIndex();

        replayAll(material1, material2);

        repositoryService.synchronize(repository);

        verifyAll(material1, material2);
    }

    @Test
    public void synchronizeUpdatingMaterial() throws Exception {
        Repository repository = getRepository();
        Material material = createMock(Material.class);

        expect(repositoryManager.getMaterialsFrom(repository)).andReturn(materialIterator);

        expect(materialIterator.hasNext()).andReturn(true);
        expect(materialIterator.next()).andReturn(null);

        expect(materialIterator.hasNext()).andReturn(true);
        expect(materialIterator.next()).andReturn(material);
        String repositoryIdentifier = "123456Identifier";
        expect(material.getRepositoryIdentifier()).andReturn(repositoryIdentifier);
        material.setRepository(repository);

        Material originalMaterial = new Material();
        long originalMaterialId = 234l;
        originalMaterial.setId(originalMaterialId);
        expect(materialDao.findByRepositoryAndRepositoryIdentifier(repository, repositoryIdentifier))
                .andReturn(originalMaterial);
        expect(material.isDeleted()).andReturn(false);
        material.setId(originalMaterialId);
        expect(materialService.update(material, true)).andReturn(material);

        expectUpdateRepository(repository);

        expect(materialIterator.hasNext()).andReturn(false);

        searchEngineService.updateIndex();

        replayAll(material);

        repositoryService.synchronize(repository);

        verifyAll(material);
    }

    @Test
    public void synchronizeUpdatingDeletedMaterial() throws Exception {
        Repository repository = getRepository();
        Material material = createMock(Material.class);

        expect(repositoryManager.getMaterialsFrom(repository)).andReturn(materialIterator);

        expect(materialIterator.hasNext()).andReturn(true);
        expect(materialIterator.next()).andReturn(null);

        expect(materialIterator.hasNext()).andReturn(true);
        expect(materialIterator.next()).andReturn(material);
        String repositoryIdentifier = "123456Identifier";
        expect(material.getRepositoryIdentifier()).andReturn(repositoryIdentifier);
        material.setRepository(repository);

        Material originalMaterial = new Material();
        long originalMaterialId = 234l;
        originalMaterial.setId(originalMaterialId);
        expect(materialDao.findByRepositoryAndRepositoryIdentifier(repository, repositoryIdentifier))
                .andReturn(originalMaterial);
        expect(material.isDeleted()).andReturn(true);

        materialService.delete(originalMaterial);

        expectUpdateRepository(repository);

        expect(materialIterator.hasNext()).andReturn(false);

        searchEngineService.updateIndex();

        replayAll(material);

        repositoryService.synchronize(repository);

        verifyAll(material);
    }

    @Test
    public void synchronize() throws Exception {
        Repository repository = getRepository();
        Material material = createMock(Material.class);

        expect(repositoryManager.getMaterialsFrom(repository)).andReturn(materialIterator);

        expect(materialIterator.hasNext()).andReturn(true);
        expect(materialIterator.next()).andReturn(material);
        expect(materialService.createMaterial(material, null, false)).andReturn(new Material());
        expect(materialIterator.hasNext()).andReturn(false);

        String repositoryIdentifier = "123456Identifier";
        expect(material.getRepositoryIdentifier()).andReturn(repositoryIdentifier);
        expect(material.isDeleted()).andReturn(false);
        material.setRepository(repository);
        expect(materialDao.findByRepositoryAndRepositoryIdentifier(repository, repositoryIdentifier)).andReturn(null);

        searchEngineService.updateIndex();

        expectUpdateRepository(repository);

        replayAll(material);

        repositoryService.synchronize(repository);

        verifyAll(material);
    }

    private void expectUpdateRepository(Repository repository) {
        final DateTime before = DateTime.now();
        repositoryDAO.updateRepository(cmp(repository, (o1, o2) -> {
            if (o1 == o2) {
                return 0;
            }

            DateTime lastSynchronization = o1.getLastSynchronization();
            if (before.isBefore(lastSynchronization) || before.isEqual(lastSynchronization)) {
                return 0;
            }

            if (lastSynchronization.isEqualNow() || lastSynchronization.isBeforeNow()) {
                return 0;
            }

            return -1;
        } , LogicalOperator.EQUAL));
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
        repository.setLastSynchronization(new DateTime().minusDays(1));
        return repository;
    }

    private void replayAll(Object... mocks) {
        replay(repositoryManager, materialIterator, materialService, repositoryDAO, materialDao, searchEngineService);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

    private void verifyAll(Object... mocks) {
        verify(repositoryManager, materialIterator, materialService, repositoryDAO, materialDao, searchEngineService);

        if (mocks != null) {
            for (Object object : mocks) {
                verify(object);
            }
        }
    }

}
