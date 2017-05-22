package ee.hm.dop.dao;

import java.math.BigInteger;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import ee.hm.dop.model.Language;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Repository;
import ee.hm.dop.model.User;

public class MaterialDAO extends LearningObjectDAO {
    public static final String HTTP = "http://";
    public static final String HTTP_WWW = HTTP + "www.";
    public static final String HTTPS = "https://";
    public static final String HTTPS_WWW = HTTPS + "www.";
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

    public List<Material> findDeletedMaterials() {
        TypedQuery<Material> query = createQuery("SELECT m FROM Material m WHERE m.deleted = true", Material.class);
        return query.getResultList();
    }

    public Long findDeletedMaterialsCount() {
        Query query = getEntityManager().createQuery("SELECT count(*) FROM Material m WHERE m.deleted = true");
        return (Long) query.getSingleResult();
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
        TypedQuery<Material> materialTypedQuery = getMaterialTypedQuery(materialSource, deleted);
        return materialTypedQuery.getResultList();
    }

    public Material findOneBySource(String materialSource, boolean deleted) {
        TypedQuery<Material> materialTypedQuery = getMaterialTypedQuery(materialSource, deleted);
        try {
            return materialTypedQuery
              .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private TypedQuery<Material> getMaterialTypedQuery(String materialSource, boolean deleted) {
        String ms1 = HTTP_WWW + materialSource;
        String ms2 = HTTPS_WWW + materialSource;
        String ms3 = HTTP + materialSource;
        String ms4 = HTTPS + materialSource;

        String query = "FROM Material m " +
          "WHERE (m.deleted = false OR m.deleted = :deleted) " +
          "AND m.source = :ms OR m.source = :ms1 OR m.source = :ms2 OR m.source = :ms3 OR m.source = :ms4";

        return createQuery(query, Material.class)
          .setParameter("ms", materialSource)
          .setParameter("ms1", ms1)
          .setParameter("ms2", ms2)
          .setParameter("ms3", ms3)
          .setParameter("ms4", ms4)
          .setParameter("deleted", deleted);
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

    public List<Material> findNewestMaterials(int numberOfMaterials, int startPosition) {
        return createQuery("FROM Material mat WHERE mat.deleted = false ORDER BY added DESC, id DESC",
                Material.class).setFirstResult(startPosition).setMaxResults(numberOfMaterials)
                .getResultList();
    }
}
