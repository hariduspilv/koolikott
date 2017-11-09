package ee.hm.dop.service.reviewmanagement;

import com.google.common.collect.Lists;
import ee.hm.dop.dao.LearningObjectDao;
import ee.hm.dop.dao.ReviewableChangeDao;
import ee.hm.dop.dao.TranslationDAO;
import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.Role;
import ee.hm.dop.service.content.LearningObjectService;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

import static junit.framework.TestCase.assertTrue;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

@Ignore
@RunWith(EasyMockRunner.class)
public class ReviewableChangeAdminServiceTest {

    @TestSubject
    private ReviewableChangeAdminService reviewableChangeAdminService = new ReviewableChangeAdminService();
    @Mock
    private ReviewableChangeDao reviewableChangeDao;
    @Mock
    private ReviewManager reviewManager;
    @Mock
    private LearningObjectService learningObjectService;
    @Mock
    private LearningObjectDao learningObjectDao;
    @Mock
    private TranslationDAO translationDAO;

    @Test
    public void revertAllChanges() {
        ReviewableChange change1 = new ReviewableChange();
        ReviewableChange change2 = new ReviewableChange();
        Material material = new Material();
        material.setReviewableChanges(new ArrayList<>());
        ResourceType resourceType = new ResourceType();
        TargetGroup targetGroup = new TargetGroup();
        User user = new User();

        material.setId(1L);
        resourceType.setId(3L);
        resourceType.setName("resource");
        user.setId(1L);
        user.setRole(Role.ADMIN);
        targetGroup.setId(5L);
        targetGroup.setName("ZERO_FIVE");
        material.setTargetGroups(new LinkedList<>(Collections.singletonList(targetGroup)));
        material.setResourceTypes(new LinkedList<>(Collections.singletonList(resourceType)));

        change1.setLearningObject(material);
        change1.setResourceType(resourceType);
        change1.setCreatedBy(user);
        material.getReviewableChanges().add(change1);

        change2.setLearningObject(material);
        change2.setTargetGroup(targetGroup);
        change2.setCreatedBy(user);
        material.getReviewableChanges().add(change2);

        expect(learningObjectService.get(1L, user)).andReturn(material);
        expect(reviewableChangeDao.getAllByLearningObject(1L)).andReturn(Arrays.asList(change1, change2));
        expect(learningObjectDao.createOrUpdate(material)).andReturn(material);
        expect(translationDAO.getTranslationsForKey(Lists.newArrayList("resource"))).andReturn(null);
        expect(translationDAO.getTranslationsForKey(Lists.newArrayList("ZERO_FIVE"))).andReturn(null);
        replay(learningObjectService);
        replay(reviewableChangeDao);
        replay(learningObjectDao);

        LearningObject updated = reviewableChangeAdminService.revertAllChanges(1L, user);

        assertTrue(updated.getTargetGroups().size() == 0);
        assertTrue(((Material) updated).getResourceTypes().size() == 0);
    }

}
