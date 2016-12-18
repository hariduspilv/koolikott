package ee.hm.dop.service;

import ee.hm.dop.dao.MaterialDAO;
import ee.hm.dop.model.CrossCurricularTheme;
import ee.hm.dop.model.KeyCompetence;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.PeerReview;
import ee.hm.dop.model.Publisher;
import ee.hm.dop.model.Recommendation;
import ee.hm.dop.model.Repository;
import ee.hm.dop.model.Role;
import ee.hm.dop.model.User;
import ee.hm.dop.model.taxon.EducationalContext;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.IAnswer;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.*;

@RunWith(EasyMockRunner.class)
public class MaterialServiceTest {

    @TestSubject
    private MaterialService materialService = new MaterialService();

    @Mock
    private MaterialDAO materialDAO;

    @Mock
    private SolrEngineService solrEngineService;

    @Mock
    private PeerReviewService peerReviewService;

    @Mock
    private ChangedLearningObjectService changedLearningObjectService;

    @Test
    public void create() {
        Capture<Material> capturedMaterial = newCapture();

        Publisher publisher = new Publisher();

        User creator = new User();
        creator.setId(2000L);
        creator.setName("First");
        creator.setSurname("Last");
        creator.setPublisher(publisher);

        Material material = new Material();
        String source = "http://www.creatematerial.example.com";
        material.setSource(source);
        PeerReview peerReview = new PeerReview();
        peerReview.setUrl("http://www.azure.com");
        List<PeerReview> peerReviews = new ArrayList<>();
        peerReviews.add(peerReview);
        material.setRecommendation(new Recommendation());
        expect(materialDAO.findBySource("creatematerial.example.com", true)).andReturn(null);
        expect(peerReviewService.createPeerReview(peerReview.getUrl())).andReturn(peerReview);

        expectMaterialUpdate(capturedMaterial);
        solrEngineService.updateIndex();

        replayAll();

        Material createdMaterial = materialService.createMaterial(material, creator, true);
        material.setPeerReviews(peerReviews);

        verifyAll();

        assertSame(capturedMaterial.getValue(), createdMaterial);
        assertNull(createdMaterial.getRecommendation());
        assertTrue(createdMaterial.isCurriculumLiterature());
        assertEquals(source, createdMaterial.getSource());
    }

    @Test
    public void createMaterialWithNotNullId() {
        Material material = new Material();
        material.setId(123L);

        replay(materialDAO);

        try {
            materialService.createMaterial(material, null, false);
            fail("Exception expected.");
        } catch (IllegalArgumentException e) {
            assertEquals("Error creating Material, material already exists.", e.getMessage());
        }

        verify(materialDAO);
    }

    @Test
    public void update() {
        DateTime startOfTest = now();
        DateTime added = new DateTime("2001-10-04T10:15:45.937");
        Long views = 124l;

        Material original = new Material();
        original.setViews(views);
        original.setAdded(added);

        long materialId = 1;
        Material material = createMock(Material.class);
        expect(material.getId()).andReturn(materialId).times(3);
        expect(material.getAuthors()).andReturn(null);
        expect(material.getPublishers()).andReturn(null);
        expect(material.getSource()).andReturn("http://creatematerial.example.com").times(3);
        expect(material.getPeerReviews()).andReturn(null).times(2);
        material.setRepository(null);
        material.setRecommendation(null);
        material.setPeerReviews(null);
        material.setSource("http://creatematerial.example.com");
        solrEngineService.updateIndex();

        material.setAdded(added);
        material.setViews(views);

        Capture<DateTime> capturedUpdateDate = newCapture();
        material.setUpdated(capture(capturedUpdateDate));

        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setName(MaterialService.BASICEDUCATION);
        expect(material.getTaxons()).andReturn(Arrays.asList(educationalContext)).times(3);

        KeyCompetence keyCompetence = new KeyCompetence();
        keyCompetence.setId(1004L);
        CrossCurricularTheme crossCurricularTheme = new CrossCurricularTheme();
        crossCurricularTheme.setId(1004L);

        expect(material.getKeyCompetences()).andReturn(Collections.singletonList(keyCompetence)).anyTimes();
        expect(material.getCrossCurricularThemes()).andReturn(Collections.singletonList(crossCurricularTheme)).anyTimes();

        expect(materialDAO.findByIdNotDeleted(materialId)).andReturn(original);
        expect(materialDAO.update(material)).andReturn(material);
        expect(materialDAO.findBySource("creatematerial.example.com", true)).andReturn(null);
        expect(material.getId()).andReturn(1L);

        replay(materialDAO, material, solrEngineService);

        materialService.update(material, null, true);

        verify(materialDAO, material, solrEngineService);

        DateTime updatedDate = capturedUpdateDate.getValue();
        DateTime maxFuture = now().plusSeconds(20);
        assertTrue(startOfTest.isBefore(updatedDate) || startOfTest.isEqual(updatedDate));
        assertTrue(updatedDate.isBefore(maxFuture));
    }

