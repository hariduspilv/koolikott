package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.inject.Inject;
import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.LearningObject;
import org.junit.Test;

/**
 * Created by mart on 10.03.16.
 */
public class LearningObjectDAOTest extends DatabaseTestBase {

    @Inject
    private LearningObjectDAO learningObjectDAO;

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
}
