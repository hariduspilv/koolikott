package ee.hm.dop.service.synchronizer;

import ee.hm.dop.service.ehis.EhisInstitutionService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Transactional
public class EhisInstitutionUpdateExecutor {

    private static final Logger logger = LoggerFactory.getLogger(EhisInstitutionUpdateExecutor.class);

    @Inject
    private EhisInstitutionService ehisInstitutionService;

    public synchronized void run() {

        try {
            logger.info("EHIS institutions updating started");
            long ehisStartOfSync = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
            List<Integer> ehisSyncInfo = ehisInstitutionService.getInstitutionsAndUpdateDb();
            long ehisEndOfSync = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
            long syncDuration = ehisEndOfSync - ehisStartOfSync;
            logger.info("EHIS institutions updating ended");
            logger.info("EHIS institution.Found - " + ehisSyncInfo.get(0) + " institutions");
            logger.info("EHIS institution.Added " + ehisSyncInfo.get(1) + " institutions into DB");
            logger.info("EHIS institution.Removed " + ehisSyncInfo.get(2) + " institutions");
            logger.info("EHIS institution sync took " + syncDuration + " seconds");

        } catch (Exception e) {
            logger.error("Unexpected error while updating EHIS institutions.", e);
        }
    }

    @Async
    @Transactional
    public synchronized void runAsync() {
        run();
    }
}
