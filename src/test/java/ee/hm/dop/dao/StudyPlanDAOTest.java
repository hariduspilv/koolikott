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
        assertEquals(new Long(2), studyPlan.getSubject().getId());
    }

    @Test
    public void findByIdWhenStudyPlanDoesNotExist() {
        StudyPlan studyPlan = studyPlanDAO.findById(100000);
        assertNull(studyPlan);
    }

    @Test
    public void findByIdNullSubject() {
        Long id = new Long(2);
        StudyPlan studyPlan = studyPlanDAO.findById(id);

        assertNotNull(studyPlan);
        assertEquals(id, studyPlan.getId());
        assertEquals("New ways how to do it", studyPlan.getTitle());
        assertNull(studyPlan.getSubject());
    }
}
