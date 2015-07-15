package ee.hm.dop.service;

import java.util.List;

import javax.inject.Inject;

import ee.hm.dop.dao.MaterialDAO;
import ee.hm.dop.model.Material;

public class MaterialService {

    @Inject private MaterialDAO materialDao;

    public List<Material> getAllMaterials() {
        return materialDao.findAll();
    }

    public Material get(long materialId) {
        return materialDao.findById(materialId);
    }

    public List<Material> getNewestMaterials(int numberOfMaterials) {
        return materialDao.findNewestMaterials(numberOfMaterials);
    }

    public boolean increaseViews(long materialId) {
        return materialDao.increaseViews(materialId);
    }
}
