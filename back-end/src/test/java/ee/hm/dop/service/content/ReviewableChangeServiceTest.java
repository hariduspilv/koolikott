package ee.hm.dop.service.content;

import ee.hm.dop.dao.ReviewableChangeDao;
import ee.hm.dop.dao.LearningObjectDao;
import ee.hm.dop.model.ReviewableChange;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.ResourceType;
import ee.hm.dop.model.TargetGroup;
import ee.hm.dop.model.User;
import ee.hm.dop.model.taxon.Subject;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.reviewmanagement.ReviewableChangeService;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

import static junit.framework.TestCase.*;
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

    @Test
    public void addChanged() {
        ReviewableChange reviewableChange = new ReviewableChange();
        Material material = new Material();
        material.setId(1L);
        ResourceType resourceType = new ResourceType();
        resourceType.setId(3L);
        User user = new User();
        user.setId(1L);

        reviewableChange.setLearningObject(material);
        reviewableChange.setResourceType(resourceType);
        reviewableChange.setCreatedBy(user);

        expect(learningObjectService.get(1L, user)).andReturn(material);
        expect(reviewableChangeDao.createOrUpdate(reviewableChange)).andReturn(reviewableChange);
        expect(reviewableChangeDao.findAll()).andReturn(Collections.singletonList(reviewableChange));
        replay(learningObjectService);
        replay(reviewableChangeDao);

        ReviewableChange updated = reviewableChangeService.addChanged(reviewableChange);

        assertEquals(reviewableChange.getId(), updated.getId());
        assertNotNull(updated.getResourceType());
        assertTrue(reviewableChangeService.findAll().size() == 1);
    }

    @Test
    public void revertAllChanges() {
        ReviewableChange change1 = new ReviewableChange();
        ReviewableChange change2 = new ReviewableChange();
        Material material = new Material();
        ResourceType resourceType = new ResourceType();
        TargetGroup targetGroup = new TargetGroup();
        User user = new User();

        material.setId(1L);
        resourceType.setId(3L);
        user.setId(1L);
        targetGroup.setId(5L);
        material.setTargetGroups(new LinkedList<>(Collections.singletonList(targetGroup)));
        material.setResourceTypes(new LinkedList<>(Collections.singletonList(resourceType)));

        change1.setLearningObject(material);
        change1.setResourceType(resourceType);
        change1.setCreatedBy(user);

        change2.setLearningObject(material);
        change2.setTargetGroup(targetGroup);
        change2.setCreatedBy(user);

        expect(learningObjectService.get(1L, user)).andReturn(material);
        expect(reviewableChangeDao.getAllByLearningObject(1L)).andReturn(Arrays.asList(change1, change2));
        expect(reviewableChangeDao.removeAllByLearningObject(1L)).andReturn(true);
        expect(learningObjectDao.createOrUpdate(material)).andReturn(material);
        replay(learningObjectService);
        replay(reviewableChangeDao);
        replay(learningObjectDao);

        LearningObject updated = reviewableChangeService.revertAllChanges(1L, user);

        assertTrue(updated.getTargetGroups().size() == 0);
        assertTrue(((Material) updated).getResourceTypes().size() == 0);
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
