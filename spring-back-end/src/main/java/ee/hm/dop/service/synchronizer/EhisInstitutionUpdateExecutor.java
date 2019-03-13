package ee.hm.dop.service.synchronizer;

import ee.hm.dop.service.ehis.EhisInstitutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import static java.lang.String.format;

@Slf4j
@Service
@Transactional
public class EhisInstitutionUpdateExecutor {

    @Inject
    private EhisInstitutionService ehisInstitutionService;

    public synchronized void run() {

        try {
            log.info("EHIS institutions updating started");
            ehisInstitutionService.getInstitutionsAndUpdateDb();
            log.info("EHIS institutions updating ended");
        } catch (Exception e) {
            log.error("Unexpected error while updating EHIS institutions.", e);
        }
    }

    @Async
    @Transactional
    public synchronized void runAsync() {
        run();
    }
}
