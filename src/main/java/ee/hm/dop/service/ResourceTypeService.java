package ee.hm.dop.service;

import ee.hm.dop.dao.ResourceTypeDAO;
import ee.hm.dop.model.ResourceType;

import javax.inject.Inject;
import java.util.List;

public class ResourceTypeService {

    @Inject
    private ResourceTypeDAO resourceTypeDAO;

    public List<ResourceType> getAllResourceTypes() {
        return resourceTypeDAO.findAll();
    }

    public ResourceType getResourceTypeByName(String name) {
        return resourceTypeDAO.findResourceTypeByName(name);
    }
}
