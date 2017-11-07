package ee.hm.dop.dao;

import ee.hm.dop.model.AdminLearningObject;
import ee.hm.dop.model.FirstReview;
import ee.hm.dop.model.User;

<<<<<<< HEAD
import java.math.BigDecimal;
<<<<<<< HEAD
=======
=======
import javax.inject.Inject;
>>>>>>> new-develop
import java.math.BigInteger;
>>>>>>> new-develop
import java.util.List;
import java.util.stream.Collectors;

public class FirstReviewDao extends AbstractDao<FirstReview> {

<<<<<<< HEAD
    public List<FirstReview> findAllUnreviewed() {
        return getEntityManager()
<<<<<<< HEAD
                .createNativeQuery("SELECT f.* FROM (\n" +
                        "  SELECT f.*\n" +
                        "  FROM FirstReview f\n" +
                        "    JOIN LearningObject o ON f.learningObject = o.id\n" +
                        "    JOIN Portfolio p ON p.id = o.id\n" +
                        "       AND (p.visibility = 'PUBLIC' OR p.visibility = 'NOT_LISTED')\n" +
                        "    WHERE f.reviewed = 0 " +
                        "UNION ALL\n" +
                        "  SELECT f.*\n" +
                        "  FROM FirstReview f\n" +
                        "    JOIN LearningObject o ON f.learningObject = o.id\n" +
                        "    JOIN Material m ON m.id = o.id\n" +
                        "    WHERE f.reviewed = 0 " +
                        ") f ORDER BY createdAt ASC, id ASC", entity())
=======
                .createNativeQuery("SELECT f.*\n" +
                        "FROM FirstReview f\n" +
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
>>>>>>> new-develop
                .setMaxResults(300)
=======
    @Inject
    private TaxonDao taxonDao;
    @Inject
    private AdminLearningObjectDao adminLearningObjectDao;

    public List<AdminLearningObject> findAllUnreviewed() {
        List<BigInteger> resultList = getEntityManager()
                .createNativeQuery("SELECT\n" +
                        "  lo.id\n" +
                        "FROM LearningObject lo\n" +
                        "  JOIN FirstReview r ON r.learningObject = lo.id\n" +
                        "WHERE r.reviewed = 0\n" +
                        "      AND (lo.visibility = 'PUBLIC' OR lo.visibility = 'NOT_LISTED')\n" +
                        "      AND lo.id NOT IN (SELECT ic.learningObject\n" +
                        "                        FROM ImproperContent ic\n" +
                        "                        WHERE ic.learningObject = lo.id\n" +
                        "                              AND ic.reviewed = 0)\n" +
                        "      AND lo.id NOT IN (SELECT ic.material\n" +
                        "                        FROM BrokenContent ic\n" +
                        "                        WHERE ic.material = lo.id\n" +
                        "                              AND ic.deleted = 0)\n" +
                        "GROUP BY lo.id\n" +
                        "ORDER BY min(r.createdAt) asc")
                .setMaxResults(200)
>>>>>>> new-develop
                .getResultList();
        List<Long> collect = resultList.stream().map(BigInteger::longValue).collect(Collectors.toList());
        return adminLearningObjectDao.findById(collect);
    }

<<<<<<< HEAD

    public List<FirstReview> findAllUnreviewed(User user) {
        return getEntityManager()
<<<<<<< HEAD
                .createNativeQuery("SELECT f.* FROM (\n" +
                        "       SELECT f.*\n" +
                        "       FROM FirstReview f\n" +
                        "         JOIN LearningObject o ON f.learningObject = o.id\n" +
                        "         JOIN Portfolio p ON p.id = o.id\n" +
                        "           AND (p.visibility = 'PUBLIC' OR p.visibility = 'NOT_LISTED')\n" +
                        "         JOIN LearningObject_Taxon lt ON lt.learningObject = o.id\n" +
                        "         JOIN User_Taxon ut ON ut.taxon = lt.taxon\n" +
                        "       WHERE f.reviewed = 0\n" +
                        "             AND ut.user = :user\n" +
                        "       UNION ALL\n" +
                        "       SELECT f.*\n" +
                        "       FROM FirstReview f\n" +
                        "         JOIN LearningObject o ON f.learningObject = o.id\n" +
                        "         JOIN Material m ON m.id = o.id\n" +
                        "         JOIN LearningObject_Taxon lt ON lt.learningObject = o.id\n" +
                        "         JOIN User_Taxon ut ON ut.taxon = lt.taxon\n" +
                        "       WHERE f.reviewed = 0\n" +
                        "             AND ut.user = :user\n" +
                        "     ) f ORDER BY createdAt ASC, id ASC", entity())
=======
                .createNativeQuery("SELECT f.*\n" +
                        "FROM FirstReview f\n" +
                        "  JOIN LearningObject o ON f.learningObject = o.id\n" +
                        "  JOIN LearningObject_Taxon lt ON lt.learningObject = o.id\n" +
                        "  JOIN User_Taxon ut ON ut.taxon = lt.taxon\n" +
                        "WHERE f.reviewed = 0\n" +
                        "  AND (o.visibility = 'PUBLIC' OR o.visibility = 'NOT_LISTED')\n" +
                        "  AND NOT exists(SELECT 1 FROM ImproperContent ic " +
                        "                   WHERE ic.learningObject = f.learningObject " +
                        "                   AND ic.reviewed = 0)\n" +
                        "  AND NOT exists(SELECT 1 FROM BrokenContent bc " +
                        "                   WHERE bc.material = f.learningObject" +
                        "                   AND bc.deleted = 0 )" +
                        "  AND ut.user = :user\n" +
                        "ORDER BY f.createdAt ASC, f.id ASC", entity())
>>>>>>> new-develop
                .setParameter("user", user.getId())
                .setMaxResults(300)
=======
    public List<AdminLearningObject> findAllUnreviewed(User user) {
        List<BigInteger> resultList =  getEntityManager()
                .createNativeQuery("SELECT\n" +
                        "  lo.id\n" +
                        "FROM LearningObject lo\n" +
                        "  JOIN FirstReview r ON r.learningObject = lo.id\n" +
                        "  JOIN LearningObject_Taxon lt on lt.learningObject = lo.id\n" +
                        "WHERE r.reviewed = 0\n" +
                        "      AND (lo.visibility = 'PUBLIC' OR lo.visibility = 'NOT_LISTED')\n" +
                        "      AND lo.id NOT IN (SELECT ic.learningObject\n" +
                        "                        FROM ImproperContent ic\n" +
                        "                        WHERE ic.learningObject = lo.id\n" +
                        "                              AND ic.reviewed = 0)\n" +
                        "      AND lo.id NOT IN (SELECT ic.material\n" +
                        "                        FROM BrokenContent ic\n" +
                        "                        WHERE ic.material = lo.id\n" +
                        "                              AND ic.deleted = 0)\n" +
                        "      AND lt.taxon in (:taxonIds)\n" +
                        "GROUP BY lo.id\n" +
                        "ORDER BY min(r.createdAt) asc")
                .setParameter("taxonIds", taxonDao.getUserTaxonsWithChildren(user))
                .setMaxResults(200)
>>>>>>> new-develop
                .getResultList();
        List<Long> collect = resultList.stream().map(BigInteger::longValue).collect(Collectors.toList());
        return adminLearningObjectDao.findById(collect);
    }

<<<<<<< HEAD
<<<<<<< HEAD
    public BigDecimal findCountOfUnreviewed() {
        return (BigDecimal) getEntityManager()
                .createNativeQuery("\n" +
                        "SELECT sum(c) FROM (\n" +
                        "                  SELECT count(1) AS c\n" +
                        "                  FROM FirstReview f\n" +
                        "                    JOIN LearningObject o ON f.learningObject = o.id\n" +
                        "                    JOIN Portfolio p ON p.id = o.id\n" +
                        "                       AND (p.visibility = 'PUBLIC'\n" +
                        "                       OR p.visibility = 'NOT_LISTED')\n" +
                        "                  WHERE f.reviewed = 0\n" +
                        "                  UNION ALL\n" +
                        "                  SELECT count(1) AS c\n" +
                        "                  FROM FirstReview f\n" +
                        "                    JOIN LearningObject o ON f.learningObject = o.id\n" +
                        "                    JOIN Material m ON m.id = o.id\n" +
                        "                  WHERE f.reviewed = 0\n" +
                        "                ) f ")
                .getSingleResult();
    }

    public BigDecimal findCountOfUnreviewed(User user) {
        return (BigDecimal) getEntityManager()
                .createNativeQuery("\n" +
                        "SELECT sum(c) FROM (\n" +
                        "                  SELECT count(1) AS c\n" +
                        "                  FROM FirstReview f\n" +
                        "                    JOIN LearningObject o ON f.learningObject = o.id\n" +
                        "                    JOIN Portfolio p ON p.id = o.id\n" +
                        "                       AND (p.visibility = 'PUBLIC'\n" +
                        "                       OR p.visibility = 'NOT_LISTED')\n" +
                        "                    JOIN LearningObject_Taxon lt ON lt.learningObject = o.id\n" +
                        "                    JOIN User_Taxon ut ON ut.taxon = lt.taxon\n" +
                        "                  WHERE f.reviewed = 0\n" +
                        "                       AND ut.user = :user\n" +
                        "                  UNION ALL\n" +
                        "                  SELECT count(1) AS c\n" +
                        "                  FROM FirstReview f\n" +
                        "                    JOIN LearningObject o ON f.learningObject = o.id\n" +
                        "                    JOIN Material m ON m.id = o.id\n" +
                        "                    JOIN LearningObject_Taxon lt ON lt.learningObject = o.id\n" +
                        "                    JOIN User_Taxon ut ON ut.taxon = lt.taxon\n" +
                        "                  WHERE f.reviewed = 0\n" +
                        "                       AND ut.user = :user\n" +
                        "                ) f ")
=======
    public BigInteger findCountOfUnreviewed() {
        return (BigInteger) getEntityManager()
=======
    public long findCountOfUnreviewed() {
        return ((BigInteger) getEntityManager()
>>>>>>> new-develop
                .createNativeQuery("SELECT count(1) AS c\n" +
                        "FROM FirstReview f\n" +
                        "   JOIN LearningObject lo ON f.learningObject = lo.id\n" +
                        "WHERE f.reviewed = 0\n" +
                        "   AND (lo.visibility = 'PUBLIC' OR lo.visibility = 'NOT_LISTED')\n" +
                        "   AND lo.id NOT IN (SELECT ic.learningObject\n" +
                        "                        FROM ImproperContent ic\n" +
                        "                        WHERE ic.learningObject = lo.id\n" +
                        "                              AND ic.reviewed = 0)\n" +
                        "   AND lo.id NOT IN (SELECT ic.material\n" +
                        "                        FROM BrokenContent ic\n" +
                        "                        WHERE ic.material = lo.id\n" +
                        "                              AND ic.deleted = 0)"
                )
                .getSingleResult()).longValue();
    }

<<<<<<< HEAD
    public BigInteger findCountOfUnreviewed(User user) {
        return (BigInteger) getEntityManager()
                .createNativeQuery("SELECT count(1) AS c\n" +
                        "FROM FirstReview f\n" +
                        "   JOIN LearningObject o ON f.learningObject = o.id\n" +
                        "   JOIN LearningObject_Taxon lt ON lt.learningObject = o.id\n" +
                        "   JOIN User_Taxon ut ON ut.taxon = lt.taxon\n" +
                        "WHERE f.reviewed = 0\n" +
                        "   AND (o.visibility = 'PUBLIC' OR o.visibility = 'NOT_LISTED')\n" +
                        "   AND ut.user = :user\n" +
                        "  AND NOT exists(SELECT 1 FROM ImproperContent ic " +
                        "                   WHERE ic.learningObject = f.learningObject " +
                        "                   AND ic.reviewed = 0)\n" +
                        "  AND NOT exists(SELECT 1 FROM BrokenContent bc " +
                        "                   WHERE bc.material = f.learningObject" +
                        "                   AND bc.deleted = 0 )")
>>>>>>> new-develop
                .setParameter("user", user.getId())
                .getSingleResult();
=======
    public long findCountOfUnreviewed(User user) {
        return ((BigInteger) getEntityManager()
                .createNativeQuery("SELECT count(DISTINCT lo.id) AS c\n" +
                        "FROM LearningObject lo\n" +
                        "   JOIN LearningObject_Taxon lt ON lt.learningObject = lo.id\n" +
                        "   JOIN FirstReview r on r.learningObject = lo.id " +
                        "WHERE (lo.visibility = 'PUBLIC' OR lo.visibility = 'NOT_LISTED')\n" +
                        "  AND r.reviewed = 1 " +
                        "  AND lo.id NOT IN (SELECT ic.learningObject\n" +
                        "                        FROM ImproperContent ic\n" +
                        "                        WHERE ic.learningObject = lo.id\n" +
                        "                              AND ic.reviewed = 0)\n" +
                        "  AND lo.id NOT IN (SELECT ic.material\n" +
                        "                        FROM BrokenContent ic\n" +
                        "                        WHERE ic.material = lo.id\n" +
                        "                              AND ic.deleted = 0)" +
                        "  AND lt.taxon IN (:taxonIds)")
                .setParameter("taxonIds", taxonDao.getUserTaxonsWithChildren(user))
                .getSingleResult()).longValue();
>>>>>>> new-develop
    }
}
