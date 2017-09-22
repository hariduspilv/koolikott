package ee.hm.dop.service.content;

import ee.hm.dop.dao.BrokenContentDao;
import ee.hm.dop.dao.MaterialDao;
import ee.hm.dop.model.BrokenContent;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Recommendation;
import ee.hm.dop.model.User;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.utils.UserUtil;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public class MaterialAdministrationService {

    @Inject
    private MaterialDao materialDao;
    @Inject
    private BrokenContentDao brokenContentDao;
    @Inject
    private MaterialService materialService;
    @Inject
    private SolrEngineService solrEngineService;

    public Recommendation addRecommendation(Material material, User loggedInUser) {
        UserUtil.mustBeAdmin(loggedInUser);

        Material originalMaterial = materialService.validateAndFindNotDeleted(material);

        Recommendation recommendation = new Recommendation();
        recommendation.setCreator(loggedInUser);
        recommendation.setAdded(DateTime.now());
        originalMaterial.setRecommendation(recommendation);

        originalMaterial = materialDao.createOrUpdate(originalMaterial);

        solrEngineService.updateIndex();

        return originalMaterial.getRecommendation();
    }

    public void removeRecommendation(Material material, User loggedInUser) {
        UserUtil.mustBeAdmin(loggedInUser);

        Material originalMaterial = materialService.validateAndFindNotDeleted(material);
        originalMaterial.setRecommendation(null);
        materialDao.createOrUpdate(originalMaterial);
        solrEngineService.updateIndex();
    }

    public void setMaterialNotBroken(Material material) {
        Material originalMaterial = materialService.validateAndFindNotDeleted(material);
        brokenContentDao.deleteBrokenMaterials(originalMaterial.getId());
    }

    public List<Material> getDeletedMaterials() {
        return materialDao.findDeletedMaterials();
    }

    public Long getDeletedMaterialsCount() {
        return materialDao.findDeletedMaterialsCount();
    }

    public List<BrokenContent> getBrokenMaterials() {
        return brokenContentDao.getBrokenMaterials();
    }

    public Long getBrokenMaterialCount() {
        return brokenContentDao.getBrokenCount();
    }

    public Boolean isBroken(long materialId) {
        return isNotEmpty(brokenContentDao.findByMaterial(materialId));
    }
}
