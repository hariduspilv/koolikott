package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.ResourceType;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

public class ResourceTypeDaoTest extends DatabaseTestBase {

    @Inject
    private ResourceTypeDao resourceTypeDao;

    @Inject
    private MaterialDAO materialDAO;

    @Test
    public void findResourceTypeByName() {
        Long id = new Long(1001);
        String name = "TEXTBOOK1";

        ResourceType returnedResourceType = resourceTypeDao.findByName(name);

        assertNotNull(returnedResourceType);
        assertNotNull(returnedResourceType.getId());
        assertEquals(id, returnedResourceType.getId());
        assertEquals(name, returnedResourceType.getName());
    }

    @Test
    public void getUsedResourceTypes() {
        Material material = new Material();
        ResourceType resourceType = new ResourceType();

        material.setSource("asd");
        material.setAdded(new DateTime());
        material.setViews((long) 123);

        resourceType.setId(1007L);
        resourceType.setName("COURSE");
        material.setResourceTypes(Collections.singletonList(resourceType));

        List<ResourceType> before = resourceTypeDao.findUsedResourceTypes();
        assertEquals(5, before.size());

        LearningObject newMaterial = materialDAO.update(material);

        List<ResourceType> result = resourceTypeDao.findUsedResourceTypes();
        assertEquals(6, result.size());

        materialDAO.remove(newMaterial);

        List<ResourceType> finalList = resourceTypeDao.findUsedResourceTypes();
        assertEquals(5, finalList.size());
    }

}
