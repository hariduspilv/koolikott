package ee.hm.dop.dao;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import ee.hm.dop.model.Material;

public class MaterialDAO {

    @Inject
    private EntityManager entityManager;

    public List<Material> findAll() {
        return entityManager.createQuery("from Material", Material.class).getResultList();
    }

    public Material findById(long materialId) {
        TypedQuery<Material> findByCode = entityManager.createNamedQuery("Material.findById", Material.class);

        Material material = null;
        try {
            material = findByCode.setParameter("id", materialId).getSingleResult();
        } catch (NoResultException ex) {
            // ignore
        }

        return material;
    }
}
