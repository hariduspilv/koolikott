package ee.hm.dop;

import ee.hm.dop.service.synchronizer.AuthenticatedUserCleaner;
import ee.hm.dop.service.synchronizer.AuthenticationStateCleaner;
import ee.hm.dop.service.synchronizer.AutomaticallyAcceptReviewableChange;
import ee.hm.dop.service.synchronizer.SynchronizeMaterialsExecutor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ApplicationInit {

    private SynchronizeMaterialsExecutor synchronizeMaterialsExecutor;
    private AutomaticallyAcceptReviewableChange automaticallyAcceptReviewableChange;
    private AuthenticationStateCleaner authenticationStateCleaner;
    private AuthenticatedUserCleaner authenticatedUserCleaner;

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        synchronizeMaterialsExecutor.runAsync();
        automaticallyAcceptReviewableChange.runAsync();
        authenticationStateCleaner.runAsync();
        authenticatedUserCleaner.runAsync();
    }
}