package ee.hm.dop.service;

import ee.hm.dop.dao.MaterialDAO;
import ee.hm.dop.model.Material;

import java.util.List;

import javax.inject.Inject;

public class MaterialService {

    @Inject
    MaterialDAO materialDao;

    public List<Material> getAllMaterials() {
        return materialDao.findAll();
    }

    public Material get(long materialId) {
        return materialDao.findById(materialId);
    }
}
