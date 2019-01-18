package ee.hm.dop.service.synchronizer;

import com.google.inject.Singleton;
import ee.hm.dop.dao.AuthenticatedUserDao;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Timer;
import java.util.TimerTask;

import static java.util.concurrent.TimeUnit.DAYS;

@Service
public class AuthenticatedUserCleaner extends DopDaemonProcess{

    private static final Logger logger = LoggerFactory.getLogger(AuthenticatedUserCleaner.class);

    @Override
    public synchronized void run() {
        try {
            beginTransaction();
            AuthenticatedUserDao authenticationStateDao = newAuthenticationStateDao();
            DateTime _3hoursBefore = DateTime.now().minusHours(3);
            long allNeededToRemove = authenticationStateDao.findCountOfOlderThan(_3hoursBefore);
            logger.info(String.format("Authentication State Cleaner found %s rows to remove",  allNeededToRemove));

            int deleted = authenticationStateDao.deleteOlderThan(_3hoursBefore);

            logger.info(String.format("Authentication State Cleaner deleted %s rows", deleted));
            closeTransaction();
        } catch (Exception e) {
            logger.error("Unexpected error while cleaning Authentication State", e);
        } finally {
            closeEntityManager();
        }
    }

    protected AuthenticatedUserDao newAuthenticationStateDao() {
        return null;
//        return GuiceInjector.getInjector().getInstance(AuthenticatedUserDao.class);
    }

    public void scheduleExecution(int hourOfDayToExecute) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    logger.info("Starting authentication state cleaner");
                    AuthenticatedUserCleaner.this.run();
                } catch (Exception e) {
                    logger.error("Unexpected error while cleaning authentication state", e);
                }
                logger.info("Finished authentication state cleaner");
            }
        };

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, getInitialDelay(hourOfDayToExecute), DAYS.toMillis(1));
    }
}
