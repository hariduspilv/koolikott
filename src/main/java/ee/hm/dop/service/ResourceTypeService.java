package ee.hm.dop.service;

import java.util.List;

import javax.inject.Inject;

import ee.hm.dop.dao.ResourceTypeDAO;
import ee.hm.dop.model.ResourceType;

public class ResourceTypeService {

    @Inject
    private ResourceTypeDAO resourceTypeDAO;

    public List<ResourceType> getAllResourceTypes() {
        return resourceTypeDAO.findAll();
    }

}
