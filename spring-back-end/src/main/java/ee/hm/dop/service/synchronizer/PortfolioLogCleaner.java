package ee.hm.dop.service.synchronizer;

import ee.hm.dop.dao.ChapterDao;
import ee.hm.dop.dao.LearningObjectLogDao;
import ee.hm.dop.model.ChapterLog;
import ee.hm.dop.model.PortfolioLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PortfolioLogCleaner {

    @Inject
    private LearningObjectLogDao learningObjectLogDao;
    @Inject
    private ChapterDao chapterDao;

    private static final Logger logger = LoggerFactory.getLogger(PortfolioLogCleaner.class);

    public synchronized void run() {
        try {
            LearningObjectLogDao learningObjectLogDao = newLearningObjectLogDao();
            LocalDateTime _halfYear = LocalDateTime.now().minusMonths(6);

            List<PortfolioLog> allLoToRemove = learningObjectLogDao.findAllByDate(_halfYear);
            logger.info(String.format("Portfolio Log Cleaner found %s rows to remove", allLoToRemove.size()));

            int counter = 0;
            for (PortfolioLog lol : allLoToRemove) {
                for (ChapterLog cl : lol.getChapters()) {
                    if (cl != null) chapterDao.remove(cl);
                }
                learningObjectLogDao.remove(lol);
                counter++;
            }
            logger.info(String.format("Portfolio Log Cleaner deleted %s rows", counter));

        } catch (Exception e) {
            logger.error("Unexpected error while cleaning Portfolio Log", e);
        }
    }

    private LearningObjectLogDao newLearningObjectLogDao() {
        return learningObjectLogDao;
    }

    @Async
    public synchronized void runAsync() {
        run();
    }
}
