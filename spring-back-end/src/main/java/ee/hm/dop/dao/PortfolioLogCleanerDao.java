package ee.hm.dop.dao;

import ee.hm.dop.model.PortfolioLog;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Repository
public class PortfolioLogCleanerDao extends AbstractDao<PortfolioLog> {

    public int deleteOlderThan(LocalDateTime dateTime) {
        return getEntityManager()
                .createNativeQuery("DELETE FROM PortfolioLog WHERE publishedAt < :deleteTime")
                .setParameter("deleteTime", Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant()))
                .executeUpdate();
    }

    public long findCountOfOlderThan(LocalDateTime dateTime) {
        return ((BigInteger) getEntityManager()
                .createNativeQuery("SELECT count(id) from PortfolioLog WHERE publishedAt < :deleteTime")
                .setParameter("deleteTime", Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant()))
                .getSingleResult())
                .longValue();
    }
}
