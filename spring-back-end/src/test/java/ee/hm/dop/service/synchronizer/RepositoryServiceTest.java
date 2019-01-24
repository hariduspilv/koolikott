package ee.hm.dop.service.synchronizer;

import ee.hm.dop.dao.RepositoryDao;
import ee.hm.dop.model.LanguageString;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.OriginalPicture;
import ee.hm.dop.model.Picture;
import ee.hm.dop.model.Repository;
import ee.hm.dop.model.RepositoryURL;
import ee.hm.dop.service.content.MaterialService;
import ee.hm.dop.service.content.enums.SearchIndexStrategy;
import ee.hm.dop.service.files.PictureSaver;
import ee.hm.dop.service.files.PictureService;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.service.synchronizer.oaipmh.MaterialIterator;
import ee.hm.dop.service.synchronizer.oaipmh.RepositoryManager;
import ee.hm.dop.service.synchronizer.oaipmh.SynchronizationAudit;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.LogicalOperator;
import org.easymock.Mock;
import org.easymock.TestSubject;
import java.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.easymock.EasyMock.cmp;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

@RunWith(EasyMockRunner.class)
public class RepositoryServiceTest {

    public static final long ORIGINAL_MATERIAL_ID = 234L;
    public static final String IDENTIFIER = "123456Identifier";
    public static final String IDENTIFIER2 = "123456Identifier2";
    @TestSubject
    private RepositoryService repositoryService = new RepositoryService();
    @Mock
    private RepositoryManager repositoryManager;
    @Mock
    private MaterialIterator materialIterator;
    @Mock
    private MaterialService materialService;
    @Mock
    private RepositoryDao repositoryDao;
    @Mock
    private SolrEngineService solrEngineService;
    @Mock
    private PictureService pictureService;
    @Mock
    private PictureSaver pictureSaver;

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
        expect(material1.getRepositoryIdentifier()).andReturn(IDENTIFIER).anyTimes();
        expect(material1.isDeleted()).andReturn(false).anyTimes();
        expect(material1.getPicture()).andReturn(null);
        material1.setRepository(repository);
        expect(materialService.findByRepository(repository, IDENTIFIER)).andReturn(null);
        expect(materialService.createMaterialBySystemUser(material1, SearchIndexStrategy.SKIP_UPDATE)).andReturn(new Material());

        expect(materialIterator.hasNext()).andReturn(true);
        expect(materialIterator.next()).andThrow(new RuntimeException());

        expect(materialIterator.hasNext()).andReturn(true);
        expect(materialIterator.next()).andReturn(material2);
        expect(material2.getRepositoryIdentifier()).andReturn(IDENTIFIER2).anyTimes();
        expect(material2.isDeleted()).andReturn(false).anyTimes();
        expect(material2.getPicture()).andReturn(null);
        material2.setRepository(repository);
        expect(materialService.findByRepository(repository, IDENTIFIER2)).andReturn(null);
        expect(materialService.createMaterialBySystemUser(material2, SearchIndexStrategy.SKIP_UPDATE)).andReturn(new Material());

        expectUpdateRepository(repository);

        expect(materialIterator.hasNext()).andReturn(false);

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
        expect(material.getRepositoryIdentifier()).andReturn(IDENTIFIER).anyTimes();
        expect(material.getPicture()).andReturn(null);
        expectSetters(repository, material);

        Material originalMaterial = new Material();
        originalMaterial.setId(ORIGINAL_MATERIAL_ID);
        expect(materialService.findByRepository(repository, IDENTIFIER)).andReturn(originalMaterial);
        expect(material.isDeleted()).andReturn(false).anyTimes();
        expect(materialService.updateBySystem(material, SearchIndexStrategy.SKIP_UPDATE)).andReturn(material);

        expectUpdateRepository(repository);

        expect(materialIterator.hasNext()).andReturn(false);

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
        String repositoryIdentifier = IDENTIFIER;
        expect(material.getRepositoryIdentifier()).andReturn(repositoryIdentifier).anyTimes();
        RepositoryURL repositoryURL = new RepositoryURL();
        repositoryURL.setBaseURL("http://www.xray24.ru");
        repository.setRepositoryURLs(Collections.singletonList(repositoryURL));

