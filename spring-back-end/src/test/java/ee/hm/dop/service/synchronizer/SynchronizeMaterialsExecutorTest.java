package ee.hm.dop.service.synchronizer;

import ee.hm.dop.model.Repository;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.service.synchronizer.oaipmh.SynchronizationAudit;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.joda.time.LocalDateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.joda.time.LocalDateTime.now;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(EasyMockRunner.class)
public class SynchronizeMaterialsExecutorTest {

    @TestSubject
    private SynchronizeMaterialsExecutor synchronizeMaterialsExecutor = new SynchronizeMaterialsExecutorMock();
    @Mock
    private RepositoryService repositoryService;
    @Mock
    private SolrEngineService solrEngineService;

    private Object lock = new Object();

    @Test
    public void synchronizeMaterials() {
        Repository repository1 = createMock(Repository.class);
        Repository repository2 = createMock(Repository.class);

        List<Repository> repositories = new ArrayList<>();
        repositories.add(repository1);
        repositories.add(repository2);

        expect(repositoryService.getAllUsedRepositories()).andReturn(repositories);
        expectLastCall();

        SynchronizationAudit audit = new SynchronizationAudit();
        audit.successfullyDownloaded();
        expect(repositoryService.synchronize(repository1)).andReturn(audit);
        expect(repositoryService.synchronize(repository2)).andReturn(audit);

        replay(repositoryService, repository1, repository2);

        waitingTestFix();
        synchronizeMaterialsExecutor.run();

        verify(repositoryService, repository1, repository2);

    }

    @Test
    public void synchronizeMaterialsUnexpectedError() {
        expect(repositoryService.getAllUsedRepositories()).andThrow(new RuntimeException("Some error..."));

        replay(repositoryService);

        synchronizeMaterialsExecutor.run();

        verify(repositoryService);
    }

    @Test
    public void scheduleExecution() {
        Repository repository1 = createMock(Repository.class);
        Repository repository2 = createMock(Repository.class);

        List<Repository> repositories = new ArrayList<>();
        repositories.add(repository1);
        repositories.add(repository2);

        expect(repositoryService.getAllUsedRepositories()).andReturn(repositories);

        expectLastCall();

        SynchronizationAudit audit = new SynchronizationAudit();
        audit.successfullyDownloaded();
        expect(repositoryService.synchronize(repository1)).andReturn(audit);
        expect(repositoryService.synchronize(repository2)).andReturn(audit);
        solrEngineService.fullImport();

        replay(repositoryService, repository1, repository2, solrEngineService);

        waitingTestFix();

        verify(repositoryService, repository1, repository2, solrEngineService);

    }

    @Test
    public void index_is_not_updated_on_empty_import() {
        expect(repositoryService.getAllUsedRepositories()).andReturn(Collections.emptyList());
        expectLastCall();

        replay(repositoryService, solrEngineService);

        waitingTestFix();

        verify(repositoryService, solrEngineService);
    }


    private class SynchronizeMaterialsExecutorMock extends SynchronizeMaterialsExecutor {

        @Override
        protected RepositoryService newRepositoryService() {
            return repositoryService;
        }

    }

    private void waitingTestFix() {
        synchronized (lock) {
            try {
                // Have to wait for the initial delay and thread execution
                lock.wait(20);
            } catch (InterruptedException e) {
            }
        }
    }
}
