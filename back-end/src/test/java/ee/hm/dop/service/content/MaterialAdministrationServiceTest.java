package ee.hm.dop.service.content;

import ee.hm.dop.dao.MaterialDao;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Recommendation;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.Role;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.service.useractions.PeerReviewService;
import org.easymock.*;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.easymock.EasyMock.*;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(EasyMockRunner.class)
public class MaterialAdministrationServiceTest {

    @TestSubject
    private MaterialAdministrationService materialAdministrationService = new MaterialAdministrationService();
    @Mock
    private MaterialDao materialDao;
    @Mock
    private SolrEngineService solrEngineService;
    @Mock
    private PeerReviewService peerReviewService;
    @Mock
    private ChangedLearningObjectService changedLearningObjectService;
    @Mock
    private FirstReviewService firstReviewService;
    @Mock
    private MaterialService materialService;

    @Test
    public void addRecommendation() {
        Capture<Material> capturedMaterial = newCapture();

        User user = createMock(User.class);
        Material material = new Material();
        material.setId(1L);
        material.setRepository(null);

        expect(materialService.validateAndFindNotDeleted(material)).andReturn(material);
        expect(user.getRole()).andReturn(Role.ADMIN).anyTimes();
        expectMaterialUpdate(capturedMaterial);
        solrEngineService.updateIndex();

        replayAll(user);

        Recommendation returnedRecommendation = materialAdministrationService.addRecommendation(material, user);

        verifyAll(user);

        Recommendation recommendation = capturedMaterial.getValue().getRecommendation();
        assertNotNull(recommendation);
        assertEquals(user, recommendation.getCreator());
        assertEquals(recommendation, returnedRecommendation);
    }

    private void expectMaterialUpdate(Capture<Material> capturedMaterial) {
        expect(materialDao.createOrUpdate(EasyMock.capture(capturedMaterial))).andAnswer(capturedMaterial::getValue);
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

        expect(materialService.validateAndFindNotDeleted(material)).andReturn(material);
        expect(user.getRole()).andReturn(Role.ADMIN).anyTimes();
        expectMaterialUpdate(capturedMaterial);
        solrEngineService.updateIndex();

        replayAll(user);

        materialAdministrationService.removeRecommendation(material, user);

        assertNull(capturedMaterial.getValue().getRecommendation());

        verifyAll(user);
    }

    private void replayAll(Object... mocks) {
        replay(materialService, materialDao, solrEngineService);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

    private void verifyAll(Object... mocks) {
        verify(materialService, materialDao, solrEngineService);

        if (mocks != null) {
            for (Object object : mocks) {
                verify(object);
            }
        }
    }
}
