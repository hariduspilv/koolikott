package ee.hm.dop.dao;

import ee.hm.dop.model.FirstReview;

import java.util.List;

public class FirstReviewDao extends AbstractDao<FirstReview> {

    public List<FirstReview> findAllUnreviewed() {
        return getEntityManager()
                .createNativeQuery("select f.* from (\n" +
                        "  SELECT f.*\n" +
                        "  FROM FirstReview f\n" +
                        "    JOIN LearningObject o ON f.learningObject = o.id\n" +
                        "    JOIN Portfolio p ON p.id = o.id AND p.visibility = 'PUBLIC'\n" +
                        "  UNION ALL\n" +
                        "  SELECT f.*\n" +
                        "  FROM FirstReview f\n" +
                        "    JOIN LearningObject o ON f.learningObject = o.id\n" +
                        "    JOIN Material m ON m.id = o.id\n" +
                        ") f order by id asc;", entity())
                .getResultList();
    }
}
