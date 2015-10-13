package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import javax.inject.Inject;

import org.junit.Test;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.EducationalContext;

/**
 * Created by mart.laus on 6.08.2015.
 */
public class EducationalContextDAOTest extends DatabaseTestBase {

    @Inject
    private EducationalContextDAO educationalContextDAO;

    @Test
    public void findAll() {
        List<EducationalContext> subjects = educationalContextDAO.findAll();

        assertEquals(9, subjects.size());
        assertValidEducationalContext(subjects.get(0));
        assertValidEducationalContext(subjects.get(1));
        assertValidEducationalContext(subjects.get(2));
        assertValidEducationalContext(subjects.get(3));
        assertValidEducationalContext(subjects.get(4));
        assertValidEducationalContext(subjects.get(5));
        assertValidEducationalContext(subjects.get(6));
        assertValidEducationalContext(subjects.get(7));
        assertValidEducationalContext(subjects.get(8));
    }

    private void assertValidEducationalContext(EducationalContext educationalContext) {
        assertNotNull(educationalContext.getId());
        assertNotNull(educationalContext.getName());

        if (educationalContext.getId() == 1) {
            assertEquals("PRESCHOOLEDUCATION", educationalContext.getName());
        } else if (educationalContext.getId() == 2) {
            assertEquals("BASICEDUCATION", educationalContext.getName());
        } else if (educationalContext.getId() == 3) {
            assertEquals("SECONDARYEDUCATION", educationalContext.getName());
        } else if (educationalContext.getId() == 4) {
            assertEquals("HIGHEREDUCATION", educationalContext.getName());
        } else if (educationalContext.getId() == 5) {
            assertEquals("VOCATIONALEDUCATION", educationalContext.getName());
        } else if (educationalContext.getId() == 6) {
            assertEquals("CONTINUINGEDUCATION", educationalContext.getName());
        } else if (educationalContext.getId() == 7) {
            assertEquals("TEACHEREDUCATION", educationalContext.getName());
        } else if (educationalContext.getId() == 8) {
            assertEquals("SPECIALEDUCATION", educationalContext.getName());
        } else if (educationalContext.getId() == 9) {
            assertEquals("OTHER", educationalContext.getName());
        } else {
            fail("Educational context with unexpected id.");
        }
    }
}
