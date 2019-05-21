package ee.hm.dop.service.synchronizer;

import ee.hm.dop.dao.PortfolioLogCleanerDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDateTime;

@Service
@Transactional
public class PortfolioLogCleaner {

    @Inject
    private PortfolioLogCleanerDao portfolioLogCleanerDao;

    private static final Logger logger = LoggerFactory.getLogger(PortfolioLogCleaner.class);

    public synchronized void run() {
        try {
            PortfolioLogCleanerDao portfolioLogCleanerDao = newPortfolioLogCleanerDao();
            LocalDateTime _halfYear = LocalDateTime.now().minusMonths(6);
            long allUnreviewed = portfolioLogCleanerDao.findCountOfOlderThan(_halfYear);
            logger.info(String.format("Portfolio Log Cleaner found %s rows to remove", allUnreviewed));

            int deleted = portfolioLogCleanerDao.deleteOlderThan(_halfYear);
            logger.info(String.format("Portfolio Log Cleaner deleted %s rows", deleted));
        } catch (Exception e) {
            logger.error("Unexpected error while cleaning Portfolio Log", e);
        }
    }

    protected PortfolioLogCleanerDao newPortfolioLogCleanerDao() {
        return portfolioLogCleanerDao;
    }

    @Async
    public synchronized void runAsync() {
        run();
    }
}
