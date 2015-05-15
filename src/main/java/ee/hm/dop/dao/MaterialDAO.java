package ee.hm.dop.dao;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import ee.hm.dop.model.Material;

public class MaterialDAO {

    @Inject
    private EntityManager entityManager;

    public List<Material> findAll() {
        return entityManager.createQuery("from Material", Material.class).getResultList();
    }
}
