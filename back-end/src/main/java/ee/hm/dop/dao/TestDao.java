package ee.hm.dop.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

public class TestDao {
    @Inject
    protected EntityManager entityManager;

    public void restoreAdminReviewingTest(List<Long> learningObjectId) {
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
                "UPDATE LearningObject SET deleted = 0 WHERE id in (:id)")
                .setParameter("id", learningObjectId)
                .executeUpdate();
    }

    public synchronized void removeChanges(List<Long> learningObjectId) {
        entityManager.createNativeQuery(
                "DELETE FROM LearningObject_Taxon f WHERE f.learningObject in (:id)")
                .setParameter("id", learningObjectId)
                .executeUpdate();

        entityManager.createNativeQuery(
                "DELETE FROM LearningObject_Tag f WHERE f.learningObject in (:id)")
                .setParameter("id", learningObjectId)
                .executeUpdate();

        entityManager.createNativeQuery(
                "DELETE FROM FirstReview f WHERE f.learningObject in (:id)")
                .setParameter("id", learningObjectId)
                .executeUpdate();

        entityManager.createNativeQuery(
                "DELETE FROM ReviewableChange f WHERE f.learningObject in (:id)")
                .setParameter("id", learningObjectId)
                .executeUpdate();

        entityManager.createNativeQuery(
                "DELETE FROM ReportingReason r " +
                        "WHERE r.improperContent in (" +
                        "   SELECT f.id from ImproperContent f " +
                        "   WHERE f.learningObject in (:id))")
                .setParameter("id", learningObjectId)
                .executeUpdate();

        entityManager.createNativeQuery(
                "DELETE FROM ImproperContent f WHERE f.learningObject in (:id)")
                .setParameter("id", learningObjectId)
                .executeUpdate();

        entityManager.createNativeQuery(
                "UPDATE LearningObject SET deleted = 0 WHERE id in (:id)")
                .setParameter("id", learningObjectId)
                .executeUpdate();
    }

    public void setUnReviewed(List<Long> learningObjectId) {
        for (Long id : learningObjectId) {
            entityManager.createNativeQuery(
                    "INSERT INTO FirstReview(learningObject, reviewed, createdAt) VALUES (:id, 0, CURRENT_TIMESTAMP())")
                    .setParameter("id", id)
                    .executeUpdate();
        }
    }
}
