package ee.hm.dop.service.synchronizer;

import ee.hm.dop.dao.ReviewableChangeDao;
import ee.hm.dop.model.AdminLearningObject;
import ee.hm.dop.model.ReviewableChange;
import ee.hm.dop.model.enums.ReviewStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

import static ee.hm.dop.utils.ConfigurationProperties.AUTOMATICALLY_ACCEPT_REVIEWABLE_CHANGES;

@Slf4j
@Service
@Transactional
public class AutomaticallyAcceptReviewableChange {

    @Inject
    private Configuration configuration;
    @Inject
    private ReviewableChangeDao reviewableChangeDao;

    public synchronized void run() {
        try {
            ReviewableChangeDao reviewableChangeDao = newReviewableChangeDao();
            List<AdminLearningObject> allUnreviewed = reviewableChangeDao.findAllUnreviewed();

            log.info(String.format("Automatic ReviewableChange Acceptor found a total of %s changes", allUnreviewed.size()));

            int accepted = 0;
            LocalDateTime _10DaysBefore = LocalDateTime.now().minusDays(configuration.getInt(AUTOMATICALLY_ACCEPT_REVIEWABLE_CHANGES));
            for (AdminLearningObject learningObject : allUnreviewed) {
                for (ReviewableChange reviewableChange : learningObject.getReviewableChanges()) {
                    if (!reviewableChange.isReviewed() && reviewableChange.getCreatedAt().isBefore(_10DaysBefore)) {
                        accepted++;
                        reviewableChange.setReviewed(true);
                        reviewableChange.setReviewedAt(LocalDateTime.now());
                        reviewableChange.setStatus(ReviewStatus.ACCEPTED_AUTOMATICALLY);
                        reviewableChangeDao.createOrUpdate(reviewableChange);
                    }
                }
            }

            log.info("Automatic ReviewableChange Acceptor has finished execution, updated changes: " + accepted);
        } catch (Exception e) {
            log.error("Unexpected error while automatically accepting ReviewableChange", e);
        }
    }

    protected ReviewableChangeDao newReviewableChangeDao() {
        return reviewableChangeDao;
    }

    @Async
    public synchronized void runAsync() {
        run();
    }
}
