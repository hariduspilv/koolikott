package ee.hm.dop.service;

import java.util.List;

import javax.inject.Inject;

import ee.hm.dop.dao.MaterialDAO;
import ee.hm.dop.model.Material;

public class MaterialService {

	@Inject
	MaterialDAO materialDao;
	
    public List<Material> getAllMaterials() {
    	return materialDao.findAll();
    }
}
