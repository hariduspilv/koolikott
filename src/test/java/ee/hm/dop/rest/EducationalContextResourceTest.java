package ee.hm.dop.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.EducationalContext;

/**
 * Created by mart.laus on 6.08.2015.
 */
public class EducationalContextResourceTest extends ResourceIntegrationTestBase {

    @Test
    public void getAllEducationalContexts() {
        Response response = doGet("educationalContext/getAll");

        List<EducationalContext> educationalContexts = response.readEntity(new GenericType<List<EducationalContext>>() {
        });

        assertEquals(9, educationalContexts.size());
        assertValidEducationalContext(educationalContexts.get(0));
        assertValidEducationalContext(educationalContexts.get(1));

    }

    private void assertValidEducationalContext(EducationalContext educationalContext) {
        assertNotNull(educationalContext.getId());
        assertNotNull(educationalContext.getName());
        if (educationalContext.getId() == 1) {
            assertEquals("PRESCHOOLEDUCATION", educationalContext.getName());
        } else if (educationalContext.getId() == 2) {
            assertEquals("BASICEDUCATION", educationalContext.getName());
        } else {
            fail("Subject with unexpected id.");
        }
    }
}
