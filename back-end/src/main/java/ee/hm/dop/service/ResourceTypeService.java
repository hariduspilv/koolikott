package ee.hm.dop.service;

import ee.hm.dop.dao.ResourceTypeDAO;
import ee.hm.dop.model.ResourceType;

import javax.inject.Inject;
import java.util.List;

public class ResourceTypeService {

    @Inject
    private TranslationService translationService;

    @Inject
    private ResourceTypeDAO resourceTypeDAO;

    public ResourceType getResourceTypeByName(String name) {
        return resourceTypeDAO.findResourceTypeByName(name);
    }

    public List<ResourceType> getAllResourceTypes() {
        return resourceTypeDAO.findAllResourceTypes();
    }

    public List<ResourceType> getUsedResourceTypes() {
        return resourceTypeDAO.findUsedResourceTypes();
    }

    public ResourceType findResourceByTranslation(String name) {
        String translationKey = translationService.getTranslationKeyByTranslation(name);
        if (translationKey == null) {
            return null;
        }

        return resourceTypeDAO.findResourceTypeByName(translationKey);
    }
}
