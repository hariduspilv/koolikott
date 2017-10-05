package ee.hm.dop.dao;

import ee.hm.dop.model.FirstReview;
import ee.hm.dop.model.User;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class FirstReviewDao extends AbstractDao<FirstReview> {

    public List<FirstReview> findAllUnreviewed() {
        return getEntityManager()
                .createNativeQuery("SELECT f.*\n" +
                        "FROM FirstReview f\n" +
                        "  JOIN LearningObject o ON f.learningObject = o.id\n" +
                        "WHERE f.reviewed = 0\n" +
                        "  AND (o.visibility = 'PUBLIC' OR o.visibility = 'NOT_LISTED')\n" +
                        "ORDER BY f.createdAt ASC, f.id ASC", entity())
                .setMaxResults(300)
                .getResultList();
    }


    public List<FirstReview> findAllUnreviewed(User user) {
        return getEntityManager()
                .createNativeQuery("SELECT f.*\n" +
                        "FROM FirstReview f\n" +
                        "  JOIN LearningObject o ON f.learningObject = o.id\n" +
                        "  JOIN LearningObject_Taxon lt ON lt.learningObject = o.id\n" +
                        "  JOIN User_Taxon ut ON ut.taxon = lt.taxon\n" +
                        "WHERE f.reviewed = 0\n" +
                        "  AND (o.visibility = 'PUBLIC' OR o.visibility = 'NOT_LISTED')\n" +
                        "  AND ut.user = :user\n" +
                        "ORDER BY f.createdAt ASC, f.id ASC", entity())
                .setParameter("user", user.getId())
                .setMaxResults(300)
                .getResultList();
    }

    public BigInteger findCountOfUnreviewed() {
        return (BigInteger) getEntityManager()
                .createNativeQuery("SELECT count(1) AS c\n" +
                        "FROM FirstReview f\n" +
                        "   JOIN LearningObject o ON f.learningObject = o.id\n" +
                        "WHERE f.reviewed = 0\n" +
                        "   AND (o.visibility = 'PUBLIC' OR o.visibility = 'NOT_LISTED')\n")
                .getSingleResult();
    }

    public BigInteger findCountOfUnreviewed(User user) {
        return (BigInteger) getEntityManager()
                .createNativeQuery("SELECT count(1) AS c\n" +
                        "FROM FirstReview f\n" +
                        "   JOIN LearningObject o ON f.learningObject = o.id\n" +
                        "   JOIN LearningObject_Taxon lt ON lt.learningObject = o.id\n" +
                        "   JOIN User_Taxon ut ON ut.taxon = lt.taxon\n" +
                        "WHERE f.reviewed = 0\n" +
                        "   AND (o.visibility = 'PUBLIC' OR o.visibility = 'NOT_LISTED')\n" +
                        "   AND ut.user = :user\n")
                .setParameter("user", user.getId())
                .getSingleResult();
    }
}
