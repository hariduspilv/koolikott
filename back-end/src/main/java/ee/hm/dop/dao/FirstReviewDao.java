package ee.hm.dop.dao;

import ee.hm.dop.model.FirstReview;

import java.math.BigDecimal;
import java.util.List;

public class FirstReviewDao extends AbstractDao<FirstReview> {

    public List<FirstReview> findAllUnreviewed() {
        return getEntityManager()
                .createNativeQuery("SELECT f.* FROM (\n" +
                        "  SELECT f.*\n" +
                        "  FROM FirstReview f\n" +
                        "    JOIN LearningObject o ON f.learningObject = o.id\n" +
                        "    JOIN Portfolio p ON p.id = o.id AND p.visibility = 'PUBLIC'\n" +
                        "    WHERE f.reviewed = 0 " +
                        "UNION ALL\n" +
                        "  SELECT f.*\n" +
                        "  FROM FirstReview f\n" +
                        "    JOIN LearningObject o ON f.learningObject = o.id\n" +
                        "    JOIN Material m ON m.id = o.id\n" +
                        "    WHERE f.reviewed = 0 " +
                        ") f ORDER BY id ASC;", entity())
                .getResultList();
    }

    public BigDecimal findCountOfUnreviewed() {
        return (BigDecimal) getEntityManager()
                .createNativeQuery("\n" +
                        "SELECT sum(c) FROM (\n" +
                        "                  SELECT count(1) AS c\n" +
                        "                  FROM FirstReview f\n" +
                        "                    JOIN LearningObject o ON f.learningObject = o.id\n" +
                        "                    JOIN Portfolio p ON p.id = o.id AND p.visibility = 'PUBLIC'\n" +
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
}
