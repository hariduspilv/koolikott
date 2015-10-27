package ee.hm.dop.dao;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.ResourceType;
import org.junit.Test;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class ResourceTypeDAOTest extends DatabaseTestBase {

    @Inject
    private ResourceTypeDAO resourceTypeDAO;

    @Test
    public void findAll() {
        List<ResourceType> resourceTypes = resourceTypeDAO.findAll();

        assertEquals(7, resourceTypes.size());
        for (int i = 0; i < resourceTypes.size(); i++) {
            assertValidResourceType(resourceTypes.get(i));
        }
    }

    @Test
    public void findResourceTypeByName() {
        Long id = new Long(1001);
        String name = "TEXTBOOK1";

        ResourceType returnedResourceType = resourceTypeDAO.findResourceTypeByName(name);

        assertNotNull(returnedResourceType);
        assertNotNull(returnedResourceType.getId());
        assertEquals(id, returnedResourceType.getId());
        assertEquals(name, returnedResourceType.getName());
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
