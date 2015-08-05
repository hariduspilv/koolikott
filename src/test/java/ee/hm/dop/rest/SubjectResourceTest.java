package ee.hm.dop.rest;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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

        // Verify if all fields are loaded
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
