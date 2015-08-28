package ee.hm.dop.executor;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.joda.time.LocalDateTime.now;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.joda.time.LocalDateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ee.hm.dop.model.Repository;
import ee.hm.dop.service.RepositoryService;
import ee.hm.dop.service.SearchEngineService;

@RunWith(EasyMockRunner.class)
public class SynchronizeMaterialsExecutorTest {

    @TestSubject
    private SynchronizeMaterialsExecutor synchronizeMaterialsExecutor = new SynchronizeMaterialsExecutorMock();

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private SearchEngineService searchEngineService;

    private Object lock = new Object();

    @Before
    @After
    public void stopExecutor() {
        synchronizeMaterialsExecutor.stop();
    }

    @Test
    public void synchronizeMaterials() {

        Repository repository1 = createMock(Repository.class);
        Repository repository2 = createMock(Repository.class);

        List<Repository> repositories = new ArrayList<>();
        repositories.add(repository1);
        repositories.add(repository2);

        expect(repositoryService.getAllRepositorys()).andReturn(repositories);

        searchEngineService.updateIndex();
        expectLastCall();

        repositoryService.synchronize(repository1);
        repositoryService.synchronize(repository2);

        replay(repositoryService, repository1, repository2, searchEngineService);

        synchronizeMaterialsExecutor.synchronizeMaterials();

        verify(repositoryService, repository1, repository2, searchEngineService);

        SynchronizeMaterialsExecutorMock mockExecutor = (SynchronizeMaterialsExecutorMock) synchronizeMaterialsExecutor;
        assertTrue(mockExecutor.transactionWasStarted);
        assertFalse(mockExecutor.transactionStarted);
    }

    @Test
    public void synchronizeMaterialsUnexpectedError() {
        expect(repositoryService.getAllRepositorys()).andThrow(new RuntimeException("Some error..."));

        replay(repositoryService, searchEngineService);

        synchronizeMaterialsExecutor.synchronizeMaterials();

        verify(repositoryService, searchEngineService);

        SynchronizeMaterialsExecutorMock mockExecutor = (SynchronizeMaterialsExecutorMock) synchronizeMaterialsExecutor;
        assertTrue(mockExecutor.transactionWasStarted);
        assertFalse(mockExecutor.transactionStarted);
    }

    @Test
    public void scheduleExecution() {

        Repository repository1 = createMock(Repository.class);
        Repository repository2 = createMock(Repository.class);

        List<Repository> repositories = new ArrayList<>();
        repositories.add(repository1);
        repositories.add(repository2);

        expect(repositoryService.getAllRepositorys()).andReturn(repositories);

        searchEngineService.updateIndex();
        expectLastCall();

        repositoryService.synchronize(repository1);
        repositoryService.synchronize(repository2);

        replay(repositoryService, repository1, repository2, searchEngineService);

        synchronizeMaterialsExecutor.scheduleExecution(1);

        synchronized (lock) {
            try {
                // Have to wait for the initial delay and thread execution
                lock.wait(20);
            } catch (InterruptedException e) {
            }
        }

        verify(repositoryService, repository1, repository2, searchEngineService);

        SynchronizeMaterialsExecutorMock mockExecutor = (SynchronizeMaterialsExecutorMock) synchronizeMaterialsExecutor;
        assertTrue(mockExecutor.transactionWasStarted);
        assertFalse(mockExecutor.transactionStarted);
    }

    @Test
    public void scheduleExecutionDoubleInitialization() {
        List<Repository> repositories = Collections.emptyList();
        expect(repositoryService.getAllRepositorys()).andReturn(repositories);

        replay(repositoryService);

        synchronizeMaterialsExecutor.scheduleExecution(1);
        synchronizeMaterialsExecutor.scheduleExecution(1);

        synchronized (lock) {
            try {
                // Have to wait for the initial delay and thread execution
                lock.wait(20);
            } catch (InterruptedException e) {
            }
        }

        verify(repositoryService);
    }

    @Test
    public void stop() {
        ScheduledFuture<?> synchronizeMaterialHandle = EasyMock.createMock(ScheduledFuture.class);
        expect(synchronizeMaterialHandle.cancel(false)).andReturn(true);
        synchronizeMaterialsExecutor.setSynchronizeMaterialHandle(synchronizeMaterialHandle);

        replay(repositoryService, synchronizeMaterialHandle);

        synchronizeMaterialsExecutor.stop();

        verify(repositoryService, synchronizeMaterialHandle);
    }

    @Test
    public void stopFailsInFirstAttempt() {
        ScheduledFuture<?> synchronizeMaterialHandle = createMock(ScheduledFuture.class);
        expect(synchronizeMaterialHandle.cancel(false)).andReturn(false);
        expect(synchronizeMaterialHandle.cancel(false)).andReturn(true);
        synchronizeMaterialsExecutor.setSynchronizeMaterialHandle(synchronizeMaterialHandle);

        replay(repositoryService, synchronizeMaterialHandle);

        synchronizeMaterialsExecutor.stop();

        verify(repositoryService, synchronizeMaterialHandle);
    }

    @Test
    public void getInitialDelay() {
        int hourOfDayToExecute = 1;

        SynchronizeMaterialsExecutor executor = new SynchronizeMaterialsExecutor();
        int delay = (int) executor.getInitialDelay(hourOfDayToExecute);

        LocalDateTime now = now();

        LocalDateTime expectedExecutionTime = now.withHourOfDay(hourOfDayToExecute).withMinuteOfHour(0)
                .withSecondOfMinute(0).withMillisOfSecond(0);
        if (now.getHourOfDay() >= hourOfDayToExecute) {
            expectedExecutionTime = expectedExecutionTime.plusDays(1);
        }

        LocalDateTime firstExecution = now.plusMillis(delay);

        assertTrue(Math.abs(firstExecution.toDate().getTime() - expectedExecutionTime.toDate().getTime()) < 100);
    }

    private class SynchronizeMaterialsExecutorMock extends SynchronizeMaterialsExecutor {

        private boolean transactionStarted;
        private boolean transactionWasStarted;

        @Override
        public long getInitialDelay(int hourOfDayToExecute) {
            return 1;
        }

        @Override
        protected RepositoryService newRepositoryService() {
            return repositoryService;
        };

        @Override
        protected void beginTransaction() {
            if (transactionStarted) {
                fail("Transaction already started");
            }

            transactionStarted = true;
            transactionWasStarted = true;
        };

        @Override
        protected void closeTransaction() {
            if (!transactionStarted) {
                fail("Transaction not started");
            }

            transactionStarted = false;
        };
    }
}
