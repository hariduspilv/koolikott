package ee.hm.dop.service.content;

import ee.hm.dop.dao.MaterialDao;
import ee.hm.dop.model.Material;

import javax.inject.Inject;
import java.util.List;

public class MaterialAdministrationService {

    @Inject
    private MaterialDao materialDao;

    public List<Material> getDeletedMaterials() {
        return materialDao.findDeletedMaterials();
    }

    public Long getDeletedMaterialsCount() {
        return materialDao.findDeletedMaterialsCount();
    }
}
