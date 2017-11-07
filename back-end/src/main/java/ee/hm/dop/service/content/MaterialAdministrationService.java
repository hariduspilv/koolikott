package ee.hm.dop.service.content;

<<<<<<< HEAD
import ee.hm.dop.dao.BrokenContentDao;
import ee.hm.dop.dao.MaterialDao;
import ee.hm.dop.model.BrokenContent;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Recommendation;
import ee.hm.dop.model.User;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.utils.UserUtil;
import org.joda.time.DateTime;
=======
import ee.hm.dop.dao.MaterialDao;
import ee.hm.dop.model.Material;
>>>>>>> new-develop

import javax.inject.Inject;
import java.util.List;

<<<<<<< HEAD
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

=======
>>>>>>> new-develop
public class MaterialAdministrationService {

    @Inject
    private MaterialDao materialDao;
<<<<<<< HEAD
    @Inject
    private BrokenContentDao brokenContentDao;
    @Inject
    private MaterialService materialService;
    @Inject
    private FirstReviewService firstReviewService;

    public void setMaterialNotBroken(Material material, User loggedInUser) {
        Material originalMaterial = materialService.validateAndFindNotDeleted(material);
        firstReviewService.setReviewed(originalMaterial, loggedInUser);
        brokenContentDao.deleteBrokenMaterials(originalMaterial.getId());
    }
=======
>>>>>>> new-develop

    public List<Material> getDeletedMaterials() {
        return materialDao.findDeletedMaterials();
    }

    public Long getDeletedMaterialsCount() {
        return materialDao.findDeletedMaterialsCount();
    }
<<<<<<< HEAD

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

    public Boolean isBroken(long materialId) {
        return isNotEmpty(brokenContentDao.findByMaterial(materialId));
    }
=======
>>>>>>> new-develop
}
