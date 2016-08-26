package ee.hm.dop.service;

import static org.easymock.EasyMock.cmp;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ee.hm.dop.dao.MaterialDAO;
import ee.hm.dop.dao.RepositoryDAO;
import ee.hm.dop.model.LanguageString;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Picture;
import ee.hm.dop.model.Repository;
import ee.hm.dop.model.RepositoryURL;
import ee.hm.dop.oaipmh.MaterialIterator;
import ee.hm.dop.oaipmh.RepositoryManager;
import ee.hm.dop.oaipmh.SynchronizationAudit;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.LogicalOperator;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

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

    @Mock
    private PictureService pictureService;

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
        expect(material1.isDeleted()).andReturn(false).anyTimes();
        expect(material1.getPicture()).andReturn(null);
        material1.setRepository(repository);
        expect(materialDao.findByRepositoryAndRepositoryIdentifier(repository, repositoryIdentifier1)).andReturn(null);
        expect(materialService.createMaterial(material1, null, false)).andReturn(new Material());

        expect(materialIterator.hasNext()).andReturn(true);
        expect(materialIterator.next()).andThrow(new RuntimeException());

        expect(materialIterator.hasNext()).andReturn(true);
        expect(materialIterator.next()).andReturn(material2);
        String repositoryIdentifier2 = "123456Identifier2";
        expect(material2.getRepositoryIdentifier()).andReturn(repositoryIdentifier2);
        expect(material2.isDeleted()).andReturn(false).anyTimes();
        expect(material2.getPicture()).andReturn(null);
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
        expect(material.getPicture()).andReturn(null);
        material.setRepository(repository);

        Material originalMaterial = new Material();
        long originalMaterialId = 234l;
        originalMaterial.setId(originalMaterialId);
        expect(materialDao.findByRepositoryAndRepositoryIdentifier(repository, repositoryIdentifier)).andReturn(
                originalMaterial);
        expect(material.isDeleted()).andReturn(false).anyTimes();
        expect(materialService.update(material, null)).andReturn(material);

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
        RepositoryURL repositoryURL = new RepositoryURL();
        repositoryURL.setBaseURL("http://www.xray24.ru");
        repository.setRepositoryURLs(Collections.singletonList(repositoryURL));

        material.setRepository(EasyMock.anyObject(Repository.class));

        Material originalMaterial = new Material();
        long originalMaterialId = 234l;
        originalMaterial.setId(originalMaterialId);
        originalMaterial.setSource("http://www.xray24.ru");
        expect(materialDao.findByRepositoryAndRepositoryIdentifier(repository, repositoryIdentifier)).andReturn(
                originalMaterial);
        expect(material.isDeleted()).andReturn(true).anyTimes();

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
        expect(material.isDeleted()).andReturn(false).anyTimes();
        expect(material.getPicture()).andReturn(null);
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
        }, LogicalOperator.EQUAL));
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

    @Test
    public void getDomainName() {
        String domain = repositoryService.getDomainName("http://www.e-ope.ee/_download/euni_repository/file/863/MT-Loeng-03-1-Pajuste-Noudmine-ja-pakkumine.pdf");
        assertEquals("e-ope.ee", domain);

        domain = repositoryService.getDomainName("e-ope.ee/_download/euni_repository/file/863/MT-Loeng-03-1-Pajuste-Noudmine-ja-pakkumine.pdf");
        assertEquals("e-ope.ee", domain);

        domain = repositoryService.getDomainName("www.e-ope.ee/_download/euni_repository/file/863/MT-Loeng-03-1-Pajuste-Noudmine-ja-pakkumine.pdf");
        assertEquals("e-ope.ee", domain);

        domain = repositoryService.getDomainName("www.e-ope.ee/_download/euni_repository/file/863/MT-Loeng-03-1-Pajuste-Noudmine-ja-pakkumine.pdf");
        assertNotEquals("koolielu.ee", domain);
    }

    @Test
    public void isRepoMaterial() {
        Repository repository = createMock(Repository.class);
        RepositoryURL repositoryURL1 = createMock(RepositoryURL.class);
        RepositoryURL repositoryURL2 = createMock(RepositoryURL.class);
        Material existingMaterial = createMock(Material.class);
        List<RepositoryURL> urls = Arrays.asList(repositoryURL1, repositoryURL2);

        expect(repositoryURL1.getBaseURL()).andReturn("koolielu.ee").anyTimes();
        expect(repositoryURL2.getBaseURL()).andReturn("koolitaja.eenet.ee").anyTimes();
        expect(repository.getRepositoryURLs()).andReturn(urls).anyTimes();
        expect(existingMaterial.getSource()).andReturn("http://www.koolielu.ee/files/t_binsol_tk.rtf");
        expect(existingMaterial.getSource()).andReturn("koolielu.ee/files/t_binsol_tk.rtf");
        expect(existingMaterial.getSource()).andReturn("www.ditmas.ee/files/t_binsol_tk.rtf");

        replayAll(repository, repositoryURL1, repositoryURL2, existingMaterial);
        boolean isRepoMaterial = repositoryService.isRepoMaterial(repository, existingMaterial);
        assertTrue(isRepoMaterial);

        isRepoMaterial = repositoryService.isRepoMaterial(repository, existingMaterial);
        assertTrue(isRepoMaterial);

        isRepoMaterial = repositoryService.isRepoMaterial(repository, existingMaterial);
        assertFalse(isRepoMaterial);

        verifyAll(repository, repositoryURL1, repositoryURL2, existingMaterial);
    }

    @Test
    public void updateRepoMaterial() {
        Material existentMaterial = new Material();
        Material newMaterial = new Material();

        List<LanguageString> titles = new ArrayList<>();
        LanguageString title1 = new LanguageString();
        titles.add(title1);

        title1.setText("existingTitle");
        existentMaterial.setSource("http://www.oldLink.ee");
        existentMaterial.setEmbeddable(true);
        existentMaterial.setTitles(titles);

        newMaterial.setSource("http://www.newLink.ee");
        newMaterial.setEmbeddable(null);
        newMaterial.setTitles(new ArrayList<>());

        expect(materialService.update(existentMaterial, null)).andReturn(existentMaterial);

        replayAll();

        Material returnedMaterial = repositoryService.updateMaterial(newMaterial, existentMaterial, new SynchronizationAudit(), true);

        verifyAll();

        assertEquals("http://www.newLink.ee", returnedMaterial.getSource());
        assertTrue(returnedMaterial.getEmbeddable());
        assertEquals(titles, returnedMaterial.getTitles());
    }

    @Test
    public void updateNonRepoMaterial() {
        Material existentMaterial = new Material();
        Material newMaterial = new Material();

        List<LanguageString> titles = new ArrayList<>();
        LanguageString title1 = new LanguageString();
        title1.setText("existingTitle");
        titles.add(title1);

        existentMaterial.setSource("http://www.oldLink.ee");
        existentMaterial.setEmbeddable(null);
        existentMaterial.setTitles(titles);

        newMaterial.setSource("http://www.newLink.ee");
        newMaterial.setEmbeddable(true);
        newMaterial.setTitles(new ArrayList<>());

        expect(materialService.update(newMaterial, null)).andReturn(newMaterial);

        replayAll();

        Material returnedMaterial = repositoryService.updateMaterial(newMaterial, existentMaterial, new SynchronizationAudit(), false);

        verifyAll();

        assertEquals("http://www.oldLink.ee", returnedMaterial.getSource());
        assertTrue(returnedMaterial.getEmbeddable());
        assertEquals(titles, returnedMaterial.getTitles());
    }

    @Test
    public void updateRepoMaterialWithPicture() {
        Material existentMaterial = new Material();
        Material newMaterial = new Material();

        existentMaterial.setPicture(new Picture());
        newMaterial.setPicture(null);

        expect(materialService.update(existentMaterial, null)).andReturn(existentMaterial);

        replayAll();

        Material returnedMaterial = repositoryService.updateMaterial(newMaterial, existentMaterial, new SynchronizationAudit(), true);

        verifyAll();

        assertNotNull(returnedMaterial.getPicture());
    }

    @Test
    public void updateNonRepoMaterialWithPicture() {
        Material existentMaterial = new Material();
        Material newMaterial = new Material();
        Picture picture = new Picture();
        picture.setId(1);

        existentMaterial.setPicture(picture);
        newMaterial.setPicture(null);

        expect(materialService.update(newMaterial, null)).andReturn(newMaterial);

        replayAll();

        Material returnedMaterial = repositoryService.updateMaterial(newMaterial, existentMaterial, new SynchronizationAudit(), false);

        verifyAll();

        assertNotNull(returnedMaterial.getPicture());
    }

    @Test
    public void updateMaterialPicture() {
        Material existentMaterial = new Material();
        Material newMaterial = new Material();
        Picture picture1 = new Picture();
        picture1.setId(1);
        Picture picture2 = new Picture();
        picture2.setId(2);

        existentMaterial.setPicture(picture1);
        newMaterial.setPicture(picture2);

        expect(materialService.update(existentMaterial, null)).andReturn(existentMaterial);
        expect(pictureService.create(picture2)).andReturn(picture2);

        replayAll();

        Material returnedMaterial = repositoryService.updateMaterial(newMaterial, existentMaterial, new SynchronizationAudit(), true);

        verifyAll();

        assertEquals(picture2.getId(), returnedMaterial.getPicture().getId());
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
        replay(repositoryManager, materialIterator, materialService, repositoryDAO, materialDao, searchEngineService, pictureService);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

    private void verifyAll(Object... mocks) {
        verify(repositoryManager, materialIterator, materialService, repositoryDAO, materialDao, searchEngineService, pictureService);

        if (mocks != null) {
            for (Object object : mocks) {
                verify(object);
            }
        }
    }

}
