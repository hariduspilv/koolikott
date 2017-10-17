package ee.hm.dop.service.content;

import ee.hm.dop.dao.MaterialDao;
import ee.hm.dop.model.CrossCurricularTheme;
import ee.hm.dop.model.KeyCompetence;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.PeerReview;
import ee.hm.dop.model.Publisher;
import ee.hm.dop.model.Recommendation;
import ee.hm.dop.model.Repository;
import ee.hm.dop.model.enums.EducationalContextC;
import ee.hm.dop.model.enums.Role;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.Visibility;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.service.content.enums.GetMaterialStrategy;
import ee.hm.dop.service.content.enums.SearchIndexStrategy;
import ee.hm.dop.service.reviewmanagement.ChangedLearningObjectService;
import ee.hm.dop.service.reviewmanagement.FirstReviewAdminService;
import ee.hm.dop.service.useractions.PeerReviewService;
import ee.hm.dop.service.solr.SolrEngineService;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.*;

@RunWith(EasyMockRunner.class)
public class MaterialServiceTest {

    public static final String SOURCE = "http://creatematerial.example.com";
    public static final String SOURCE_WWW = "http://www.creatematerial.example.com";
    @TestSubject
    private MaterialService materialService = new MaterialService();
    @Mock
    private MaterialDao materialDao;
    @Mock
    private SolrEngineService solrEngineService;
    @Mock
    private PeerReviewService peerReviewService;
    @Mock
    private ChangedLearningObjectService changedLearningObjectService;
    @Mock
    private FirstReviewAdminService firstReviewAdminService;
    @Mock
    private MaterialGetter materialGetter;

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
        String source = SOURCE_WWW;
        material.setSource(source);
        PeerReview peerReview = new PeerReview();
        peerReview.setUrl("http://www.azure.com");
        List<PeerReview> peerReviews = new ArrayList<>();
        peerReviews.add(peerReview);
        material.setRecommendation(new Recommendation());
        expect(materialGetter.getBySource(SOURCE_WWW, GetMaterialStrategy.INCLUDE_DELETED)).andReturn(null);
        expect(peerReviewService.createPeerReview(peerReview.getUrl())).andReturn(peerReview);

        expectMaterialUpdate(capturedMaterial);
        expect(firstReviewAdminService.save(material)).andReturn(null);
        solrEngineService.updateIndex();

        replayAll();

        Material createdMaterial = materialService.createMaterial(material, creator, SearchIndexStrategy.UPDATE_INDEX);
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

        replay(materialDao);

        try {
            materialService.createMaterialBySystemUser(material, SearchIndexStrategy.SKIP_UPDATE);
            fail("Exception expected.");
        } catch (IllegalArgumentException e) {
            assertEquals("Error creating Material, material already exists.", e.getMessage());
        }

