package ee.hm.dop.service;

import ee.hm.dop.dao.MaterialDAO;
import ee.hm.dop.model.Material;

import javax.inject.Inject;
import java.util.List;

public class MaterialService {

	@Inject
	MaterialDAO materialDao;
	
    public List<Material> getAllMaterials() {
    	return materialDao.findAll();
    }

	public Material find(long materialId) {
		return materialDao.find(materialId);
	}
}
