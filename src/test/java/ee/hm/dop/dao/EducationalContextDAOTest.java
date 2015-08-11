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

        assertEquals(8, subjects.size());
        assertValidEducationalContext (subjects.get(0));
        assertValidEducationalContext (subjects.get(1));
    }

    private void assertValidEducationalContext(EducationalContext educationalContext) {
        assertNotNull(educationalContext.getId());
        assertNotNull(educationalContext.getName());
        if (educationalContext.getId() == 1001) {
            assertEquals("PRESCHOOL", educationalContext.getName());
        } else if (educationalContext.getId() == 1002) {
            assertEquals("COMPULSORYEDUCATION", educationalContext.getName());
        } else {
            fail("Educational context with unexpected id.");
        }
    }
}
