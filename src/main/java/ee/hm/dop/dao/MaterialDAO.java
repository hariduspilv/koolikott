package ee.hm.dop.dao;

import java.util.List;

import javax.persistence.TypedQuery;

import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Repository;
import ee.hm.dop.model.User;

public class MaterialDAO extends LearningObjectDAO {

    @Override
    public Material findByIdNotDeleted(long materialId) {
        return castTo(Material.class, super.findByIdNotDeleted(materialId));
    }

    @Override
    public Material findById(long materialId) {
        return castTo(Material.class, super.findById(materialId));
    }

    public List<LearningObject> getDeletedMaterials() {
        List<LearningObject> learningObjects = super.getDeletedLearningObjects();
        removeNot(Material.class, learningObjects);
        return learningObjects;
    }

    /**
     * finds all materials contained in the idList. There is no guarantee about
     * in which order the materials will be in the result list.
     *
     * @param idList
     *            the list with materials id
     * @return a list of materials specified by idList
     */
    @Override
    public List<LearningObject> findAllById(List<Long> idList) {
        List<LearningObject> learningObjects = super.findAllById(idList);
        removeNot(Material.class, learningObjects);
        return learningObjects;
    }

    public List<Material> findNewestMaterials(int numberOfMaterials) {

        return entityManager.createQuery("FROM Material m WHERE m.deleted = false ORDER BY added desc", Material.class)
                .setMaxResults(numberOfMaterials).getResultList();
    }

    public List<Material> findPopularMaterials(int numberOfMaterials) {
        return entityManager.createQuery("FROM Material m WHERE m.deleted = false ORDER BY views DESC", Material.class)
                .setMaxResults(numberOfMaterials).getResultList();
    }

    public byte[] findNotDeletedPictureByMaterial(Material material) {
        TypedQuery<byte[]> findById = entityManager.createQuery(
                "SELECT m.picture FROM Material m WHERE m.id = :id AND m.deleted = false", byte[].class);

        return getBytes(material, findById);
    }

    public byte[] findPictureByMaterial(Material material) {
        TypedQuery<byte[]> findById = entityManager.createQuery("SELECT m.picture FROM Material m WHERE m.id = :id",
                byte[].class);

        return getBytes(material, findById);
    }

    public Material findByRepositoryAndRepositoryIdentifier(Repository repository, String repositoryIdentifier) {
        String select = "SELECT m FROM Material m WHERE m.repository.id = :repositoryId"
                + " AND m.repositoryIdentifier = :repositoryIdentifier AND m.deleted = false";
        TypedQuery<Material> query = entityManager.createQuery(select, Material.class);

        query.setParameter("repositoryId", repository.getId()) //
                .setParameter("repositoryIdentifier", repositoryIdentifier);

        return getSingleResult(query);
    }

    /**
     * Find all materials with the specified creator. Materials are ordered by
     * added date with newest first.
     *
     * @param creator
     *            User who created the materials
     * @return A list of materials
     */
    @Override
    public List<LearningObject> findByCreator(User creator) {
        List<LearningObject> learningObjects = super.findByCreator(creator);
        removeNot(Material.class, learningObjects);
        return learningObjects;
    }
}
