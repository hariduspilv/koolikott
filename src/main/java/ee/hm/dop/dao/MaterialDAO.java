package ee.hm.dop.dao;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.slf4j.LoggerFactory;

import ee.hm.dop.model.Material;
import ee.hm.dop.oaipmh.IdentifierIterator;

public class MaterialDAO {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(IdentifierIterator.class);
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

    /**
     * finds all materials contained in the idList. There is no guarantee about
     * in which order the materials will be in the result list.
     *
     * @param idList the list with materials id
     * @return a list of materials specified by idList
     */
    public List<Material> findAllById(List<Long> idList) {
        TypedQuery<Material> findAllByIdList = entityManager.createNamedQuery("Material.findAllById", Material.class);
        return findAllByIdList.setParameter("idList", idList).getResultList();
    }

    public List<Material> findNewestMaterials(int numberOfMaterials) {

        return entityManager.createQuery("from Material order by added desc", Material.class)
                .setMaxResults(numberOfMaterials).getResultList();
    }

    public void update(Material material) {
        entityManager.persist(material);
    }

    public void persistMaterial(Material material) {
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();

        }
        try {
            entityManager.persist(material);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            logger.error("Could not persist a material.");
        }

    }
}