        verify(materialDao);
    }

    @Test
    public void update() {
        DateTime startOfTest = now();
        DateTime added = new DateTime("2001-10-04T10:15:45.937");
        Long views = 124L;

        Material original = new Material();
        original.setViews(views);
        original.setAdded(added);

        long materialId = 1;
        Material material = createMock(Material.class);

        expect(material.getId()).andReturn(materialId).times(3);
        expect(material.getAuthors()).andReturn(null);
        expect(material.getPublishers()).andReturn(null);
        expect(material.getSource()).andReturn(SOURCE).times(3);
        expect(material.getPeerReviews()).andReturn(null).times(2);
        expect(material.getTitles()).andReturn(null);
        expect(material.getDescriptions()).andReturn(null);

        material.setRepository(null);
        material.setRecommendation(null);
        material.setPeerReviews(null);
        material.setSource(SOURCE);
        material.setVisibility(Visibility.PUBLIC);
        solrEngineService.updateIndex();

        material.setAdded(added);
        material.setViews(views);

        Capture<DateTime> capturedUpdateDate = newCapture();
        material.setUpdated(capture(capturedUpdateDate));

        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setName(EducationalContextC.BASICEDUCATION);
        expect(material.getTaxons()).andReturn(Collections.singletonList(educationalContext)).times(2);

        KeyCompetence keyCompetence = new KeyCompetence();
        keyCompetence.setId(1004L);
        CrossCurricularTheme crossCurricularTheme = new CrossCurricularTheme();
        crossCurricularTheme.setId(1004L);

        expect(material.getKeyCompetences()).andReturn(Collections.singletonList(keyCompetence)).anyTimes();
        expect(material.getCrossCurricularThemes()).andReturn(Collections.singletonList(crossCurricularTheme)).anyTimes();

        expect(materialGetter.get(materialId, null)).andReturn(original);
        expect(materialDao.createOrUpdate(material)).andReturn(material);
        expect(materialGetter.getBySource(SOURCE, GetMaterialStrategy.INCLUDE_DELETED)).andReturn(null);
        expect(material.getId()).andReturn(1L);

        replay(materialDao, material, solrEngineService, materialGetter);

        materialService.updateBySystem(material, SearchIndexStrategy.UPDATE_INDEX);

        verify(materialDao, material, solrEngineService, materialGetter);

        DateTime updatedDate = capturedUpdateDate.getValue();
        DateTime maxFuture = now().plusSeconds(20);
        assertTrue(startOfTest.isBefore(updatedDate) || startOfTest.isEqual(updatedDate));
        assertTrue(updatedDate.isBefore(maxFuture));
    }

    @Test
    public void updateWhenMaterialDoesNotExist() {
        long materialId = 1;
        Material material = createMock(Material.class);
        material.setSource(SOURCE);
        expect(material.getId()).andReturn(materialId).times(2);
        expect(material.getSource()).andReturn(SOURCE).times(3);

        expect(material.getPeerReviews()).andReturn(null);
        expect(materialGetter.getBySource(SOURCE, GetMaterialStrategy.INCLUDE_DELETED)).andReturn(null);
        expect(materialGetter.get(materialId,null)).andReturn(null);

        replay(materialDao, material, materialGetter);

        try {
            materialService.updateBySystem(material, SearchIndexStrategy.UPDATE_INDEX);
            fail("Exception expected.");
        } catch (IllegalArgumentException ex) {
            assertEquals("Error updating Material: material does not exist.", ex.getMessage());
        }

        verify(materialDao, material, materialGetter);
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

        material.setRepository(repository);

        expect(materialDao.createOrUpdate(material)).andReturn(material);

        material.setViews(0L);
        material.setAdded(null);
        material.setPeerReviews(null);
        material.setSource(SOURCE_WWW);
        material.setUpdated(EasyMock.anyObject(DateTime.class));

        expect(material.getAuthors()).andReturn(null);
        expect(material.getPublishers()).andReturn(null);
        expect(material.getTaxons()).andReturn(null);
        expect(material.getPeerReviews()).andReturn(null).times(2);
        expect(material.getSource()).andReturn(SOURCE_WWW).times(3);
        expect(materialGetter.getBySource(SOURCE_WWW, GetMaterialStrategy.INCLUDE_DELETED)).andReturn(null);
        expect(materialGetter.get(materialId,null)).andReturn(original);
        expect(material.getTitles()).andReturn(null);
        expect(material.getDescriptions()).andReturn(null);

        material.setKeyCompetences(null);
        material.setCrossCurricularThemes(null);
        material.setVisibility(Visibility.PUBLIC);

        KeyCompetence keyCompetence = new KeyCompetence();
        keyCompetence.setId(1004L);
        CrossCurricularTheme crossCurricularTheme = new CrossCurricularTheme();
        crossCurricularTheme.setId(1004L);

        expect(material.getKeyCompetences()).andReturn(Collections.singletonList(keyCompetence)).anyTimes();
        expect(material.getCrossCurricularThemes()).andReturn(Collections.singletonList(crossCurricularTheme)).anyTimes();
        expect(material.getId()).andReturn(1L);

        replay(materialDao, material, materialGetter);

        Material returned = materialService.updateBySystem(material, SearchIndexStrategy.UPDATE_INDEX);

        assertNotNull(returned);
        verify(materialDao, material, materialGetter);
    }

    @Test
    public void delete() {
        Material material = createMock(Material.class);
        materialDao.delete(material);

        replay(materialDao, material);

        materialService.delete(material);

        verify(materialDao, material);
    }

    @Test
    public void updateByUserNullMaterial() {
        User user = createMock(User.class);

        replay(user);

        try {
            materialService.update(null, user, SearchIndexStrategy.UPDATE_INDEX);
            fail("Exception expected.");
        } catch (RuntimeException ex) {
            assertEquals("Material not found", ex.getMessage());
        }

        verify(user);
    }

    @Test
    public void updateByUserIsAdmin() {
        User user = createMock(User.class);
        Material material = new Material();
        material.setId(1L);
        material.setRepository(null);
        material.setSource(SOURCE);

        expect(materialGetter.get(material.getId(), user)).andReturn(material).anyTimes();
        expect(materialDao.findById(material.getId())).andReturn(material).anyTimes();
        expect(user.getRole()).andReturn(Role.ADMIN).anyTimes();
        expect(materialDao.createOrUpdate(material)).andReturn(material);
        expect(materialGetter.getBySource(SOURCE, GetMaterialStrategy.INCLUDE_DELETED)).andReturn(null);
        expect(changedLearningObjectService.getAllByLearningObject(material.getId())).andReturn(null);
        solrEngineService.updateIndex();

        replay(user, materialDao, solrEngineService, changedLearningObjectService, materialGetter);

        Material returned = materialService.update(material, user, SearchIndexStrategy.UPDATE_INDEX);

        assertNotNull(returned);
        verify(user, materialDao, solrEngineService, materialGetter);
    }

    @Test
    public void updateByUserIsPublisher() {
        User user = createMock(User.class);
        Material material = new Material();
        material.setId(1L);
        material.setRepository(null);
        material.setCreator(user);
        material.setSource(SOURCE);

        expect(materialDao.findByIdNotDeleted(material.getId())).andReturn(material).anyTimes();
        expect(user.getRole()).andReturn(Role.USER).anyTimes();
        expect(materialDao.createOrUpdate(material)).andReturn(material);
        expect(user.getId()).andReturn(1L).anyTimes();
        expect(materialGetter.getBySource(SOURCE, GetMaterialStrategy.INCLUDE_DELETED)).andReturn(null);
        expect(materialGetter.get(material.getId(), user)).andReturn(material);
        expect(changedLearningObjectService.getAllByLearningObject(material.getId())).andReturn(null);

        replay(user, materialDao, changedLearningObjectService, materialGetter);

        Material returned = materialService.update(material, user, SearchIndexStrategy.UPDATE_INDEX);

        assertNotNull(returned);
        verify(user, materialDao, materialGetter);
    }

    private void expectMaterialUpdate(Capture<Material> capturedMaterial) {
        expect(materialDao.createOrUpdate(EasyMock.capture(capturedMaterial))).andAnswer(capturedMaterial::getValue);
    }

    private void replayAll(Object... mocks) {
        replay(materialDao, materialGetter, solrEngineService);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

    private void verifyAll(Object... mocks) {
        verify(materialDao, materialGetter, solrEngineService);

        if (mocks != null) {
            for (Object object : mocks) {
                verify(object);
            }
        }
    }
}
