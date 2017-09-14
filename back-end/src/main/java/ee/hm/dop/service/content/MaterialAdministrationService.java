package ee.hm.dop.service.content;

import ee.hm.dop.dao.BrokenContentDao;
import ee.hm.dop.dao.MaterialDao;
import ee.hm.dop.model.BrokenContent;
import ee.hm.dop.model.Material;

import javax.inject.Inject;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public class MaterialAdministrationService {

    @Inject
    private MaterialDao materialDao;
    @Inject
    private BrokenContentDao brokenContentDao;

    public void setMaterialNotBroken(Material material) {
        if (material == null || material.getId() == null) {
            throw new RuntimeException("Material not found while adding broken material");
        }
        Material originalMaterial = materialDao.findByIdNotDeleted(material.getId());
        if (originalMaterial == null) {
            throw new RuntimeException("Material not found while adding broken material");
        }

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
        return brokenContentDao.getCount();
    }

    public Boolean isBroken(long materialId) {
        return isNotEmpty(brokenContentDao.findByMaterial(materialId));
    }
}
