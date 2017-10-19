package ee.hm.dop.service.content;

import ee.hm.dop.common.test.TestConstants;
import ee.hm.dop.dao.LearningObjectDao;
<<<<<<< HEAD
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.Recommendation;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.Role;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.service.useractions.PeerReviewService;
=======
import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.Role;
import ee.hm.dop.service.reviewmanagement.*;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.service.useractions.PeerReviewService;
import ee.hm.dop.utils.UserUtil;
>>>>>>> new-develop
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
public class LearningObjectAdministrationServiceTest {

    @TestSubject
    private LearningObjectAdministrationService learningObjectAdministrationService = new LearningObjectAdministrationService();
    @Mock
    private LearningObjectDao learningObjectDao;
    @Mock
    private SolrEngineService solrEngineService;
    @Mock
    private PeerReviewService peerReviewService;
    @Mock
    private ChangedLearningObjectService changedLearningObjectService;
    @Mock
<<<<<<< HEAD
    private FirstReviewService firstReviewService;
    @Mock
    private LearningObjectService learningObjectService;
=======
    private FirstReviewAdminService firstReviewAdminService;
    @Mock
    private LearningObjectService learningObjectService;
    @Mock
    private ImproperContentService improperContentService;
    @Mock
    private MaterialAdministrationService materialAdministrationService;
    @Mock
    private BrokenContentService brokenContentService;
    @Mock
    private ReviewManager reviewManager;
>>>>>>> new-develop

    @Test
    public void addRecommendationMaterial() {
        Capture<Material> capturedMaterial = newCapture();

        User user = createMock(User.class);
        Material material = new Material();
        material.setId(1L);
        material.setRepository(null);

        expect(learningObjectService.validateAndFind(material)).andReturn(material);
        expect(user.getRole()).andReturn(Role.ADMIN).anyTimes();
        expectMaterialUpdate(capturedMaterial);
        solrEngineService.updateIndex();

        replayAll(user);

        Recommendation returnedRecommendation = learningObjectAdministrationService.addRecommendation(material, user);

        verifyAll(user);

        Recommendation recommendation = capturedMaterial.getValue().getRecommendation();
        assertNotNull(recommendation);
        assertEquals(user, recommendation.getCreator());
        assertEquals(recommendation, returnedRecommendation);
    }

    private void expectMaterialUpdate(Capture<Material> capturedMaterial) {
        expect(learningObjectDao.createOrUpdate(EasyMock.capture(capturedMaterial))).andAnswer(capturedMaterial::getValue);
    }

    @Test
    public void removeRecommendationMaterial() {
        Capture<Material> capturedMaterial = newCapture();

        Recommendation recommendation = new Recommendation();
        recommendation.setCreator(new User());
        recommendation.setAdded(DateTime.now());

        User user = createMock(User.class);
        Material material = new Material();
        material.setId(1L);
        material.setRepository(null);
        material.setRecommendation(recommendation);

        expect(learningObjectService.validateAndFind(material)).andReturn(material);
        expect(user.getRole()).andReturn(Role.ADMIN).anyTimes();
        expectMaterialUpdate(capturedMaterial);
        solrEngineService.updateIndex();

        replayAll(user);

        learningObjectAdministrationService.removeRecommendation(material, user);

        assertNull(capturedMaterial.getValue().getRecommendation());

        verifyAll(user);
    }

    @Test
    public void addRecommendationPortfolio() {
        Capture<Portfolio> capturedPortfolio = newCapture();

        User user = new User();
        user.setId(TestConstants.PORTFOLIO_11);
        user.setRole(Role.USER);

        User admin = new User();
        admin.setId(222L);
        admin.setRole(Role.ADMIN);

        Portfolio portfolio = new Portfolio();
        portfolio.setId(1L);
        portfolio.setTitle("Super title");

        Portfolio originalPortfolio = new Portfolio();
        originalPortfolio.setId(1L);
        originalPortfolio.setTitle("Super title");
        originalPortfolio.setCreator(user);

        expect(learningObjectService.validateAndFind(portfolio)).andReturn(originalPortfolio);
        expectPortfolioUpdate(capturedPortfolio);
        solrEngineService.updateIndex();

        replayAll();

        Recommendation returnedRecommendation = learningObjectAdministrationService.addRecommendation(portfolio, admin);

        verifyAll();

        Recommendation recommendation = capturedPortfolio.getValue().getRecommendation();
        assertNotNull(recommendation);
        assertEquals(admin.getId(), recommendation.getCreator().getId());
        assertEquals(recommendation, returnedRecommendation);
    }

