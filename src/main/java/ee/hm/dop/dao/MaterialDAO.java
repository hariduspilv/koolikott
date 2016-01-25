package ee.hm.dop.dao;

import java.security.InvalidParameterException;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.joda.time.DateTime;

import ee.hm.dop.model.Material;
import ee.hm.dop.model.Repository;
import ee.hm.dop.model.User;

public class MaterialDAO {

    @Inject
    private EntityManager entityManager;

    public Material findById(long materialId) {
        return findById(materialId, false);
    }

    public Material findDeletedById(long materialId) {
        return findById(materialId, true);
    }

    private Material findById(long materialId, boolean includeDeleted) {
        TypedQuery<Material> findByCode = entityManager.createQuery(
                "SELECT m FROM Material m WHERE m.id = :id AND m.deleted = :includeDeleted", Material.class);

        Material material = null;
        try {
            material = findByCode.setParameter("id", materialId).setParameter("includeDeleted", includeDeleted)
                    .getSingleResult();
        } catch (NoResultException ex) {
            // ignore
        }

        return material;
    }

    public List<Material> getDeletedMaterials() {
        TypedQuery<Material> query = entityManager.createQuery("SELECT m FROM Material m WHERE m.deleted = true",
                Material.class);
        return query.getResultList();
    }

    /**
     * finds all materials contained in the idList. There is no guarantee about
     * in which order the materials will be in the result list.
     *
     * @param idList
     *            the list with materials id
     * @return a list of materials specified by idList
     */
    public List<Material> findAllById(List<Long> idList) {
        TypedQuery<Material> findAllByIdList = entityManager
                .createQuery("SELECT m FROM Material m WHERE m.deleted = false AND m.id in :idList", Material.class);
        return findAllByIdList.setParameter("idList", idList).getResultList();
    }

    public List<Material> findNewestMaterials(int numberOfMaterials) {

        return entityManager.createQuery("FROM Material m WHERE m.deleted = false ORDER BY added desc", Material.class)
                .setMaxResults(numberOfMaterials).getResultList();
    }

    public List<Material> findPopularMaterials(int numberOfMaterials) {
        return entityManager.createQuery("FROM Material m WHERE m.deleted = false ORDER BY views DESC", Material.class)
                .setMaxResults(numberOfMaterials).getResultList();
    }

    public Material update(Material material) {
        if (material.getId() != null) {
            material.setUpdated(DateTime.now());
        } else {
            material.setAdded(DateTime.now());
        }

        Material merged = entityManager.merge(material);
        entityManager.persist(merged);
        return merged;
    }

    public void delete(Material material) {
        setDeleted(material, true);
    }

    public void restore(Material material) {
        setDeleted(material, false);
    }

    private void setDeleted(Material material, boolean deleted) {
        if (material.getId() == null) {
            throw new InvalidParameterException("Material does not exist.");
        }

        material.setDeleted(deleted);
        update(material);
    }

    /**
     * For testing purposes.
     *
     * @param material
     */
    protected void remove(Material material) {
        entityManager.remove(material);
    }

    public byte[] findPictureByMaterial(Material material) {
        TypedQuery<byte[]> findById = entityManager
                .createQuery("SELECT m.picture FROM Material m WHERE m.id = :id AND m.deleted = false", byte[].class);

        byte[] picture = null;
        try {
            picture = findById.setParameter("id", material.getId()).getSingleResult();
        } catch (NoResultException ex) {
            // ignore
        }

        return picture;
    }

    public Material findByRepositoryAndRepositoryIdentifier(Repository repository, String repositoryIdentifier) {
        String select = "SELECT m FROM Material m WHERE m.repository.id = :repositoryId"
                + " AND m.repositoryIdentifier = :repositoryIdentifier AND m.deleted = false";
        TypedQuery<Material> query = entityManager.createQuery(select, Material.class);

        query.setParameter("repositoryId", repository.getId()) //
                .setParameter("repositoryIdentifier", repositoryIdentifier);

        return getSingleResult(query);
    }

    private <T> T getSingleResult(TypedQuery<T> query) {
        T singleResult = null;

        try {
            singleResult = query.getSingleResult();
        } catch (NoResultException ex) {
            // ignore
        }

        return singleResult;
    }

    /**
     * Find all materials with the specified creator. Materials are ordered by
     * added date with newest first.
     *
     * @param creator
     *            User who created the materials
     * @return A list of materials
     */
    public List<Material> findByCreator(User creator) {
        String query = "SELECT m FROM Material m WHERE m.creator.id = :creatorId AND m.deleted = false order by added desc";
        TypedQuery<Material> findAllByCreator = entityManager.createQuery(query, Material.class);
        return findAllByCreator.setParameter("creatorId", creator.getId()).getResultList();
    }
}
