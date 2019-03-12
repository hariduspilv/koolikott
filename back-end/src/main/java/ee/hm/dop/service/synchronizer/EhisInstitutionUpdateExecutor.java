package ee.hm.dop.service.synchronizer;

import ee.hm.dop.service.ehis.EhisInstitutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.Timer;
import java.util.TimerTask;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.DAYS;

@Singleton
public class EhisInstitutionUpdateExecutor extends DopDaemonProcess {

    @Inject
    private EhisInstitutionService ehisInstitutionService;

    private static final Logger logger = LoggerFactory.getLogger(EhisInstitutionUpdateExecutor.class);

    @Override
    public synchronized void run() {

        try {
            beginTransaction();
            logger.info(format("EHIS institutions updating started"));
            ehisInstitutionService.getInstitutionsAndUpdateDb();
            closeTransaction();
            logger.info(format("EHIS institutions updating ended"));
        } catch (Exception e) {
            logger.error("Unexpected error while updating EHIS institutions.", e);
        }
//        closeEntityManager();
    }

    public void scheduleExecution(int hourOfDayToExecute) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    logger.info("Starting EHIS institutions synchronization process.");
                    EhisInstitutionUpdateExecutor.this.run();
                } catch (Exception e) {
                    logger.error("Unexpected error while scheduling EHIS institutions sync.", e);
                }

                logger.info("Finished EHIS institutions synchronization process.");
            }
        };

        Timer timer = new Timer();
        long initialDelay = getInitialDelay(hourOfDayToExecute);

        timer.scheduleAtFixedRate(timerTask, initialDelay, DAYS.toMillis(1));
    }
}
