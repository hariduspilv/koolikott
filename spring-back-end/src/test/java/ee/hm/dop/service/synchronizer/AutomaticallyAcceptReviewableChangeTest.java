package ee.hm.dop.service.synchronizer;

import com.google.common.collect.Lists;
import ee.hm.dop.dao.ReviewableChangeDao;
import ee.hm.dop.model.AdminMaterial;
import ee.hm.dop.model.ReviewableChange;
import ee.hm.dop.model.enums.ReviewStatus;
import org.apache.commons.configuration2.Configuration;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import java.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

import static ee.hm.dop.utils.ConfigurationProperties.AUTOMATICALLY_ACCEPT_REVIEWABLE_CHANGES;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(EasyMockRunner.class)
public class AutomaticallyAcceptReviewableChangeTest {

    public static final LocalDateTime OLD_DATE = LocalDateTime.of(2000, 10, 10, 10, 10);
    @TestSubject
    private AutomaticallyAcceptReviewableChange automaticallyAcceptReviewableChange = new AutomaticallyAcceptReviewableChangeMock();
    @Mock
    private ReviewableChangeDao reviewableChangeDao;
    @Mock
    private Configuration configuration;

    private final Object lock = new Object();

    @Test
    public void reviewableChanges_older_than_10_days_are_accepted_automatically() throws Exception {
        ReviewableChange reviewableChange1 = new ReviewableChange();
        reviewableChange1.setCreatedAt(OLD_DATE);

        ReviewableChange reviewableChange2 = new ReviewableChange();
        reviewableChange2.setCreatedAt(LocalDateTime.now());

        ReviewableChange reviewableChange3 = new ReviewableChange();
        reviewableChange3.setCreatedAt(OLD_DATE);
        reviewableChange3.setReviewed(true);

        List<ReviewableChange> reviewableChanges = Lists.newArrayList(reviewableChange1, reviewableChange2, reviewableChange3);

        AdminMaterial material = new AdminMaterial();
        material.setReviewableChanges(reviewableChanges);

        expect(configuration.getInt(AUTOMATICALLY_ACCEPT_REVIEWABLE_CHANGES)).andReturn(10);
        expect(reviewableChangeDao.findAllUnreviewed()).andReturn(Lists.newArrayList(material));
        expect(reviewableChangeDao.createOrUpdate(reviewableChange1)).andReturn(reviewableChange1);

        replay(reviewableChangeDao, configuration);
        automaticallyAcceptReviewableChange.run();
        verify(reviewableChangeDao, configuration);

        assertEquals(OLD_DATE, reviewableChange1.getCreatedAt());
        assertEquals(true, reviewableChange1.isReviewed());
        assertNotNull(reviewableChange1.getReviewedAt());
        assertEquals(ReviewStatus.ACCEPTED_AUTOMATICALLY, reviewableChange1.getStatus());

        assertEquals(false, reviewableChange2.isReviewed());
        assertNull(reviewableChange2.getReviewedAt());
        assertNull(reviewableChange2.getStatus());

        assertEquals(OLD_DATE, reviewableChange3.getCreatedAt());
        assertEquals(true, reviewableChange3.isReviewed());
        assertNull(reviewableChange3.getReviewedAt());
        assertNull(reviewableChange3.getStatus());
    }

    @Test
    public void reviewableChanges_older_than_10_days_are_accepted_automatically_when_scheduled() throws Exception {
        ReviewableChange reviewableChange1 = new ReviewableChange();
        reviewableChange1.setCreatedAt(OLD_DATE);

        List<ReviewableChange> reviewableChanges = Lists.newArrayList(reviewableChange1);

        AdminMaterial material = new AdminMaterial();
        material.setReviewableChanges(reviewableChanges);

        expect(configuration.getInt(AUTOMATICALLY_ACCEPT_REVIEWABLE_CHANGES)).andReturn(10);
        expect(reviewableChangeDao.findAllUnreviewed()).andReturn(Lists.newArrayList(material));
        expect(reviewableChangeDao.createOrUpdate(reviewableChange1)).andReturn(reviewableChange1);

        replay(reviewableChangeDao, configuration);
        automaticallyAcceptReviewableChange.scheduleExecution(1);

        waitingTestFix();

        verify(reviewableChangeDao, configuration);

        assertEquals(OLD_DATE, reviewableChange1.getCreatedAt());
        assertEquals(true, reviewableChange1.isReviewed());
        assertNotNull(reviewableChange1.getReviewedAt());
        assertEquals(ReviewStatus.ACCEPTED_AUTOMATICALLY, reviewableChange1.getStatus());
    }

    private void waitingTestFix() {
        synchronized (lock) {
            try {
                // Have to wait for the initial delay and thread execution
                lock.wait(60);
            } catch (InterruptedException e) {
            }
        }
    }


    @Test
    public void accepting_reviewableChanges_automatically_can_be_stopped() {
        ScheduledFuture<?> acceptReviewableChangeHandle = EasyMock.createMock(ScheduledFuture.class);
        expect(acceptReviewableChangeHandle.cancel(false)).andReturn(true);
        automaticallyAcceptReviewableChange.setReviewableChangeHandle(acceptReviewableChangeHandle);

        replay(reviewableChangeDao, acceptReviewableChangeHandle);
        automaticallyAcceptReviewableChange.stop();
        verify(reviewableChangeDao, acceptReviewableChangeHandle);
    }

    private class AutomaticallyAcceptReviewableChangeMock extends AutomaticallyAcceptReviewableChange {
        @Override
        protected ReviewableChangeDao newReviewableChangeDao() {
            return reviewableChangeDao;
        }

        @Override
        public long getInitialDelay(int hourOfDayToExecute) {
            return 1;
        }

        @Override
        protected void beginTransaction() {
        }

        @Override
        protected void closeTransaction() {
        }

        @Override
        protected void closeEntityManager() {
        }
    }
}
