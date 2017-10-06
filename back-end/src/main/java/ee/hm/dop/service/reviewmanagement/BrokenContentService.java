package ee.hm.dop.service.reviewmanagement;

import ee.hm.dop.dao.BrokenContentDao;
import ee.hm.dop.model.BrokenContent;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.User;
import ee.hm.dop.service.content.MaterialService;
import ee.hm.dop.utils.UserUtil;

import javax.inject.Inject;

import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public class BrokenContentService {

    @Inject
    private MaterialService materialService;
    @Inject
    private BrokenContentDao brokenContentDao;

    public BrokenContent save(Material material, User loggedInUser) {
        Material originalMaterial = materialService.validateAndFindNotDeleted(material);
        BrokenContent brokenContent = new BrokenContent();
        brokenContent.setCreator(loggedInUser);
        brokenContent.setMaterial(originalMaterial);
        return brokenContentDao.update(brokenContent);
    }

    public Boolean hasSetBroken(long materialId, User loggedInUser) {
        return isNotEmpty(brokenContentDao.findByMaterialAndUser(materialId, loggedInUser));
    }

    public Boolean isBroken(long materialId) {
        return isNotEmpty(brokenContentDao.findByMaterial(materialId));
    }

    public void setMaterialNotBroken(Material material) {
        brokenContentDao.deleteBrokenMaterials(material.getId());
    }

    public List<BrokenContent> getBrokenMaterials(User user) {
        UserUtil.mustBeModeratorOrAdmin(user);
        if (UserUtil.isAdmin(user)) {
            return brokenContentDao.getBrokenMaterials();
        } else {
            return brokenContentDao.getBrokenMaterials(user);
        }
    }

    public Long getBrokenMaterialCount(User user) {
        UserUtil.mustBeModeratorOrAdmin(user);
        if (UserUtil.isAdmin(user)) {
            return brokenContentDao.getBrokenCount();
        } else {
            return brokenContentDao.getBrokenCount(user);
        }
    }
}
