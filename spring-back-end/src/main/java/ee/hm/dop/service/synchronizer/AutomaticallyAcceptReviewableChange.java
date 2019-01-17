package ee.hm.dop.service.synchronizer;

import com.google.inject.Singleton;
import ee.hm.dop.dao.ReviewableChangeDao;
import ee.hm.dop.model.AdminLearningObject;
import ee.hm.dop.model.ReviewableChange;
import ee.hm.dop.model.enums.ReviewStatus;
import org.apache.commons.configuration2.Configuration;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

import static ee.hm.dop.utils.ConfigurationProperties.AUTOMATICALLY_ACCEPT_REVIEWABLE_CHANGES;
import static java.util.concurrent.TimeUnit.DAYS;

@Singleton
public class AutomaticallyAcceptReviewableChange extends DopDaemonProcess {

    @Inject
    private Configuration configuration;

    private static final Logger logger = LoggerFactory.getLogger(AutomaticallyAcceptReviewableChange.class);
    private volatile static Future<?> acceptReviewableChangeHandle;

    @Override
    public synchronized void run() {
        try {
            beginTransaction();
            ReviewableChangeDao reviewableChangeDao = newReviewableChangeDao();
            List<AdminLearningObject> allUnreviewed = reviewableChangeDao.findAllUnreviewed();

            logger.info(String.format("Automatic ReviewableChange Acceptor found a total of %s changes",  allUnreviewed.size()));

            int accepted = 0;
            DateTime _10DaysBefore = DateTime.now().minusDays(configuration.getInt(AUTOMATICALLY_ACCEPT_REVIEWABLE_CHANGES));
            for (AdminLearningObject learningObject : allUnreviewed) {
                for (ReviewableChange reviewableChange : learningObject.getReviewableChanges()) {
                    if (!reviewableChange.isReviewed() && reviewableChange.getCreatedAt().isBefore(_10DaysBefore)) {
                        accepted++;
                        reviewableChange.setReviewed(true);
                        reviewableChange.setReviewedAt(DateTime.now());
                        reviewableChange.setStatus(ReviewStatus.ACCEPTED_AUTOMATICALLY);
                        reviewableChangeDao.createOrUpdate(reviewableChange);
                    }
                }
            }

            logger.info("Automatic ReviewableChange Acceptor has finished execution, updated changes: " + accepted);
            closeTransaction();
        } catch (Exception e) {
            logger.error("Unexpected error while automatically accepting ReviewableChange", e);
        } finally {
            closeEntityManager();
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

    protected ReviewableChangeDao newReviewableChangeDao() {
        return null;
//        return GuiceInjector.getInjector().getInstance(ReviewableChangeDao.class);
    }

    /**
     * For test
     */
    void setReviewableChangeHandle(ScheduledFuture<?> reviewableChangeHandle) {
        AutomaticallyAcceptReviewableChange.acceptReviewableChangeHandle = reviewableChangeHandle;
    }
}
