package ee.hm.dop.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;

public class TestDao {
    @Inject
    protected EntityManager entityManager;

    public void restore(Long learningObjectId) {
        entityManager.createNativeQuery(
                "UPDATE FirstReview SET " +
                        "reviewed = 0, " +
                        "reviewedBy = NULL, " +
                        "reviewedAt = NULL " +
                        "WHERE learningObject = :id")
                .setParameter("id", learningObjectId)
                .executeUpdate();

        entityManager.createNativeQuery(
                "UPDATE ImproperContent SET " +
                        "reviewed = 0, " +
                        "reviewedBy = NULL, " +
                        "reviewedAt = NULL, " +
                        "status = NULL " +
                        "WHERE learningObject = :id")
                .setParameter("id", learningObjectId)
                .executeUpdate();

        entityManager.createNativeQuery(
                "UPDATE BrokenContent SET deleted = 0 WHERE material = :id")
                .setParameter("id", learningObjectId)
                .executeUpdate();

        entityManager.createNativeQuery(
                "UPDATE LearningObject SET deleted = 0 WHERE id = :id")
                .setParameter("id", learningObjectId)
                .executeUpdate();
    }
}
