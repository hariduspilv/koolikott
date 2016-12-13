package ee.hm.dop.dao;

import ee.hm.dop.model.Language;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Repository;
import ee.hm.dop.model.User;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.List;

public class MaterialDAO extends LearningObjectDAO {
    @Inject
    private EntityManager entityManager;

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
     * @param idList the list with materials id
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
                + " AND m.repositoryIdentifier = :repositoryIdentifier";
        TypedQuery<Material> query = createQuery(select, Material.class);

        query.setParameter("repositoryId", repository.getId()) //
                .setParameter("repositoryIdentifier", repositoryIdentifier);

        return getSingleResult(query, Material.class);
    }

    /**
     * Find all materials with the specified creator. Materials are ordered by
     * added date with newest first.
     *
     * @param creator User who created the materials
     * @return A list of materials
     */
    @Override
    public List<LearningObject> findByCreator(User creator, int start, int maxResults) {
        List<LearningObject> learningObjects = super.findByCreator(creator, start, maxResults);
        removeNot(Material.class, learningObjects);
        return learningObjects;
    }

    public List<Material> findBySource(String materialSource, boolean deleted) {
        String queryStart = deleted ? "FROM Material m WHERE " : "FROM Material m WHERE m.deleted = false AND ";

        return createQuery(queryStart +
                        "m.source='http://www." + materialSource + "' " +
                        "OR m.source ='https://www." + materialSource + "' " +
                        "OR m.source='http://" + materialSource + "' " +
                        "OR m.source='https://" + materialSource + "'",
                Material.class).getResultList();
    }

    public Material findOneBySource(String materialSource, boolean deleted) {
        String queryStart = deleted ? "FROM Material m WHERE " : "FROM Material m WHERE m.deleted = false AND ";
        try {
            return createQuery(queryStart +
                            "m.source='http://www." + materialSource + "' " +
                            "OR m.source ='https://www." + materialSource + "' " +
                            "OR m.source='http://" + materialSource + "' " +
                            "OR m.source='https://" + materialSource + "'",
                    Material.class).setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Language> findLanguagesUsedInMaterials() {
        return createQuery("SELECT DISTINCT m.language FROM Material m WHERE m.deleted = false", Language.class)
                .getResultList();
    }

    public long findByCreatorSize(User creator) {
        String queryString = "SELECT Count(lo.id) FROM LearningObject lo INNER JOIN Material m ON lo.id=m.id WHERE lo.creator = :creator AND lo.deleted = FALSE";
        Query query = entityManager.createNativeQuery(queryString);
        return ((BigInteger) query.setParameter("creator", creator).getSingleResult()).longValue();
    }
}
