package ee.hm.dop.service.synchronizer;

import ee.hm.dop.config.guice.GuiceInjector;
import ee.hm.dop.dao.PortfolioDao;
import ee.hm.dop.dao.PortfolioMaterialDao;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.service.content.PortfolioMaterialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

@Singleton
public class UpdatePortfolioMaterialsExecutor extends DopDaemonProcess {

    private static final Logger logger = LoggerFactory.getLogger(UpdatePortfolioMaterialsExecutor.class);

    @Override
    public synchronized void run() {
        try {
            PortfolioMaterialService newPortfolioMaterialService = newPortfolioMaterialService();

            boolean hasData = newPortfolioMaterialDao().hasData();
            if (hasData) {
                logger.info("PortfolioMaterials table is not empty, migration will not proceed!");
                return;
            }

            beginTransaction();
            logger.info("Portfolio materials updating started");
            for (Portfolio portfolio : newPortfolioDao().getAllPortfoliosDeletedExcluded()) {
                newPortfolioMaterialService.save(portfolio);
            }
            closeTransaction();
            logger.info("Portfolio materials updating ended");

        } catch (Exception e) {
            logger.error("Unexpected error while synchronizing portfolios with materials.", e);
        } finally {
            closeEntityManager();
        }
    }

    private PortfolioDao newPortfolioDao() {
        return GuiceInjector.getInjector().getInstance(PortfolioDao.class);
    }

    private PortfolioMaterialDao newPortfolioMaterialDao() {
        return GuiceInjector.getInjector().getInstance(PortfolioMaterialDao.class);
    }

    private PortfolioMaterialService newPortfolioMaterialService() {
        return GuiceInjector.getInjector().getInstance(PortfolioMaterialService.class);
    }
}
