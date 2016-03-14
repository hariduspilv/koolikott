package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.google.inject.Inject;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.LearningObject;

/**
 * Created by mart on 10.03.16.
 */
public class LearningObjectDAOTest extends DatabaseTestBase {

    @Inject
    private LearningObjectDAO learningObjectDAO;

    @Test
    public void getNewest() {
        List<LearningObject> learningObjects = learningObjectDAO.findNewestLearningObjects(8, 0);
        assertEquals(8, learningObjects.size());
        validateNewestAreFirst(learningObjects);
    }

    @Test
    public void getNewestFromThird() {
        List<LearningObject> learningObjects = learningObjectDAO.findNewestLearningObjects(8, 3);
        assertEquals(8, learningObjects.size());
        validateNewestAreFirst(learningObjects);
    }

    @Test
    public void getPopularFromZero() {
        List<LearningObject> learningObjects = learningObjectDAO.findPopularLearningObjects(8, 0);
        assertEquals(8, learningObjects.size());
    }

    @Test
    public void getPopularFromThird() {
        List<LearningObject> learningObjects = learningObjectDAO.findPopularLearningObjects(6, 3);
        assertEquals(6, learningObjects.size());
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
