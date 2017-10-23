package ee.hm.dop.dao;

import ee.hm.dop.model.FirstReview;
import ee.hm.dop.model.ReviewableChange;
import ee.hm.dop.model.User;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

public class ReviewableChangeDao extends AbstractDao<ReviewableChange> {

    @Inject
    private TaxonDao taxonDao;

    public List<ReviewableChange> getAllByLearningObject(long learningObjectId) {
        return getEntityManager().createQuery("FROM ReviewableChange clo WHERE clo.learningObject.id = :id", entity()) //
                .setParameter("id", learningObjectId).getResultList();
    }

    @Deprecated
    public boolean removeById(long id) {
        return getEntityManager().createQuery("DELETE FROM ReviewableChange clo WHERE clo.id = :id")
                .setParameter("id", id)
                .executeUpdate() > 0;
    }

    @Deprecated
    public boolean removeAllByLearningObject(long id) {
        return getEntityManager().createQuery("DELETE FROM ReviewableChange clo WHERE clo.learningObject.id = :id")
                .setParameter("id", id)
                .executeUpdate() > 0;
    }

    @Deprecated
    public Long getCount() {
        return (Long) getEntityManager().createQuery("SELECT COUNT(DISTINCT clo.learningObject) FROM ReviewableChange clo").getSingleResult();
    }

    public List<ReviewableChange> findAllUnreviewed() {
        return getEntityManager()
                .createNativeQuery("SELECT f.*\n" +
                        "FROM ReviewableChange f\n" +
                        "  JOIN LearningObject o ON f.learningObject = o.id\n" +
                        "WHERE f.reviewed = 0\n" +
                        "  AND (o.visibility = 'PUBLIC' OR o.visibility = 'NOT_LISTED')\n" +
                        "  AND NOT exists(SELECT 1 FROM ImproperContent ic " +
                        "                   WHERE ic.learningObject = f.learningObject " +
                        "                   AND ic.reviewed = 0)\n" +
                        "  AND NOT exists(SELECT 1 FROM BrokenContent bc " +
                        "                   WHERE bc.material = f.learningObject" +
                        "                   AND bc.deleted = 0 )" +
                        "ORDER BY f.createdAt ASC, f.id ASC", entity())
                .setMaxResults(300)
                .getResultList();
    }


    public List<ReviewableChange> findAllUnreviewed(User user) {
        return getEntityManager()
                .createNativeQuery("SELECT f.*\n" +
                        "FROM ReviewableChange f\n" +
                        "  JOIN LearningObject o ON f.learningObject = o.id\n" +
                        "  JOIN LearningObject_Taxon lt ON lt.learningObject = o.id\n" +
                        "WHERE f.reviewed = 0\n" +
                        "  AND (o.visibility = 'PUBLIC' OR o.visibility = 'NOT_LISTED')\n" +
                        "  AND NOT exists(SELECT 1 FROM ImproperContent ic " +
                        "                   WHERE ic.learningObject = f.learningObject " +
                        "                   AND ic.reviewed = 0)\n" +
                        "  AND NOT exists(SELECT 1 FROM BrokenContent bc " +
                        "                   WHERE bc.material = f.learningObject" +
                        "                   AND bc.deleted = 0 )" +
                        "  AND lt.taxon IN (:taxonIds)\n" +
                        "ORDER BY f.createdAt ASC, f.id ASC", entity())
                .setParameter("taxonIds", taxonDao.getUserTaxonsWithChildren(user))
                .setMaxResults(300)
                .getResultList();
    }

    public long findCountOfUnreviewed() {
        return ((BigInteger) getEntityManager()
                .createNativeQuery("SELECT count(1) AS c\n" +
                        "FROM ReviewableChange f\n" +
                        "   JOIN LearningObject o ON f.learningObject = o.id\n" +
                        "WHERE f.reviewed = 0\n" +
                        "   AND (o.visibility = 'PUBLIC' OR o.visibility = 'NOT_LISTED')\n" +
                        "  AND NOT exists(SELECT 1 FROM ImproperContent ic " +
                        "                   WHERE ic.learningObject = f.learningObject " +
                        "                   AND ic.reviewed = 0)\n" +
                        "  AND NOT exists(SELECT 1 FROM BrokenContent bc " +
                        "                   WHERE bc.material = f.learningObject" +
                        "                   AND bc.deleted = 0 )")
                .getSingleResult()).longValue();
    }

    public long findCountOfUnreviewed(User user) {
        return ((BigInteger) getEntityManager()
                .createNativeQuery("SELECT count(1) AS c\n" +
                        "FROM ReviewableChange f\n" +
                        "   JOIN LearningObject o ON f.learningObject = o.id\n" +
                        "   JOIN LearningObject_Taxon lt ON lt.learningObject = o.id\n" +
                        "WHERE f.reviewed = 0\n" +
                        "   AND (o.visibility = 'PUBLIC' OR o.visibility = 'NOT_LISTED')\n" +
                        "  AND NOT exists(SELECT 1 FROM ImproperContent ic " +
                        "                   WHERE ic.learningObject = f.learningObject " +
                        "                   AND ic.reviewed = 0)\n" +
                        "  AND NOT exists(SELECT 1 FROM BrokenContent bc " +
                        "                   WHERE bc.material = f.learningObject" +
                        "                   AND bc.deleted = 0 ) " +
                        "  AND lt.taxon IN (:taxonIds)")
                .setParameter("taxonIds", taxonDao.getUserTaxonsWithChildren(user))
                .getSingleResult()).longValue();
    }
}