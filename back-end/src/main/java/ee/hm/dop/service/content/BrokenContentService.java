package ee.hm.dop.service.content;

import ee.hm.dop.dao.BrokenContentDao;
import ee.hm.dop.model.BrokenContent;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.User;

import javax.inject.Inject;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public class BrokenContentService {

    @Inject
    private MaterialService materialService;
    @Inject
    private BrokenContentDao brokenContentDao;

    public BrokenContent addBrokenMaterial(Material material, User loggedInUser) {
        Material originalMaterial = materialService.validateAndFindNotDeleted(material);
        BrokenContent brokenContent = new BrokenContent();
        brokenContent.setCreator(loggedInUser);
        brokenContent.setMaterial(originalMaterial);
        return brokenContentDao.update(brokenContent);
    }

    public Boolean hasSetBroken(long materialId, User loggedInUser) {
        return isNotEmpty(brokenContentDao.findByMaterialAndUser(materialId, loggedInUser));
    }
}
