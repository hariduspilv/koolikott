package ee.hm.dop.service.synchronizer;

import com.google.inject.Singleton;
import ee.hm.dop.config.guice.GuiceInjector;
import ee.hm.dop.dao.MaterialDao;
import ee.hm.dop.dao.PortfolioDao;
import ee.hm.dop.dao.PortfolioMaterialDao;
import ee.hm.dop.model.*;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.service.synchronizer.oaipmh.SynchronizationAudit;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.DAYS;

@Singleton
public class SynchronizeMaterialsExecutor extends DopDaemonProcess {

    public static final String MATERIAL_REGEX = "class=\"chapter-embed-card chapter-embed-card--material\" data-id=\"[0-9]*\"";
    public static final String NUMBER_REGEX = "\\d+";

    @Inject
    private SolrEngineService solrEngineService;
    @Inject
    private PortfolioDao portfolioDao;
//    @Inject
//    private PortfolioMaterialDao portfolioMaterialDao;
    @Inject
    private MaterialDao materialDao;



    private static final Logger logger = LoggerFactory.getLogger(SynchronizeMaterialsExecutor.class);
    private static Future<?> synchronizeMaterialHandle;

    @Override
    public synchronized void run() {
        List<SynchronizationAudit> audits = new ArrayList<>();
        try {
            beginTransaction();
//            ---------------------------------------
            //run only once
            //getAllPortfolios

            PortfolioMaterialDao newPortfolioMaterialDao = newPortfolioMaterialDao();
            Pattern chapterPattern = Pattern.compile(MATERIAL_REGEX);
            Pattern numberPattern = Pattern.compile(NUMBER_REGEX);

            for (Portfolio portfolio : portfolioDao.findAll()) {
                for (Chapter chapter : portfolio.getChapters()) {
                    for (ChapterBlock block : chapter.getBlocks()) {
                        if (StringUtils.isNotBlank(block.getHtmlContent())) {
                            Matcher matcher = chapterPattern.matcher(block.getHtmlContent());
                            while (matcher.find()) {
                                Matcher numberMatcher = numberPattern.matcher(matcher.group());
                                while (numberMatcher.find()) {
                                    PortfolioMaterial portfolioMaterial = new PortfolioMaterial();
                                    portfolioMaterial.setPortfolio(portfolio);
                                    portfolioMaterial.setMaterial(materialDao.findById(Long.valueOf(numberMatcher.group())));
                                    newPortfolioMaterialDao.createOrUpdate(portfolioMaterial);
                                }
                            }
                        }
                    }
                }

            }
            RepositoryService repositoryService = newRepositoryService();
            List<Repository> repositories = repositoryService.getAllUsedRepositories();

            logger.info(format("Synchronizing %d repositories...", repositories.size()));

            for (Repository repository : repositories) {
                logger.info(format("Synchonizing repository %S:", repository));
                //For every repository make a new transaction - one fail will not roll back all repositories
                beginTransaction();

                SynchronizationAudit audit = repositoryService.synchronize(repository);
                if (audit != null) audits.add(audit);

                closeTransaction();
            }

            closeTransaction();
            logger.info("Synchronization repository service finished execution.");
        } catch (Exception e) {
            logger.error("Unexpected error while synchronizing materials.", e);
        } finally {
            if (audits.stream().anyMatch(SynchronizationAudit::changeOccured)) {
                logger.info("Solr full import after synchronizing all materials");
                solrEngineService.fullImport();
            } else {
                logger.info("Synchronizing materials doesn't need solr update");
            }
            closeEntityManager();
        }
    }

    public void scheduleExecution(int hourOfDayToExecute) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    logger.info("Starting new material synchronization process.");
                    SynchronizeMaterialsExecutor.this.run();
                } catch (Exception e) {
                    logger.error("Unexpected error while scheduling sync.", e);
                }

                logger.info("Finished new material synchronization process.");
            }
        };

        Timer timer = new Timer();
        long initialDelay = getInitialDelay(hourOfDayToExecute);

        timer.scheduleAtFixedRate(timerTask, initialDelay, DAYS.toMillis(1));
    }

    public synchronized void stop() {
        if (synchronizeMaterialHandle == null) {
            logger.info("Synchronization repository service not scheduled for running.");
            return;
        }

        logger.info("Canceling Synchronization repository service.");
        while (!synchronizeMaterialHandle.cancel(false)) {
            try {
                logger.info("Was not possible to cancel service. Waiting for 100ms and try again.");
                wait(100);
            } catch (InterruptedException ignored) {
            }
        }

        synchronizeMaterialHandle = null;
        logger.info("Synchronization repository service canceled.");
    }

    /**
     * Test only
     */
    void setSynchronizeMaterialHandle(ScheduledFuture<?> synchronizeMaterialHandle) {
        SynchronizeMaterialsExecutor.synchronizeMaterialHandle = synchronizeMaterialHandle;
    }

    protected RepositoryService newRepositoryService() {
        return GuiceInjector.getInjector().getInstance(RepositoryService.class);
    }
    private PortfolioMaterialDao newPortfolioMaterialDao(){
        return GuiceInjector.getInjector().getInstance(PortfolioMaterialDao.class);
    }
}
