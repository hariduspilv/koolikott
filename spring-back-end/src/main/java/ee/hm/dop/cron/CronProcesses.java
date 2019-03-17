package ee.hm.dop.cron;

import ee.hm.dop.service.synchronizer.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class CronProcesses {

    @Inject
    private SynchronizeMaterialsExecutor synchronizeMaterialsExecutor;
    @Inject
    private AutomaticallyAcceptReviewableChange automaticallyAcceptReviewableChange;
    @Inject
    private AuthenticationStateCleaner authenticationStateCleaner;
    @Inject
    private AuthenticatedUserCleaner authenticatedUserCleaner;
    @Inject
    private EhisInstitutionUpdateExecutor ehisInstitutionUpdateExecutor;


    @Scheduled(cron = "0 0 1 * * *")
    public void synchronizeMaterialsExecutor() {
        synchronizeMaterialsExecutor.run();
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void automaticallyAcceptReviewableChange() {
        automaticallyAcceptReviewableChange.run();
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void authenticationStateCleaner() {
        authenticationStateCleaner.run();
    }

    @Scheduled(cron = "0 0 4 * * *")
    public void authenticatedUserCleaner() {
        authenticatedUserCleaner.run();
    }

    @Scheduled(cron = "0 0 5 * * *")
    public void ehisInstitutionUpdateExecutor() {
        ehisInstitutionUpdateExecutor.run();
    }
}
