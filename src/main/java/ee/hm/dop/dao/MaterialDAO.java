package ee.hm.dop.dao;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import ee.hm.dop.model.Material;

public class MaterialDAO {

    @Inject
    private EntityManager entityManager;

    public List<Material> findAll() {
        return entityManager.createQuery("from Material", Material.class).getResultList();
    }

    public Material find(long materialId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Material> criteriaQuery = criteriaBuilder.createQuery(Material.class);
        Root<Material> fromMaterial = criteriaQuery.from(Material.class);

        criteriaQuery.select(fromMaterial).where(criteriaBuilder.equal(fromMaterial.get("id"), materialId));

        TypedQuery<Material> query = entityManager.createQuery(criteriaQuery);

        Material Material = null;
        try {
            Material = query.getSingleResult();
        } catch (NoResultException ex) {
            // ignore
        }

        return Material;
    }

}
