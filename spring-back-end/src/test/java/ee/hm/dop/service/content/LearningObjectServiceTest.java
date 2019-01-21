package ee.hm.dop.service.content;

import ee.hm.dop.dao.LearningObjectDao;
import ee.hm.dop.model.Portfolio;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.fail;

@RunWith(EasyMockRunner.class)
public class LearningObjectServiceTest {

    @TestSubject
    private LearningObjectService learningObjectService = new LearningObjectService();
    @Mock
    private LearningObjectDao learningObjectDao;

    @Test
    public void incrementViewCount() {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(99L);
        Portfolio originalPortfolio = createMock(Portfolio.class);
        expect(learningObjectDao.findById(portfolio.getId())).andReturn(originalPortfolio);
        learningObjectDao.incrementViewCount(originalPortfolio);

        replayAll(originalPortfolio);

        learningObjectService.incrementViewCount(portfolio);

        verifyAll(originalPortfolio);
    }

    @Test
    public void incrementViewCountPortfolioNotFound() {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(99L);
        expect(learningObjectDao.findById(portfolio.getId())).andReturn(null);

        replayAll();

        try {
            learningObjectService.incrementViewCount(portfolio);
            fail("Exception expected");
        } catch (Exception e) {
            // expected
        }

        verifyAll();
    }

    private void replayAll(Object... mocks) {
        replay(learningObjectDao);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

    private void verifyAll(Object... mocks) {
        verify(learningObjectDao);

        if (mocks != null) {
            for (Object object : mocks) {
                verify(object);
            }
        }
    }
}
