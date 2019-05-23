package ee.hm.dop.dao;

import ee.hm.dop.model.LearningObjectLog;
import ee.hm.dop.model.PortfolioLog;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class LearningObjectLogDao extends AbstractDao<LearningObjectLog> {

    @Override
    public Class<LearningObjectLog> entity() {
        return LearningObjectLog.class;
    }

    public LearningObjectLog findById(long objectId) {
        TypedQuery<LearningObjectLog> findByCode = getEntityManager()
                .createQuery("SELECT lo FROM LearningObject_Log lo WHERE lo.id = :id", entity())
                .setParameter("id", objectId);
        return getSingleResult(findByCode);
    }

    public List<PortfolioLog> findAllByDate(LocalDateTime dateTime) {
        return getEntityManager()
                .createQuery("SELECT log FROM PortfolioLog log " +
                        "WHERE log.publishedAt < :deleteTime", PortfolioLog.class)
                .setParameter("deleteTime", dateTime)
                .getResultList();
    }

    @Override
    public LearningObjectLog createOrUpdate(LearningObjectLog entity) {
        return super.createOrUpdate(entity);
    }
}
