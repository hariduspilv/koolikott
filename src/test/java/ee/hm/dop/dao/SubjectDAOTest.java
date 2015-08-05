package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

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
        assertValidSubject(subjects.get(0));
        assertValidSubject(subjects.get(1));
    }

    private void assertValidSubject(Subject subject) {
        assertNotNull(subject.getId());
        assertNotNull(subject.getName());
        if (subject.getId() == 1) {
            assertEquals("Biology", subject.getName());
        } else if (subject.getId() == 2) {
            assertEquals("Mathematics", subject.getName());
        } else {
            fail("Subject with unexpected id.");
        }
    }

}
