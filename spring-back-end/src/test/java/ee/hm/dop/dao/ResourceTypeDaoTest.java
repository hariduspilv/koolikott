package ee.hm.dop.dao;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.ResourceType;
import ee.hm.dop.model.enums.Visibility;
import java.time.LocalDateTime;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ResourceTypeDaoTest extends DatabaseTestBase {

    @Inject
    private ResourceTypeDao resourceTypeDao;

    @Inject
    private MaterialDao materialDao;

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
        material.setAdded(LocalDateTime.now());
        material.setViews((long) 123);
        material.setVisibility(Visibility.PUBLIC);

        resourceType.setId(1007L);
        resourceType.setName("COURSE");
        material.setResourceTypes(Collections.singletonList(resourceType));

        List<ResourceType> before = resourceTypeDao.findUsedResourceTypes();
        assertEquals(5, before.size());

        Material newMaterial = materialDao.createOrUpdate(material);

        List<ResourceType> result = resourceTypeDao.findUsedResourceTypes();
        assertEquals(6, result.size());

        materialDao.remove(newMaterial);

        List<ResourceType> finalList = resourceTypeDao.findUsedResourceTypes();
        assertEquals(5, finalList.size());
    }

}
