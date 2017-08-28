package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.google.inject.Inject;
import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.LearningObject;
import org.junit.Test;

/**
 * Created by mart on 10.03.16.
 */
public class LearningObjectDaoTest extends DatabaseTestBase {

    @Inject
    private LearningObjectDao learningObjectDao;

    @Test
    public void getNewest() {
        List<LearningObject> learningObjects = learningObjectDao.findNewestLearningObjects(8, 0);
        assertEquals(8, learningObjects.size());
        validateNewestAreFirst(learningObjects);
    }

    @Test
    public void getNewestFromThird() {
        List<LearningObject> learningObjects = learningObjectDao.findNewestLearningObjects(8, 3);
        assertEquals(8, learningObjects.size());
        validateNewestAreFirst(learningObjects);
    }

    private void validateNewestAreFirst(List<LearningObject> learningObjects) {
        LearningObject last = null;
        for (LearningObject learningObject : learningObjects) {
            if (last != null) {
                // Check that the learningObjects are from newest to oldest
                assertTrue(last.getAdded().isAfter(learningObject.getAdded())
                        || last.getAdded().isEqual(learningObject.getAdded()));
            }

            last = learningObject;
            assertNotNull(learningObject.getAdded());
        }
    }
}
