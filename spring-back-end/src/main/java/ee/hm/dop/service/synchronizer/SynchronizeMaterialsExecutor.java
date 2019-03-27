package ee.hm.dop.service.synchronizer;

import ee.hm.dop.model.Repository;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.service.synchronizer.oaipmh.SynchronizationAudit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@Service
@Transactional
public class SynchronizeMaterialsExecutor {

    @Inject
    private SolrEngineService solrEngineService;
    @Inject
    private RepositoryService repositoryService;

    public synchronized void run() {

        List<SynchronizationAudit> audits = new ArrayList<>();
        try {
            RepositoryService repositoryService = newRepositoryService();
            List<Repository> repositories = repositoryService.getAllUsedRepositories();

            log.info(format("Synchronizing %d repositories...", repositories.size()));

            for (Repository repository : repositories) {
                log.info(format("Synchonizing repository %S:", repository));
                //For every repository make a new transaction - one fail will not roll back all repositories

                SynchronizationAudit audit = repositoryService.synchronize(repository);
                if (audit != null) audits.add(audit);
            }

            log.info("Synchronization repository service finished execution.");
        } catch (Exception e) {
            log.error("Unexpected error while synchronizing materials.", e);
        } finally {
            if (audits.stream().anyMatch(SynchronizationAudit::changeOccured)) {
                log.info("Solr full import after synchronizing all materials");
                solrEngineService.fullImport();
            } else {
                log.info("Synchronizing materials doesn't need solr update");
            }
        }
    }

    protected RepositoryService newRepositoryService() {
        return repositoryService;
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public synchronized void runAsync() {
        run();
    }
}
