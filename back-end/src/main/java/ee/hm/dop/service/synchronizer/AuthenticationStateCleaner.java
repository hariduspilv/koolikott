package ee.hm.dop.service.synchronizer;

import com.google.inject.Singleton;
import ee.hm.dop.config.guice.GuiceInjector;
import ee.hm.dop.dao.AuthenticationStateDao;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

import static java.util.concurrent.TimeUnit.DAYS;

@Singleton
public class AuthenticationStateCleaner extends DopDaemonProcess{

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationStateCleaner.class);

    @Override
    public synchronized void run() {
        try {
            beginTransaction();
            AuthenticationStateDao authenticationStateDao = newAuthenticationStateDao();
            DateTime _3hoursBefore = DateTime.now().minusHours(3);
            long allUnreviewed = authenticationStateDao.findCountOfOlderThan(_3hoursBefore);
            logger.info(String.format("Authentication State Cleaner found %s rows to remove",  allUnreviewed));

            int deleted = authenticationStateDao.deleteOlderThan(_3hoursBefore);
            logger.info(String.format("Authentication State Cleaner deleted %s rows", deleted));
            closeTransaction();
        } catch (Exception e) {
            logger.error("Unexpected error while cleaning Authentication State", e);
        } finally {
            closeEntityManager();
        }
    }

    protected AuthenticationStateDao newAuthenticationStateDao() {
        return GuiceInjector.getInjector().getInstance(AuthenticationStateDao.class);
    }

    public void scheduleExecution(int hourOfDayToExecute) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    logger.info("Starting authentication state cleaner");
                    AuthenticationStateCleaner.this.run();
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
