package ee.hm.dop.cron;

import ee.hm.dop.config.cronExecutorsProperties.*;
import ee.hm.dop.service.synchronizer.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class CronProcesses {

    @Inject
    private EhisInstitutionProperties ehisInstitutionProperties;
    @Inject
    private MaterialSyncProperties materialSyncProperties;
    @Inject
    private AcceptReviewableChangeProperties acceptReviewableChangeProperties;
    @Inject
    private AuthenticatedUserCleanerProperties authenticatedUserCleanerProperties;
    @Inject
    private AuthenticationStateCleanerProperties authenticationStateCleanerProperties;
    @Inject
    private PortfolioLogCleanerProperties portfolioLogCleanerProperties;
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
    @Inject
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