    @Test
    public void removeRecommendationPortfolio() {
        Capture<Portfolio> capturedPortfolio = newCapture();

        User user = new User();
        user.setId(TestConstants.PORTFOLIO_11);
        user.setRole(Role.USER);

        User admin = new User();
        admin.setId(222L);
        admin.setRole(Role.ADMIN);

        Portfolio portfolio = new Portfolio();
        portfolio.setId(1L);
        portfolio.setTitle("Super title");

        Portfolio originalPortfolio = new Portfolio();
        originalPortfolio.setId(1L);
        originalPortfolio.setTitle("Super title");
        originalPortfolio.setCreator(user);

        expect(learningObjectService.validateAndFind(portfolio)).andReturn(originalPortfolio);
        expectPortfolioUpdate(capturedPortfolio);
        solrEngineService.updateIndex();

        replayAll();

        learningObjectAdministrationService.removeRecommendation(portfolio, admin);

        verifyAll();

        Recommendation recommendation = capturedPortfolio.getValue().getRecommendation();
        assertNull(recommendation);
    }

<<<<<<< HEAD
=======

    @Test
    public void restore() {
        Long materialID = 15L;

        Material material = new Material();
        material.setId(materialID);

        Material originalMaterial = new Material();
        originalMaterial.setId(15L);

        User user = new User();
        user.setRole(Role.ADMIN);

        learningObjectDao.restore(originalMaterial);
        solrEngineService.updateIndex();

//        improperContentService.setReviewed(material.getImproperContents(), user);
        brokenContentService.setMaterialNotBroken(material);
        expect(learningObjectService.validateAndFindDeletedOnly(material)).andReturn(originalMaterial);

        replayAll();

        learningObjectAdministrationService.restore(material, user);

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

//        expect(materialDao.findById(materialID)).andReturn(originalMaterial);

        replayAll();

        try {
            learningObjectAdministrationService.restore(material, user);
        } catch (RuntimeException e) {
            assertEquals(UserUtil.MUST_BE_ADMIN, e.getMessage());
        }

        verifyAll();
    }

    @Test
    public void deleteByAdmin() {
        Material originalMaterial = new Material();
        originalMaterial.setId(15L);

        User user = new User();
        user.setRole(Role.ADMIN);

        learningObjectDao.delete(originalMaterial);
        expect(learningObjectService.validateAndFind(originalMaterial)).andReturn(originalMaterial);
        solrEngineService.updateIndex();

        replayAll();

        learningObjectAdministrationService.delete(originalMaterial, user);

        verifyAll();
    }

    @Test
    public void userCanNotDeleteRepositoryMaterial() {
        Material originalMaterial = new Material();
        originalMaterial.setId(15L);
        originalMaterial.setRepository(new Repository());
        originalMaterial.setRepositoryIdentifier("asd");

        User user = new User();
        user.setRole(Role.USER);

        expect(learningObjectService.validateAndFind(originalMaterial)).andReturn(originalMaterial);
        replayAll();

        try {
            learningObjectAdministrationService.delete(originalMaterial, user);
        } catch (RuntimeException e) {
            assertEquals(UserUtil.MUST_BE_ADMIN_OR_MODERATOR, e.getMessage());
        }

        verifyAll();
    }


>>>>>>> new-develop
    private void expectPortfolioUpdate(Capture<Portfolio> capturedPortfolio) {
        expect(learningObjectDao.createOrUpdate(EasyMock.capture(capturedPortfolio))).andAnswer(capturedPortfolio::getValue);
    }

    private void replayAll(Object... mocks) {
        replay(learningObjectService, learningObjectDao, solrEngineService);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

    private void verifyAll(Object... mocks) {
        verify(learningObjectService, learningObjectDao, solrEngineService);

        if (mocks != null) {
            for (Object object : mocks) {
                verify(object);
            }
        }
    }
}