    @Test
    public void updateWhenMaterialDoesNotExist() {
        long materialId = 1;
        Material material = createMock(Material.class);
        material.setSource("http://creatematerial.example.com");
        expect(material.getId()).andReturn(materialId).times(2);
        expect(material.getSource()).andReturn("http://creatematerial.example.com").times(3);

        expect(materialDAO.findByIdNotDeleted(materialId)).andReturn(null);
        expect(materialDAO.findBySource("creatematerial.example.com", true)).andReturn(null);
        expect(material.getPeerReviews()).andReturn(null);

        replay(materialDAO, material);

        try {
            materialService.update(material, null, true);
            fail("Exception expected.");
        } catch (IllegalArgumentException ex) {
            assertEquals("Error updating Material: material does not exist.", ex.getMessage());
        }

        verify(materialDAO, material);
    }

    @Test
    public void updateAddingRepository() {
        Material original = new Material();
        Repository repository = new Repository();
        original.setRepository(repository);

        long materialId = 1;
        Material material = createMock(Material.class);
        expect(material.getId()).andReturn(materialId).times(3);
        material.setRecommendation(null);
        expect(materialDAO.findByIdNotDeleted(materialId)).andReturn(original);
        material.setRepository(repository);
        expect(materialDAO.update(material)).andReturn(material);
        material.setViews(0L);
        material.setAdded(null);
        material.setPeerReviews(null);
        material.setSource("http://www.creatematerial.example.com");
        material.setUpdated(EasyMock.anyObject(DateTime.class));
        expect(material.getAuthors()).andReturn(null);
        expect(material.getPublishers()).andReturn(null);
        expect(material.getTaxons()).andReturn(null);
        expect(material.getPeerReviews()).andReturn(null).times(2);
        expect(material.getSource()).andReturn("http://www.creatematerial.example.com").times(3);
        expect(materialDAO.findBySource("creatematerial.example.com", true)).andReturn(null);
        material.setKeyCompetences(null);
        material.setCrossCurricularThemes(null);

        KeyCompetence keyCompetence = new KeyCompetence();
        keyCompetence.setId(1004L);
        CrossCurricularTheme crossCurricularTheme = new CrossCurricularTheme();
        crossCurricularTheme.setId(1004L);

        expect(material.getKeyCompetences()).andReturn(Collections.singletonList(keyCompetence)).anyTimes();
        expect(material.getCrossCurricularThemes()).andReturn(Collections.singletonList(crossCurricularTheme)).anyTimes();
        expect(material.getId()).andReturn(1L);

        replay(materialDAO, material);

        Material returned = materialService.update(material, null, true);

        assertNotNull(returned);
        verify(materialDAO, material);
    }

    @Test
    public void delete() {
        Material material = createMock(Material.class);
        materialDAO.delete(material);

        replay(materialDAO, material);

        materialService.delete(material);

        verify(materialDAO, material);
    }

    @Test
    public void deleteByAdmin() {
        Long materialID = 15L;

        Material originalMaterial = new Material();
        originalMaterial.setId(15L);

        User user = new User();
        user.setRole(Role.ADMIN);

        expect(materialDAO.findByIdNotDeleted(materialID)).andReturn(originalMaterial);
        materialDAO.delete(originalMaterial);
        solrEngineService.updateIndex();

        replayAll();

        materialService.delete(materialID, user);

        verifyAll();
    }

    @Test
    public void userCanNotDeleteRepositoryMaterial() {
        Long materialID = 15L;

        Material originalMaterial = new Material();
        originalMaterial.setId(materialID);
        originalMaterial.setRepository(new Repository());
        originalMaterial.setRepositoryIdentifier("asd");

        User user = new User();
        user.setRole(Role.USER);

        expect(materialDAO.findByIdNotDeleted(materialID)).andReturn(originalMaterial);

        replayAll();

        try {
            materialService.delete(materialID, user);
        } catch (RuntimeException e) {
            assertEquals("Logged in user must be an administrator.", e.getMessage());
        }

        verifyAll();
    }

    @Test
    public void restore() {
        Long materialID = 15L;

        Material material = new Material();
        material.setId(materialID);

        Material originalMaterial = new Material();
        originalMaterial.setId(15L);

        User user = new User();
        user.setRole(Role.ADMIN);

        expect(materialDAO.findById(materialID)).andReturn(originalMaterial);
        materialDAO.restore(originalMaterial);
        solrEngineService.updateIndex();

        replayAll();

        materialService.restore(material, user);

        verifyAll();
    }

