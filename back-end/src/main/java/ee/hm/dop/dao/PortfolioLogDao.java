package ee.hm.dop.dao;

import ee.hm.dop.model.PortfolioLog;

import java.util.List;

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
}
