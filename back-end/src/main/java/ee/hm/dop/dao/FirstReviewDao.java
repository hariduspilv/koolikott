package ee.hm.dop.dao;

import ee.hm.dop.model.FirstReview;
import ee.hm.dop.model.User;

import java.math.BigDecimal;
import java.util.List;

public class FirstReviewDao extends AbstractDao<FirstReview> {

    public List<FirstReview> findAllUnreviewed() {
        return getEntityManager()
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
                .getResultList();
    }


    public List<FirstReview> findAllUnreviewed(User user) {
        return getEntityManager()
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
                .setParameter("user", user.getId())
                .getResultList();
    }

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
                .setParameter("user", user.getId())
                .getSingleResult();
    }
}
