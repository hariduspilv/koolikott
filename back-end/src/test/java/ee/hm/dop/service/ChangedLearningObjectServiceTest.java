package ee.hm.dop.service;

import ee.hm.dop.dao.ChangedLearningObjectDAO;
import ee.hm.dop.dao.LearningObjectDAO;
import ee.hm.dop.model.ChangedLearningObject;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.ResourceType;
import ee.hm.dop.model.TargetGroup;
import ee.hm.dop.model.User;
import ee.hm.dop.model.taxon.Subject;
import ee.hm.dop.model.taxon.Taxon;
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
public class ChangedLearningObjectServiceTest {

    @TestSubject
    private ChangedLearningObjectService changedLearningObjectService = new ChangedLearningObjectService();

    @Mock
    private LearningObjectService learningObjectService;

    @Mock
    private ChangedLearningObjectDAO changedLearningObjectDAO;

    @Mock
    private LearningObjectDAO learningObjectDAO;

    @Test
    public void addChanged() {
        ChangedLearningObject changedLearningObject = new ChangedLearningObject();
        Material material = new Material();
        material.setId(1L);
        ResourceType resourceType = new ResourceType();
        resourceType.setId(3L);
        User user = new User();
        user.setId(1L);

        changedLearningObject.setLearningObject(material);
        changedLearningObject.setResourceType(resourceType);
        changedLearningObject.setChanger(user);

        expect(learningObjectService.get(1L, user)).andReturn(material);
        expect(changedLearningObjectDAO.update(changedLearningObject)).andReturn(changedLearningObject);
        expect(changedLearningObjectDAO.findAll()).andReturn(Collections.singletonList(changedLearningObject));
        replay(learningObjectService);
        replay(changedLearningObjectDAO);

        ChangedLearningObject updated = changedLearningObjectService.addChanged(changedLearningObject);

        assertEquals(changedLearningObject.getId(), updated.getId());
        assertNotNull(updated.getResourceType());
        assertTrue(changedLearningObjectService.findAll().size() == 1);
    }

    @Test
    public void revertAllChanges() {
        ChangedLearningObject change1 = new ChangedLearningObject();
        ChangedLearningObject change2 = new ChangedLearningObject();
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
        change1.setChanger(user);

        change2.setLearningObject(material);
        change2.setTargetGroup(targetGroup);
        change2.setChanger(user);

        expect(learningObjectService.get(1L, user)).andReturn(material);
        expect(changedLearningObjectDAO.getAllByLearningObject(1L)).andReturn(Arrays.asList(change1, change2));
        expect(changedLearningObjectDAO.removeAllByLearningObject(1L)).andReturn(true);
        expect(learningObjectDAO.update(material)).andReturn(material);
        replay(learningObjectService);
        replay(changedLearningObjectDAO);
        replay(learningObjectDAO);

        LearningObject updated = changedLearningObjectService.revertAllChanges(1L, user);

        assertTrue(updated.getTargetGroups().size() == 0);
        assertTrue(((Material) updated).getResourceTypes().size() == 0);
    }

    @Test
    public void materialHasThisTaxon() {
        Material material = new Material();

        ChangedLearningObject change1 = new ChangedLearningObject();
        ChangedLearningObject change2 = new ChangedLearningObject();
        ChangedLearningObject change3 = new ChangedLearningObject();

        Taxon subject = new Subject();
        TargetGroup targetGroup = new TargetGroup();
        ResourceType resourceType = new ResourceType();

        change1.setTaxon(subject);
        change2.setTargetGroup(targetGroup);
        change3.setResourceType(resourceType);

        material.setTaxons(Collections.singletonList(subject));

        assertTrue(changedLearningObjectService.materialHasThis(material, change1));
        assertFalse(changedLearningObjectService.materialHasThis(material, change2));
        assertFalse(changedLearningObjectService.materialHasThis(material, change3));
    }

    @Test
    public void materialHasThisResourceType() {
        Material material = new Material();

        ChangedLearningObject change1 = new ChangedLearningObject();
        ChangedLearningObject change2 = new ChangedLearningObject();
        ChangedLearningObject change3 = new ChangedLearningObject();

        Taxon subject = new Subject();
        TargetGroup targetGroup = new TargetGroup();
        ResourceType resourceType = new ResourceType();

        change1.setTaxon(subject);
        change2.setTargetGroup(targetGroup);
        change3.setResourceType(resourceType);

        material.setResourceTypes(Collections.singletonList(resourceType));

        assertFalse(changedLearningObjectService.materialHasThis(material, change1));
        assertFalse(changedLearningObjectService.materialHasThis(material, change2));
        assertTrue(changedLearningObjectService.materialHasThis(material, change3));
    }

    @Test
    public void materialHasThisTargetGroup() {
        Material material = new Material();

        ChangedLearningObject change1 = new ChangedLearningObject();
        ChangedLearningObject change2 = new ChangedLearningObject();
        ChangedLearningObject change3 = new ChangedLearningObject();

        Taxon subject = new Subject();
        TargetGroup targetGroup = new TargetGroup();
        ResourceType resourceType = new ResourceType();

        change1.setTaxon(subject);
        change2.setTargetGroup(targetGroup);
        change3.setResourceType(resourceType);

        material.setTargetGroups(Collections.singletonList(targetGroup));

        assertFalse(changedLearningObjectService.materialHasThis(material, change1));
        assertTrue(changedLearningObjectService.materialHasThis(material, change2));
        assertFalse(changedLearningObjectService.materialHasThis(material, change3));
    }

    @Test
    public void portfolioHasThisTaxon() {
        Portfolio portfolio = new Portfolio();

        ChangedLearningObject change1 = new ChangedLearningObject();
        ChangedLearningObject change2 = new ChangedLearningObject();

        Taxon subject = new Subject();
        TargetGroup targetGroup = new TargetGroup();

        change1.setTaxon(subject);
        change2.setTargetGroup(targetGroup);

        portfolio.setTargetGroups(Collections.singletonList(targetGroup));

        assertFalse(changedLearningObjectService.portfolioHasThis(portfolio, change1));
        assertTrue(changedLearningObjectService.portfolioHasThis(portfolio, change2));
    }
}
