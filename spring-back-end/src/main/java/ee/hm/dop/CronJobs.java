package ee.hm.dop;

import ee.hm.dop.service.synchronizer.AuthenticatedUserCleaner;
import ee.hm.dop.service.synchronizer.AuthenticationStateCleaner;
import ee.hm.dop.service.synchronizer.AutomaticallyAcceptReviewableChange;
import ee.hm.dop.service.synchronizer.SynchronizeMaterialsExecutor;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CronJobs {

    private SynchronizeMaterialsExecutor synchronizeMaterialsExecutor;
    private AutomaticallyAcceptReviewableChange automaticallyAcceptReviewableChange;
    private AuthenticationStateCleaner authenticationStateCleaner;
    private AuthenticatedUserCleaner authenticatedUserCleaner;

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
}
