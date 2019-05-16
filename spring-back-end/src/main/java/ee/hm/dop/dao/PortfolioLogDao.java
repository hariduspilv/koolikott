package ee.hm.dop.dao;

import ee.hm.dop.model.PortfolioLog;
import org.springframework.stereotype.Repository;

import java.util.List;

import static java.time.LocalDateTime.now;

@Repository
public class PortfolioLogDao extends AbstractDao<PortfolioLog> {

    public Class<PortfolioLog> entity() {
        return PortfolioLog.class;
    }

    public List<PortfolioLog> findByIdAllPortfolioLogs(Long learningObjectId) {
        return getEntityManager()
                .createQuery("" +
                                "SELECT p FROM PortfolioLog p \n" +
                                "   WHERE p.learningObject = :id "
                        , PortfolioLog.class)
                .setParameter("id", learningObjectId)
                .getResultList();
    }

    @Override
    public PortfolioLog createOrUpdate(PortfolioLog entity) {
        entity.setLastInteraction(now());
        return super.createOrUpdate(entity);
    }
}
