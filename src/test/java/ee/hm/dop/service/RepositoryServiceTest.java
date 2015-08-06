package ee.hm.dop.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

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
    public void updateRepositoryErrorGettingMaterials() throws Exception {
        Repository repository = getRepository();

        expect(repositoryManager.getMaterialsFrom(repository)).andThrow(new Exception());

        replay(repositoryManager);

        repositoryService.updateRepository(repository);

        verify(repositoryManager);
    }

    @Test
    public void updateRepositoryhasNextFalse() throws Exception {
        Repository repository = getRepository();

        expect(repositoryManager.getMaterialsFrom(repository)).andReturn(materials);
        expect(materials.hasNext()).andReturn(false);

        replay(repositoryManager, materials);

        repositoryService.updateRepository(repository);

        verify(repositoryManager, materials);
    }

    @Test
    public void updateRepositoryNext() throws Exception {
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

        repositoryService.updateRepository(repository);

        verify(repositoryManager, materials, material);
    }

    @Test
    public void updateRepositoryHandleMaterial() throws Exception {
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

        repositoryService.updateRepository(repository);

        verify(repositoryManager, materials, material, materialService);
    }

    @Test
    public void updateRepository() throws Exception {
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
            if (o1.getLastSynchronization().getMillis() <= DateTime.now().getMillis()){
                return 0;
            }

            return  -1;
        }, LogicalOperator.EQUAL));

        replay(repositoryManager, materials, material, materialService, repositoryDAO);

        repositoryService.updateRepository(repository);
        verify(repositoryManager, materials, material, materialService, repositoryDAO);
    }

    private Repository getRepository() {
        Repository repository = new Repository();
        repository.setBaseURL("http://koolitaja.eenet.ee:57219/Waramu3Web/OAIHandler");
        repository.setSchema("waramu");
        repository.setId((long) 1);
        repository.setLastSynchronization(new DateTime());
        return repository;
    }


}
