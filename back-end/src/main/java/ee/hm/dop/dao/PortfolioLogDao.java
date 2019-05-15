package ee.hm.dop.dao;

import ee.hm.dop.model.PortfolioLog;

import java.util.List;

import static org.joda.time.DateTime.now;

public class PortfolioLogDao extends AbstractDao<PortfolioLog> {

    public Class<PortfolioLog> entity() {
        return PortfolioLog.class;
    }

    public PortfolioLog createOrUpdate(PortfolioLog entity) {
        entity.setPublishedAt(now());
        return super.createOrUpdate(entity);
    }

    public List<PortfolioLog> findByIdAllPortfolioLOgs(Long learningObjectId) {
        return getEntityManager()
                .createQuery("" +
                                "SELECT p FROM PortfolioLog p \n" +
                                "   WHERE p.learningObject = :id "
                        , PortfolioLog.class)
                .setParameter("id", learningObjectId)
                .getResultList();

    }
}
