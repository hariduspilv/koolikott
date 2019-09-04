package ee.hm.dop;

import ee.hm.dop.service.synchronizer.*;
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
    private EhisInstitutionUpdateExecutor ehisInstitutionUpdateExecutor;
    private UpdatePortfolioMaterialsExecutor updatePortfolioMaterialsExecutor;
    private GenerateSitemapXmlsExecutor generateSitemapXmlsExecutor;

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
//        synchronizeMaterialsExecutor.runAsync();
        automaticallyAcceptReviewableChange.runAsync();
        authenticationStateCleaner.runAsync();
        authenticatedUserCleaner.runAsync();
        ehisInstitutionUpdateExecutor.runAsync();
        updatePortfolioMaterialsExecutor.runAsync();
        generateSitemapXmlsExecutor.runAsync();
    }
}