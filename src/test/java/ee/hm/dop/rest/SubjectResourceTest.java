package ee.hm.dop.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.Subject;

public class SubjectResourceTest extends ResourceIntegrationTestBase {

    @Test
    public void getAllSubjects() {
        Response response = doGet("subject/getAll");

        List<Subject> subjects = response.readEntity(new GenericType<List<Subject>>() {
        });

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
