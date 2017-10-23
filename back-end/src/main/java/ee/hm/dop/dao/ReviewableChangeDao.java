package ee.hm.dop.dao;

import ee.hm.dop.model.ReviewableChange;

import java.util.List;

public class ReviewableChangeDao extends AbstractDao<ReviewableChange> {

    public List<ReviewableChange> getAllByLearningObject(long learningObjectId) {
        return getEntityManager().createQuery("FROM ReviewableChange clo WHERE clo.learningObject.id = :id", entity()) //
                .setParameter("id", learningObjectId).getResultList();
    }

    public boolean removeById(long id) {
        return getEntityManager().createQuery("DELETE FROM ReviewableChange clo WHERE clo.id = :id")
                .setParameter("id", id)
                .executeUpdate() > 0;
    }

    public boolean removeAllByLearningObject(long id) {
        return getEntityManager().createQuery("DELETE FROM ReviewableChange clo WHERE clo.learningObject.id = :id")
                .setParameter("id", id)
                .executeUpdate() > 0;
    }

    public Long getCount() {
        return (Long) getEntityManager().createQuery("SELECT COUNT(DISTINCT clo.learningObject) FROM ReviewableChange clo").getSingleResult();
    }
}
