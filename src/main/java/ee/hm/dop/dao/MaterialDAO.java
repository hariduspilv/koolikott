package ee.hm.dop.dao;

import ee.hm.dop.model.Material;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

public class MaterialDAO {

    @Inject
    private EntityManager entityManager;

    public List<Material> findAll() {
        return entityManager.createQuery("from Material", Material.class).getResultList();
    }

    public Material find(long materialId) {
        return entityManager.find(Material.class, materialId);
    }

}
