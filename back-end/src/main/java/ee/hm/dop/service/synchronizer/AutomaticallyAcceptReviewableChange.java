package ee.hm.dop.service.synchronizer;

import com.google.inject.Singleton;
import ee.hm.dop.config.guice.GuiceInjector;
import ee.hm.dop.dao.ReviewableChangeDao;
import ee.hm.dop.model.AdminLearningObject;
import ee.hm.dop.model.ReviewableChange;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.service.solr.SolrEngineService;
import org.apache.commons.configuration.Configuration;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import static ee.hm.dop.utils.ConfigurationProperties.AUTOMATICALLY_ACCEPT_REVIEWABLE_CHANGES;
import static java.util.concurrent.TimeUnit.DAYS;

@Singleton
public class AutomaticallyAcceptReviewableChange extends DopDaemonProcess {

    @Inject
    private SolrEngineService solrEngineService;
    @Inject
    private Configuration configuration;

    private static final Logger logger = LoggerFactory.getLogger(AutomaticallyAcceptReviewableChange.class);
    private static ScheduledFuture<?> acceptReviewableChangeHandle;

    @Override
    public synchronized void run() {
        try {
            ReviewableChangeDao reviewableChangeDao = newReviewableChangeDao();
            List<AdminLearningObject> allUnreviewed = reviewableChangeDao.findAllUnreviewed();

            beginTransaction();

            for (AdminLearningObject learningObject : allUnreviewed) {
                for (ReviewableChange reviewableChange : learningObject.getReviewableChanges()) {
                    DateTime _10DaysBefore = DateTime.now().minusDays(configuration.getInt(AUTOMATICALLY_ACCEPT_REVIEWABLE_CHANGES));
                    if (reviewableChange.getCreatedAt().isBefore(_10DaysBefore)) {
                        reviewableChange.setReviewed(true);
                        reviewableChange.setReviewedAt(DateTime.now());
                        reviewableChange.setStatus(ReviewStatus.ACCEPTED_AUTOMATICALLY);
                        reviewableChangeDao.createOrUpdate(reviewableChange);
                    }
                }
            }

            closeTransaction();
            logger.info("Automatically accepting ReviewableChange has finished execution");
        } catch (Exception e) {
            logger.error("Unexpected error while automatically accepting ReviewableChange");
            e.printStackTrace();
        } finally {
            logger.info("Updating Solr index after automatically accepting ReviewableChange");
            solrEngineService.updateIndex();
        }
    }

    public void scheduleExecution(int hourOfDayToExecute) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    logger.info("Starting automatically accepting ReviewableChange process");
                    AutomaticallyAcceptReviewableChange.this.run();
                } catch (Exception e) {
                    logger.error("Unexpected error while automatically accepting ReviewableChange", e);
                }
                logger.info("Finished automatically accepting ReviewableChange process");
            }
        };

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, getInitialDelay(hourOfDayToExecute), DAYS.toMillis(1));
    }

    @Override
    public void stop() {
        if (acceptReviewableChangeHandle == null) {
            logger.info("Automatically accepting ReviewableChange not scheduled for running.");
            return;
        }

        logger.info("Canceling automatically accepting ReviewableChange.");
        while (!acceptReviewableChangeHandle.cancel(false)) {
            try {
                logger.info("Was not possible to cancel service. Waiting for 100ms and try again.");
                wait(100);
            } catch (InterruptedException ignored) {
            }
        }

        acceptReviewableChangeHandle = null;
        logger.info("Automatically accepting ReviewableChange canceled.");
    }

    private ReviewableChangeDao newReviewableChangeDao() {
        return GuiceInjector.getInjector().getInstance(ReviewableChangeDao.class);
    }
}
