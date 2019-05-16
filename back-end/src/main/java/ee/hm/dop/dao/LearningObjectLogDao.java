package ee.hm.dop.dao;

import ee.hm.dop.model.LearningObjectLog;

import javax.persistence.TypedQuery;

public class LearningObjectLogDao extends AbstractDao<LearningObjectLog> {

    @Override
    public Class<LearningObjectLog> entity() {
        return LearningObjectLog.class;
    }

    public LearningObjectLog findById(long objectId) {
        TypedQuery<LearningObjectLog> findByCode = getEntityManager()
                .createQuery("SELECT lo FROM LearningObjectLog lo WHERE lo.id = :id", entity()) //
                .setParameter("id", objectId);
        return getSingleResult(findByCode);
    }

    @Override
    public LearningObjectLog createOrUpdate(LearningObjectLog learningObjectLog) {
        return super.createOrUpdate(learningObjectLog);
    }
}
