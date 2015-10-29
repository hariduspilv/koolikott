package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.junit.Test;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.ResourceType;

public class ResourceTypeDAOTest extends DatabaseTestBase {

    @Inject
    private ResourceTypeDAO resourceTypeDAO;

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

}
