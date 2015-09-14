package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.inject.Inject;

import org.junit.Test;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.StudyPlan;

public class StudyPlanDAOTest extends DatabaseTestBase {

    @Inject
    private StudyPlanDAO studyPlanDAO;

    @Test
    public void findById() {
        StudyPlan studyPlan = studyPlanDAO.findById(1);

        assertNotNull(studyPlan);
        assertEquals(new Long(1), studyPlan.getId());
        assertEquals("The new stock market", studyPlan.getTitle());
    }

    @Test
    public void findByIdWhenStudyPlanDoesNotExist() {
        StudyPlan studyPlan = studyPlanDAO.findById(100000);
        assertNull(studyPlan);
    }
}