        material.setRepository(EasyMock.anyObject(Repository.class));

        Material originalMaterial = new Material();
        originalMaterial.setId(ORIGINAL_MATERIAL_ID);
        originalMaterial.setSource("http://www.xray24.ru");
        expect(materialService.findByRepository(repository, repositoryIdentifier)).andReturn(originalMaterial);
        expect(material.isDeleted()).andReturn(true).anyTimes();

        materialService.delete(originalMaterial);

        expectUpdateRepository(repository);

        expect(materialIterator.hasNext()).andReturn(false);

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
        expect(materialService.createMaterialBySystemUser(material, SearchIndexStrategy.SKIP_UPDATE)).andReturn(new Material());
        expect(materialIterator.hasNext()).andReturn(false);

        expect(material.getRepositoryIdentifier()).andReturn(IDENTIFIER).anyTimes();
        expect(material.isDeleted()).andReturn(false).anyTimes();
        expect(material.getPicture()).andReturn(null);
        material.setRepository(repository);
        expect(materialService.findByRepository(repository, IDENTIFIER)).andReturn(null);

        expectUpdateRepository(repository);

        replayAll(material);

        repositoryService.synchronize(repository);

        verifyAll(material);
    }

    private void expectUpdateRepository(Repository repository) {
        final LocalDateTime before = LocalDateTime.now();
        repositoryDao.updateRepository(cmp(repository, (o1, o2) -> {
            if (o1 == o2) {
                return 0;
            }

            LocalDateTime lastSynchronization = o1.getLastSynchronization();
            if (before.isBefore(lastSynchronization) || before.isEqual(lastSynchronization)) {
                return 0;
            }

            if (lastSynchronization.equals(LocalDateTime.now()) || lastSynchronization.isBefore(LocalDateTime.now())) {
                return 0;
            }

            return -1;
        }, LogicalOperator.EQUAL));
    }

    @Test
    public void getAllRepositorys() {
        @SuppressWarnings("unchecked")
        List<Repository> repositories = createMock(List.class);
        expect(repositoryDao.findAll()).andReturn(repositories);

        replayAll(repositories);

        List<Repository> allRepositorys = repositoryService.getAllRepositories();

        verifyAll(repositories);

        assertSame(repositories, allRepositorys);
    }

    @Test
    public void getAllRepositorysWhenNoRepositories() {
        expect(repositoryDao.findAll()).andReturn(new ArrayList<>());

        replayAll();

        List<Repository> allRepositorys = repositoryService.getAllRepositories();

        verifyAll();

        assertNotNull(allRepositorys);
        assertEquals(0, allRepositorys.size());
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
        boolean isRepoMaterial = repositoryService.isFromSameRepo(repository, existingMaterial);
        assertTrue(isRepoMaterial);

        isRepoMaterial = repositoryService.isFromSameRepo(repository, existingMaterial);
        assertTrue(isRepoMaterial);

        isRepoMaterial = repositoryService.isFromSameRepo(repository, existingMaterial);
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

        expect(materialService.updateBySystem(existentMaterial, SearchIndexStrategy.SKIP_UPDATE)).andReturn(existentMaterial);

        replayAll();

        Material returnedMaterial = repositoryService.updateMaterial(newMaterial, existentMaterial, new SynchronizationAudit(), MaterialHandlingStrategy.MATERIAL_IS_FROM_SAME_REPO);

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

        expect(materialService.updateBySystem(newMaterial, SearchIndexStrategy.SKIP_UPDATE)).andReturn(newMaterial);

        replayAll();

        Material returnedMaterial = repositoryService.updateMaterial(newMaterial, existentMaterial, new SynchronizationAudit(), MaterialHandlingStrategy.MATERIAL_IS_FROM_OTHER_REPO);

        verifyAll();

        assertEquals("http://www.oldLink.ee", returnedMaterial.getSource());
        assertTrue(returnedMaterial.getEmbeddable());
        assertEquals(titles, returnedMaterial.getTitles());
    }

    @Test
    public void updateRepoMaterialWithPicture() {
        Material existentMaterial = new Material();
        Material newMaterial = new Material();

        existentMaterial.setPicture(new OriginalPicture());
        newMaterial.setPicture(null);

        expect(materialService.updateBySystem(existentMaterial, SearchIndexStrategy.SKIP_UPDATE)).andReturn(existentMaterial);

        replayAll();

        Material returnedMaterial = repositoryService.updateMaterial(newMaterial, existentMaterial, new SynchronizationAudit(), MaterialHandlingStrategy.MATERIAL_IS_FROM_SAME_REPO);

        verifyAll();

        assertNotNull(returnedMaterial.getPicture());
    }

    @Test
    public void updateNonRepoMaterialWithPicture() {
        Material existentMaterial = new Material();
        Material newMaterial = new Material();
        Picture picture = picture(1);

        existentMaterial.setPicture(picture);
        newMaterial.setPicture(null);

        expect(materialService.updateBySystem(newMaterial, SearchIndexStrategy.SKIP_UPDATE)).andReturn(newMaterial);

        replayAll();

        Material returnedMaterial = repositoryService.updateMaterial(newMaterial, existentMaterial, new SynchronizationAudit(), MaterialHandlingStrategy.MATERIAL_IS_FROM_OTHER_REPO);

        verifyAll();

        assertNotNull(returnedMaterial.getPicture());
    }

    @Test
    public void updateMaterialPicture() {
        Material existentMaterial = new Material();
        existentMaterial.setPicture(picture(1));

        Material newMaterial = new Material();
        Picture picture2 = picture(2);
        newMaterial.setPicture(picture2);

        expect(materialService.updateBySystem(existentMaterial, SearchIndexStrategy.SKIP_UPDATE)).andReturn(existentMaterial);
        expect(pictureSaver.create(picture2)).andReturn(picture2);

        replayAll();

        Material returnedMaterial = repositoryService.updateMaterial(newMaterial, existentMaterial, new SynchronizationAudit(), MaterialHandlingStrategy.MATERIAL_IS_FROM_SAME_REPO);

        verifyAll();

        assertEquals(picture2.getId(), returnedMaterial.getPicture().getId());
    }

    private Picture picture(int id) {
        Picture picture1 = new OriginalPicture();
        picture1.setId(id);
        return picture1;
    }

    private Repository getRepository() {
        Repository repository = new Repository();
        repository.setBaseURL("http://koolitaja.eenet.ee:57219/Waramu3Web/OAIHandler");
        repository.setSchema("waramu");
        repository.setId((long) 1);
        repository.setLastSynchronization(LocalDateTime.now().minusDays(1));
        return repository;
    }

    private void replayAll(Object... mocks) {
        replay(repositoryManager, materialIterator, materialService, repositoryDao, solrEngineService, pictureService, pictureSaver);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

    private void verifyAll(Object... mocks) {
        verify(repositoryManager, materialIterator, materialService, repositoryDao, solrEngineService, pictureService, pictureSaver);

        if (mocks != null) {
            for (Object object : mocks) {
                verify(object);
            }
        }
    }

    private void expectSetters(Repository repository, Material material) {
        material.setChanged(0);
        expectLastCall();
        material.setDeleted(false);
        expectLastCall();
        material.setDislikes(0);
        expectLastCall();
        material.setLikes(0);
        expectLastCall();
        material.setImproper(0);
        expectLastCall();
        material.setUnReviewed(0);
        expectLastCall();
        material.setViews(0L);
        expectLastCall();
        material.setPaid(false);
        expectLastCall();
        material.setPublicationConfirmed(false);
        expectLastCall();
        material.setSpecialEducation(false);
        expectLastCall();
        material.setRepository(repository);
        expectLastCall();
        material.setId(ORIGINAL_MATERIAL_ID);
        expectLastCall();
    }
}
