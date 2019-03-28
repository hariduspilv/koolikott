package ee.hm.dop.service.synchronizer;

import ee.hm.dop.dao.PortfolioDao;
import ee.hm.dop.dao.PortfolioMaterialDao;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.service.content.PortfolioMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@Slf4j
@Service
@Transactional
public class UpdatePortfolioMaterialsExecutor {

    private static final Logger logger = LoggerFactory.getLogger(UpdatePortfolioMaterialsExecutor.class);
    @Inject
    private PortfolioDao portfolioDao;
    @Inject
    private PortfolioMaterialService portfolioMaterialService;
    @Inject
    private PortfolioMaterialDao portfolioMaterialDao;

    public synchronized void run() {
        try {
            boolean hasData = portfolioMaterialDao.hasData();
            if (hasData) {
                logger.info("PortfolioMaterials table is not empty, migration will not proceed!");
                return;
            }
            logger.info("Portfolio materials updating started");
            for (Portfolio portfolio : portfolioDao.getAllPortfoliosDeletedExcluded()) {
                portfolioMaterialService.save(portfolio);
            }
            logger.info("Portfolio materials updating ended");
        } catch (Exception e) {
            logger.error("Unexpected error while synchronizing portfolios with materials.", e);
        }
    }

    @Async
    @Transactional
    public synchronized void runAsync() {
        run();
    }
}
