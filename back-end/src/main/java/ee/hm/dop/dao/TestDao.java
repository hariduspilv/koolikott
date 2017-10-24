package ee.hm.dop.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

public class TestDao {
    @Inject
    protected EntityManager entityManager;

    public void restore(List<Long> learningObjectId) {
        entityManager.createNativeQuery(
                "UPDATE FirstReview SET " +
                        "reviewed = 0, " +
                        "reviewedBy = NULL, " +
                        "reviewedAt = NULL " +
                        "WHERE learningObject in (:id)")
                .setParameter("id", learningObjectId)
                .executeUpdate();

        entityManager.createNativeQuery(
                "UPDATE ImproperContent SET " +
                        "reviewed = 0, " +
                        "reviewedBy = NULL, " +
                        "reviewedAt = NULL, " +
                        "status = NULL " +
                        "WHERE learningObject in (:id)")
                .setParameter("id", learningObjectId)
                .executeUpdate();

        entityManager.createNativeQuery(
                "UPDATE ReviewableChange SET " +
                        "reviewed = 0, " +
                        "reviewedBy = NULL, " +
                        "reviewedAt = NULL, " +
                        "status = NULL " +
                        "WHERE learningObject in (:id)")
                .setParameter("id", learningObjectId)
                .executeUpdate();

        entityManager.createNativeQuery(
                "UPDATE BrokenContent SET deleted = 0 WHERE material in (:id)")
                .setParameter("id", learningObjectId)
                .executeUpdate();

        entityManager.createNativeQuery(
                "UPDATE LearningObject SET deleted = 0 WHERE id in (:id)")
                .setParameter("id", learningObjectId)
                .executeUpdate();
    }
}
