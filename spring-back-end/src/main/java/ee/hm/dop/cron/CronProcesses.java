package ee.hm.dop.cron;

import ee.hm.dop.config.cronExecutorsProperties.*;
import ee.hm.dop.service.synchronizer.*;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CronProcesses {

    private EhisInstitutionProperties ehisInstitutionProperties;
    private MaterialSyncProperties materialSyncProperties;
    private AcceptReviewableChangeProperties acceptReviewableChangeProperties;
    private AuthenticatedUserCleanerProperties authenticatedUserCleanerProperties;
    private AuthenticationStateCleanerProperties authenticationStateCleanerProperties;
    private PortfolioLogCleanerProperties portfolioLogCleanerProperties;
    private SynchronizeMaterialsExecutor synchronizeMaterialsExecutor;
    private AutomaticallyAcceptReviewableChange automaticallyAcceptReviewableChange;
    private AuthenticationStateCleaner authenticationStateCleaner;
    private AuthenticatedUserCleaner authenticatedUserCleaner;
    private EhisInstitutionUpdateExecutor ehisInstitutionUpdateExecutor;
    private PortfolioLogCleaner portfolioLogCleaner;

    @Scheduled(cron = "${cron.materialSync.scheduledTime}")
    public void synchronizeMaterialsExecutor() {
        if (materialSyncProperties.isEnabled()) {
            synchronizeMaterialsExecutor.run();
        }
    }

    @Scheduled(cron = "${cron.acceptReviewableChange.scheduledTime}")
    public void automaticallyAcceptReviewableChange() {
        if (acceptReviewableChangeProperties.isEnabled()) {
            automaticallyAcceptReviewableChange.run();
        }
    }

    @Scheduled(cron = "${cron.authenticationStateCleaner.scheduledTime}")
    public void authenticationStateCleaner() {
        if (authenticationStateCleanerProperties.isEnabled()) {
            authenticationStateCleaner.run();
        }
    }

    @Scheduled(cron = "${cron.authenticatedUserCleaner.scheduledTime}")
    public void authenticatedUserCleaner() {
        if (authenticatedUserCleanerProperties.isEnabled()) {
            authenticatedUserCleaner.run();
        }
    }

    @Scheduled(cron = "${cron.ehisInstitution.scheduledTime}")
    public void ehisInstitutionUpdateExecutor() {
        if (ehisInstitutionProperties.isEnabled()) {
            ehisInstitutionUpdateExecutor.run();
        }
    }

    @Scheduled(cron = "${cron.portfolioLogStateCleaner.scheduledTime}")
    public void portfolioLogCleaner() {
        if (portfolioLogCleanerProperties.isEnabled()) {
            portfolioLogCleaner.run();
        }
    }
}
