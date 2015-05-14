package ee.hm.dop.dao;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import ee.hm.dop.model.Material;

public class MaterialDAO {
	
	protected EntityManager entityManager;
	  
	@Inject
	public MaterialDAO(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	public List<Material> getAllMaterials() {
		entityManager.getTransaction().begin();
		List<Material> resultList = entityManager.createQuery("from Material", Material.class).getResultList();
		entityManager.getTransaction().commit();
		return resultList;
	}
}
