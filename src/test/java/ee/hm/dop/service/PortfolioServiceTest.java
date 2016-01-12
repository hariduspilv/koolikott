package ee.hm.dop.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import ee.hm.dop.dao.PortfolioDAO;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.Visibility;

@RunWith(EasyMockRunner.class)
public class PortfolioServiceTest {

    @TestSubject
    private PortfolioService portfolioService = new PortfolioService();

    @Mock
    private PortfolioDAO portfolioDAO;

    @Mock
    private SearchEngineService searchEngineService;

    @Test
    public void get() {
        int portfolioId = 125;
        Portfolio portfolio = createMock(Portfolio.class);
        expect(portfolioDAO.findById(portfolioId)).andReturn(portfolio);
        expect(portfolio.getVisibility()).andReturn(Visibility.PUBLIC);

        replayAll(portfolio);

        Portfolio result = portfolioService.get(portfolioId, null);

        verifyAll(portfolio);

        assertSame(portfolio, result);
    }

    @Test
    public void incrementViewCount() {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(99L);
        Portfolio originalPortfolio = createMock(Portfolio.class);
        expect(portfolioDAO.findById(portfolio.getId())).andReturn(originalPortfolio);
        portfolioDAO.incrementViewCount(originalPortfolio);

        replayAll(originalPortfolio);

        portfolioService.incrementViewCount(portfolio);

        verifyAll(originalPortfolio);
    }

    @Test
    public void incrementViewCountPortfolioNotFound() {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(99L);
        expect(portfolioDAO.findById(portfolio.getId())).andReturn(null);

        replayAll();

        try {
            portfolioService.incrementViewCount(portfolio);
            fail("Exception expected");
        } catch (Exception e) {
            // expected
        }

        verifyAll();
    }

    private void replayAll(Object... mocks) {
        replay(portfolioDAO, searchEngineService);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

    private void verifyAll(Object... mocks) {
        verify(portfolioDAO, searchEngineService);

        if (mocks != null) {
            for (Object object : mocks) {
                verify(object);
            }
        }
    }

}
