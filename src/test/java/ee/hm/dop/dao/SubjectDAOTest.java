package ee.hm.dop.dao;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import javax.inject.Inject;

import org.junit.Test;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.Subject;

public class SubjectDAOTest extends DatabaseTestBase {

    @Inject
    private SubjectDAO subjectDAO;

    @Test
    public void findAll() {
        List<Subject> subjects = subjectDAO.findAll();
        assertEquals(2, subjects.size());

        // Verify if all required fields are loaded
        for (int i = 0; i < subjects.size(); i++) {
            Subject subject = subjects.get(i);
            assertEquals(Long.valueOf(i + 1), subject.getId());
            assertFalse(isBlank(subject.getName()));
        }

        // Verify the subject names
        assertEquals("Biology", subjects.get(0).getName());
        assertEquals("Mathematics", subjects.get(1).getName());
    }

}
