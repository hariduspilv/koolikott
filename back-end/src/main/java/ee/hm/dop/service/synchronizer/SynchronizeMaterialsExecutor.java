package ee.hm.dop.service.synchronizer;

import com.google.inject.Singleton;
import ee.hm.dop.config.guice.GuiceInjector;
import ee.hm.dop.model.Repository;
import ee.hm.dop.service.solr.SolrEngineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.DAYS;

@Singleton
public class SynchronizeMaterialsExecutor extends DopDaemonProcess {

    @Inject
    private SolrEngineService solrEngineService;

    private static final Logger logger = LoggerFactory.getLogger(SynchronizeMaterialsExecutor.class);
    private static Future<?> synchronizeMaterialHandle;

    @Override
    public synchronized void run() {
        try {
            beginTransaction();
            RepositoryService repositoryService = newRepositoryService();
            List<Repository> repositories = repositoryService.getAllUsedRepositories();

            logger.info(format("Synchronizing %d repositories...", repositories.size()));

            for (Repository repository : repositories) {
                logger.info(format("Synchonizing repository %S:", repository));
                //For every repository make a new transaction - one fail will not roll back all repositories
                beginTransaction();

                repositoryService.synchronize(repository);

                closeTransaction();
            }

            logger.info("Synchronization repository service finished execution.");
        } catch (Exception e) {
            logger.error("Unexpected error while synchronizing materials.", e);
        } finally {
            logger.info("Updating Solr index after synchronizing all materials");
            updateSolrIndex();
            closeTransaction();
        }
    }

    private void updateSolrIndex() {
        logger.info("Updating Search Engine index...");
        solrEngineService.updateIndex();
    }

    /**
     * Can be started twice. Please refactor, meanwhile use with care
     */
    @Deprecated
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
        long period = DAYS.toMillis(1);

        timer.scheduleAtFixedRate(timerTask, initialDelay, period);
    }

    @Override
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
}
