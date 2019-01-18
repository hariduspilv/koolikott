package ee.hm.dop.service.metadata;

import ee.hm.dop.dao.ResourceTypeDao;
import ee.hm.dop.model.ResourceType;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class ResourceTypeService {

    @Inject
    private TranslationService translationService;
    @Inject
    private ResourceTypeDao resourceTypeDao;

    public ResourceType getResourceTypeByName(String name) {
        return resourceTypeDao.findByName(name);
    }

    public List<ResourceType> getResourceTypeByName(List<String> name) {
        return resourceTypeDao.findByName(name);
    }

    public List<ResourceType> getAllResourceTypes() {
        return resourceTypeDao.findAll();
    }

    public List<ResourceType> getUsedResourceTypes() {
        return resourceTypeDao.findUsedResourceTypes();
    }

    public ResourceType findResourceByTranslation(String name) {
        String translationKey = translationService.getTranslationKeyByTranslation(name);
        return translationKey != null ? resourceTypeDao.findByName(translationKey) : null;
    }
}
