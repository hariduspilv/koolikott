package ee.hm.dop.executor;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.joda.time.LocalDateTime.now;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import com.google.inject.Singleton;
import ee.hm.dop.guice.GuiceInjector;
import ee.hm.dop.model.Repository;
import ee.hm.dop.service.RepositoryService;
import ee.hm.dop.utils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class SynchronizeMaterialsExecutor {

    private static final Logger logger = LoggerFactory.getLogger(SynchronizeMaterialsExecutor.class);

    // Guarantees that only 1 threat is running at a time
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static ScheduledFuture<?> synchronizeMaterialHandle;

    public synchronized void synchronizeMaterials() {
        try {
            beginTransaction();

            RepositoryService repositoryService = newRepositoryService();
            List<Repository> repositories = repositoryService.getAllRepositorys();

            logger.info(format("Synchronizing %d repositories...", repositories.size()));

            for (Repository repository : repositories) {
                logger.info(format("synchonizing repository %S:", repository));
                repositoryService.synchronize(repository);
            }

            logger.info("Synchronization repository service finished execution.");
        } catch (Exception e) {
            logger.error("Unexpected error while synchronizing materials.", e);
        } finally {
            closeTransaction();
        }
    }

    public synchronized void scheduleExecution(int hourOfDayToExecute) {
        if (synchronizeMaterialHandle != null) {
            logger.info("Synchronize Materials Executor already started.");
            return;
        }

        final Runnable executor = new Runnable() {

            @Override
            public void run() {
                logger.info("Starting new material synchronization process.");
                synchronizeMaterials();
            }
        };

        long initialDelay = getInitialDelay(hourOfDayToExecute);
        long period = DAYS.toMillis(1);

        logger.info("Scheduling Synchronization repository service first execution to "
                + now().plusMillis((int) initialDelay));
        synchronizeMaterialHandle = scheduler.scheduleAtFixedRate(executor, initialDelay, period, MILLISECONDS);
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
            } catch (InterruptedException e) {
            }
        }

        synchronizeMaterialHandle = null;
        logger.info("Synchronization repository service canceled.");
    }

    /**
     * Package access modifier for testing purpose
     */
    long getInitialDelay(int hourOfDayToExecute) {
        return ExecutorHelper.getInitialDelay(hourOfDayToExecute);
    }

    /**
     * Test only
     * 
     * @param synchronizeMaterialHandle
     */
    void setSynchronizeMaterialHandle(ScheduledFuture<?> synchronizeMaterialHandle) {
        SynchronizeMaterialsExecutor.synchronizeMaterialHandle = synchronizeMaterialHandle;
    }

    /**
     * Package access modifier for testing purpose
     * 
     */
    protected RepositoryService newRepositoryService() {
        return GuiceInjector.getInjector().getInstance(RepositoryService.class);
    }

    /**
     * Package access modifier for testing purpose
     * 
     */
    protected void closeTransaction() {
        DbUtils.closeTransaction();
    }

    /**
     * Package access modifier for testing purpose
     * 
     */
    protected void beginTransaction() {
        DbUtils.getTransaction().begin();
    }

}
