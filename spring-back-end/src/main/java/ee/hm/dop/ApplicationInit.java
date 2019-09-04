package ee.hm.dop;

import ee.hm.dop.config.cronExecutorsProperties.AcceptReviewableChangeProperties;
import ee.hm.dop.config.cronExecutorsProperties.AuthenticatedUserCleanerProperties;
import ee.hm.dop.config.cronExecutorsProperties.AuthenticationStateCleanerProperties;
import ee.hm.dop.config.cronExecutorsProperties.EhisInstitutionProperties;
import ee.hm.dop.config.cronExecutorsProperties.GenerateSitemapXmlsExecutorProperties;
import ee.hm.dop.config.cronExecutorsProperties.MaterialSyncProperties;
import ee.hm.dop.config.cronExecutorsProperties.PortfolioLogCleanerProperties;
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

    private EhisInstitutionProperties ehisInstitutionProperties;
    private MaterialSyncProperties materialSyncProperties;
    private AcceptReviewableChangeProperties acceptReviewableChangeProperties;
    private AuthenticatedUserCleanerProperties authenticatedUserCleanerProperties;
    private AuthenticationStateCleanerProperties authenticationStateCleanerProperties;
    private GenerateSitemapXmlsExecutorProperties generateSitemapXmlsExecutorProperties;


    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        if (materialSyncProperties.isEnabled()) {
            synchronizeMaterialsExecutor.runAsync();
        }
        if (acceptReviewableChangeProperties.isEnabled()) {
            automaticallyAcceptReviewableChange.runAsync();
        }
        if (authenticationStateCleanerProperties.isEnabled()) {
            authenticationStateCleaner.runAsync();
        }
        if (authenticatedUserCleanerProperties.isEnabled()) {
            authenticatedUserCleaner.runAsync();
        }
        if (ehisInstitutionProperties.isEnabled()) {
            ehisInstitutionUpdateExecutor.runAsync();
        }
        updatePortfolioMaterialsExecutor.runAsync();
        if (generateSitemapXmlsExecutorProperties.isEnabled()) {
            generateSitemapXmlsExecutor.runAsync();
        }
    }
}