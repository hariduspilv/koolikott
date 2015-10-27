package ee.hm.dop.rest;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.ResourceType;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class ResourceTypeResourceTest extends ResourceIntegrationTestBase {

    @Test
    public void getAllResourceTypes() {
        Response response = doGet("resourceType/getAll");

        List<ResourceType> resourceTypes = response.readEntity(new GenericType<List<ResourceType>>() {
        });

        assertEquals(7, resourceTypes.size());
        for (int i = 0; i < resourceTypes.size(); i++) {
            assertValidResourceType(resourceTypes.get(i));
        }
    }

    private void assertValidResourceType(ResourceType resourceType) {
        Map<Long, String> resourceTypes = new HashMap<>();
        resourceTypes.put(1001L, "TEXTBOOK1");
        resourceTypes.put(1002L, "EXPERIMENT1");
        resourceTypes.put(1003L, "SIMULATION1");
        resourceTypes.put(1004L, "GLOSSARY1");
        resourceTypes.put(1005L, "ROLEPLAY1");
        resourceTypes.put(1006L, "WEBSITE");
        resourceTypes.put(1007L, "COURSE");

        assertNotNull(resourceType.getId());
        assertNotNull(resourceType.getName());
        if (resourceTypes.containsKey(resourceType.getId())) {
            assertEquals(resourceTypes.get(resourceType.getId()), resourceType.getName());
        } else {
            fail("ResourceType with unexpected id.");
        }
    }

}