    @Test
    public void userCanNotRestoreRepositoryMaterial() {
        Long materialID = 15L;

        Material material = new Material();
        material.setId(materialID);

        Material originalMaterial = new Material();
        originalMaterial.setId(materialID);
        originalMaterial.setRepository(new Repository());
        originalMaterial.setRepositoryIdentifier("asd");

        User user = new User();
        user.setRole(Role.USER);

        expect(materialDAO.findById(materialID)).andReturn(originalMaterial);

        replayAll();

        try {
            materialService.restore(material, user);
        } catch (RuntimeException e) {
            assertEquals("Logged in user must be an administrator.", e.getMessage());
        }

        verifyAll();
    }

    @Test
    public void updateByUserNullMaterial() {
        User user = createMock(User.class);

        replay(user);

        try {
            materialService.update(null, user, true);
            fail("Exception expected.");
        } catch (IllegalArgumentException ex) {
            assertEquals("Material id parameter is mandatory", ex.getMessage());
        }

        verify(user);
    }

    @Test
    public void updateByUserIsAdmin() {
        User user = createMock(User.class);
        Material material = new Material();
        material.setId(1L);
        material.setRepository(null);
        material.setSource("http://creatematerial.example.com");

        expect(materialDAO.findById(material.getId())).andReturn(material).anyTimes();
        expect(user.getRole()).andReturn(Role.ADMIN).anyTimes();
        expect(materialDAO.update(material)).andReturn(material);
        expect(materialDAO.findBySource("creatematerial.example.com", true)).andReturn(null);
        expect(changedLearningObjectService.getAllByLearningObject(material.getId())).andReturn(null);
        solrEngineService.updateIndex();

        replay(user, materialDAO, solrEngineService, changedLearningObjectService);

        Material returned = materialService.update(material, user, true);

        assertNotNull(returned);
        verify(user, materialDAO, solrEngineService);
    }

    @Test
    public void updateByUserIsPublisher() {
        User user = createMock(User.class);
        Material material = new Material();
        material.setId(1L);
        material.setRepository(null);
        material.setCreator(user);
        material.setSource("http://creatematerial.example.com");

        expect(materialDAO.findByIdNotDeleted(material.getId())).andReturn(material).anyTimes();
        expect(user.getRole()).andReturn(Role.USER).anyTimes();
        expect(materialDAO.update(material)).andReturn(material);
        expect(user.getUsername()).andReturn("username").anyTimes();
        expect(materialDAO.findBySource("creatematerial.example.com", true)).andReturn(null);
        expect(changedLearningObjectService.getAllByLearningObject(material.getId())).andReturn(null);

        replay(user, materialDAO, changedLearningObjectService);

        Material returned = materialService.update(material, user, true);

        assertNotNull(returned);
        verify(user, materialDAO);
    }

    @Test
    public void addRecommendation() {
        Capture<Material> capturedMaterial = newCapture();

        User user = createMock(User.class);
        Material material = new Material();
        material.setId(1L);
        material.setRepository(null);

        expect(materialDAO.findByIdNotDeleted(material.getId())).andReturn(material).anyTimes();
        expect(user.getRole()).andReturn(Role.ADMIN).anyTimes();
        expectMaterialUpdate(capturedMaterial);
        solrEngineService.updateIndex();

        replayAll(user);

        Recommendation returnedRecommendation = materialService.addRecommendation(material, user);

        verifyAll(user);

        Recommendation recommendation = capturedMaterial.getValue().getRecommendation();
        assertNotNull(recommendation);
        assertEquals(user, recommendation.getCreator());
        assertEquals(recommendation, returnedRecommendation);
    }

    private void expectMaterialUpdate(Capture<Material> capturedMaterial) {
        expect(materialDAO.update(EasyMock.capture(capturedMaterial))).andAnswer(new IAnswer<Material>() {
            @Override
            public Material answer() throws Throwable {
                return capturedMaterial.getValue();
            }
        });
    }

    @Test
    public void removeRecommendation() {
        Capture<Material> capturedMaterial = newCapture();

        Recommendation recommendation = new Recommendation();
        recommendation.setCreator(new User());
        recommendation.setAdded(DateTime.now());

        User user = createMock(User.class);
        Material material = new Material();
        material.setId(1L);
        material.setRepository(null);
        material.setRecommendation(recommendation);

        expect(materialDAO.findByIdNotDeleted(material.getId())).andReturn(material).anyTimes();
        expect(user.getRole()).andReturn(Role.ADMIN).anyTimes();
        expectMaterialUpdate(capturedMaterial);
        solrEngineService.updateIndex();

        replayAll(user);

        materialService.removeRecommendation(material, user);

        assertNull(capturedMaterial.getValue().getRecommendation());

        verifyAll(user);
    }

    private void replayAll(Object... mocks) {
        replay(materialDAO, solrEngineService);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

    private void verifyAll(Object... mocks) {
        verify(materialDAO, solrEngineService);

        if (mocks != null) {
            for (Object object : mocks) {
                verify(object);
            }
        }
    }
}
