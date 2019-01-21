package ee.hm.dop.service.reviewmanagement;

import ee.hm.dop.dao.LearningObjectDao;
import ee.hm.dop.dao.ReviewableChangeDao;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.ResourceType;
import ee.hm.dop.model.ReviewableChange;
import ee.hm.dop.model.TargetGroup;
import ee.hm.dop.model.User;
import ee.hm.dop.model.taxon.Subject;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.content.LearningObjectService;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

@RunWith(EasyMockRunner.class)
public class ReviewableChangeServiceTest {

    @TestSubject
    private ReviewableChangeService reviewableChangeService = new ReviewableChangeService();
    @Mock
    private LearningObjectService learningObjectService;
    @Mock
    private ReviewableChangeDao reviewableChangeDao;
    @Mock
    private LearningObjectDao learningObjectDao;
    @Mock
    private ReviewManager reviewManager;

    @Test
    public void addChanged() {
        ReviewableChange reviewableChange = new ReviewableChange();
        Material material = new Material();
        material.setId(1L);
        material.setReviewableChanges(new ArrayList<>());
        ResourceType resourceType = new ResourceType();
        resourceType.setId(3L);
        User user = new User();
        user.setId(1L);

        reviewableChange.setLearningObject(material);
        reviewableChange.setResourceType(resourceType);
        reviewableChange.setCreatedBy(user);

        expect(learningObjectService.get(1L, user)).andReturn(material);
        expect(reviewableChangeDao.createOrUpdate(EasyMock.anyObject(ReviewableChange.class))).andReturn(reviewableChange);
        replay(learningObjectService);
        replay(reviewableChangeDao);

        ReviewableChange updated = reviewableChangeService.registerChange(material, user, null, resourceType, null, null);

        assertEquals(reviewableChange.getId(), updated.getId());
        assertNotNull(updated.getResourceType());
    }

    @Test
    public void materialHasThisTaxon() {
        Material material = new Material();

        ReviewableChange change1 = new ReviewableChange();
        ReviewableChange change2 = new ReviewableChange();
        ReviewableChange change3 = new ReviewableChange();

        Taxon subject = new Subject();
        TargetGroup targetGroup = new TargetGroup();
        ResourceType resourceType = new ResourceType();

        change1.setTaxon(subject);
        change2.setTargetGroup(targetGroup);
        change3.setResourceType(resourceType);

        material.setTaxons(Collections.singletonList(subject));

        assertTrue(reviewableChangeService.learningObjectHasThis(material, change1));
        assertFalse(reviewableChangeService.learningObjectHasThis(material, change2));
        assertFalse(reviewableChangeService.learningObjectHasThis(material, change3));
    }

    @Test
    public void materialHasThisResourceType() {
        Material material = new Material();

        ReviewableChange change1 = new ReviewableChange();
        ReviewableChange change2 = new ReviewableChange();
        ReviewableChange change3 = new ReviewableChange();

        Taxon subject = new Subject();
        TargetGroup targetGroup = new TargetGroup();
        ResourceType resourceType = new ResourceType();

        change1.setTaxon(subject);
        change2.setTargetGroup(targetGroup);
        change3.setResourceType(resourceType);

        material.setResourceTypes(Collections.singletonList(resourceType));

        assertFalse(reviewableChangeService.learningObjectHasThis(material, change1));
        assertFalse(reviewableChangeService.learningObjectHasThis(material, change2));
        assertTrue(reviewableChangeService.learningObjectHasThis(material, change3));
    }

    @Test
    public void materialHasThisTargetGroup() {
        Material material = new Material();

        ReviewableChange change1 = new ReviewableChange();
        ReviewableChange change2 = new ReviewableChange();
        ReviewableChange change3 = new ReviewableChange();

        Taxon subject = new Subject();
        TargetGroup targetGroup = new TargetGroup();
        ResourceType resourceType = new ResourceType();

        change1.setTaxon(subject);
        change2.setTargetGroup(targetGroup);
        change3.setResourceType(resourceType);

        material.setTargetGroups(Collections.singletonList(targetGroup));

        assertFalse(reviewableChangeService.learningObjectHasThis(material, change1));
        assertTrue(reviewableChangeService.learningObjectHasThis(material, change2));
        assertFalse(reviewableChangeService.learningObjectHasThis(material, change3));
    }

    @Test
    public void portfolioHasThisTaxon() {
        Portfolio portfolio = new Portfolio();

        ReviewableChange change1 = new ReviewableChange();
        ReviewableChange change2 = new ReviewableChange();

        Taxon subject = new Subject();
        TargetGroup targetGroup = new TargetGroup();

        change1.setTaxon(subject);
        change2.setTargetGroup(targetGroup);

        portfolio.setTargetGroups(Collections.singletonList(targetGroup));

        assertFalse(reviewableChangeService.learningObjectHasThis(portfolio, change1));
        assertTrue(reviewableChangeService.learningObjectHasThis(portfolio, change2));
    }
}
