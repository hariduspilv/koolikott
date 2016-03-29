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

    public List<LearningObject> findDeletedMaterials() {
        List<LearningObject> learningObjects = super.findDeletedLearningObjects();
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

    public Material findByRepositoryAndRepositoryIdentifier(Repository repository, String repositoryIdentifier) {
        String select = "SELECT m FROM Material m WHERE m.repository.id = :repositoryId"
                + " AND m.repositoryIdentifier = :repositoryIdentifier AND m.deleted = false";
        TypedQuery<Material> query = createQuery(select, Material.class);

        query.setParameter("repositoryId", repository.getId()) //
                .setParameter("repositoryIdentifier", repositoryIdentifier);

        return getSingleResult(query, Material.class);
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

    public List<Material> findBySource(String materialSource) {
        return createQuery("FROM Material m WHERE m.deleted = false AND m.source = :source", Material.class)
                .setParameter("source", materialSource).getResultList();
    }
}
