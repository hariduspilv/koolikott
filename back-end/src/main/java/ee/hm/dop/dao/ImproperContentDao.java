package ee.hm.dop.dao;

import ee.hm.dop.model.AdminLearningObject;
import ee.hm.dop.model.ImproperContent;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.User;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

public class ImproperContentDao extends AbstractDao<ImproperContent> {

    @Inject
    private TaxonDao taxonDao;
    @Inject
    private AdminLearningObjectDao adminLearningObjectDao;

    public ImproperContent findByLearningObjectAndCreator(LearningObject learningObject, User creator) {
        return findByField("learningObject", learningObject, "createdBy", creator, "reviewed", false);
    }

    public List<AdminLearningObject> findAllUnreviewed() {
        List<BigInteger> resultList = getEntityManager()
                .createNativeQuery("SELECT\n" +
                        "  lo.id\n" +
                        "FROM LearningObject lo\n" +
                        "  JOIN ImproperContent r ON r.learningObject = lo.id\n" +
                        "WHERE r.reviewed = 0\n" +
                        "      AND lo.deleted = 0\n" +
                        "      AND (lo.visibility = 'PUBLIC' OR lo.visibility = 'NOT_LISTED')\n" +
                        "GROUP BY lo.id\n" +
                        "ORDER BY min(r.createdAt) ASC")
                .setMaxResults(200)
                .getResultList();
        List<Long> collect = resultList.stream().map(BigInteger::longValue).collect(Collectors.toList());
        return adminLearningObjectDao.findById(collect);
    }

    public List<AdminLearningObject> findAllUnreviewed(User user) {
        List<BigInteger> resultList = getEntityManager()
                .createNativeQuery("SELECT\n" +
                        "  lo.id\n" +
                        "FROM LearningObject lo\n" +
                        "  JOIN ImproperContent r ON r.learningObject = lo.id\n" +
                        "  JOIN LearningObject_Taxon lt ON lt.learningObject = lo.id\n" +
                        "WHERE r.reviewed = 0\n" +
                        "      AND lo.deleted = 0\n" +
                        "      AND (lo.visibility = 'PUBLIC' OR lo.visibility = 'NOT_LISTED')\n" +
                        "      AND lt.taxon IN (:taxonIds)\n" +
                        "GROUP BY lo.id\n" +
                        "ORDER BY min(r.createdAt) ASC")
                .setParameter("taxonIds", taxonDao.getUserTaxonsWithChildren(user))
                .setMaxResults(200)
                .getResultList();
        List<Long> collect = resultList.stream().map(BigInteger::longValue).collect(Collectors.toList());
        return adminLearningObjectDao.findById(collect);
    }

    public long findCountOfUnreviewed() {
        return ((BigInteger) getEntityManager()
                .createNativeQuery("SELECT count(DISTINCT lo.id) AS c\n" +
                        "FROM ImproperContent f\n" +
                        "   JOIN LearningObject lo ON f.learningObject = lo.id\n" +
                        "WHERE f.reviewed = 0\n" +
                        "   AND lo.deleted = 0\n" +
                        "   AND (lo.visibility = 'PUBLIC' OR lo.visibility = 'NOT_LISTED')\n")
                .getSingleResult()).longValue();
    }

    public long findCountOfUnreviewed(User user) {
        return ((BigInteger) getEntityManager()
                .createNativeQuery("SELECT count(DISTINCT lo.id) AS c\n" +
                        "FROM LearningObject lo\n" +
                        "   JOIN LearningObject_Taxon lt ON lt.learningObject = lo.id\n" +
                        "   JOIN ImproperContent r ON r.learningObject = lo.id " +
                        "WHERE (lo.visibility = 'PUBLIC' OR lo.visibility = 'NOT_LISTED')\n" +
                        "  AND r.reviewed = 0 " +
                        "  AND lo.deleted = 0\n" +
                        "  AND lt.taxon IN (:taxonIds)")
                .setParameter("taxonIds", taxonDao.getUserTaxonsWithChildren(user))
                .getSingleResult()).longValue();
    }

    public List<ImproperContent> findAllUnreviewedOld() {
        return findByFieldList("reviewed", false);
    }

    public List<ImproperContent> findByLearningObjectId(Long learningObjectId) {
        return getEntityManager()
                .createQuery("select ic " +
                        "from ImproperContent ic " +
                        "where ic.learningObject.id = :loId " +
                        "and ic.reviewed = false")
                .setParameter("loId", learningObjectId)
                .getResultList();
    }
}