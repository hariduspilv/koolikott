package ee.hm.dop.service.content;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.newCapture;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import ee.hm.dop.dao.PortfolioDao;
import ee.hm.dop.model.Portfolio;
<<<<<<< HEAD
import ee.hm.dop.model.Recommendation;
import ee.hm.dop.model.enums.Role;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.Visibility;
import ee.hm.dop.service.content.PortfolioService;
import ee.hm.dop.service.solr.SolrEngineService;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.IAnswer;
=======
import ee.hm.dop.service.permission.PortfolioPermission;
import ee.hm.dop.service.solr.SolrEngineService;
import org.easymock.EasyMockRunner;
>>>>>>> new-develop
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EasyMockRunner.class)
public class PortfolioServiceTest {

    @TestSubject
<<<<<<< HEAD
    private PortfolioService portfolioService = new PortfolioService();
=======
    private PortfolioGetter portfolioGetter = new PortfolioGetter();
>>>>>>> new-develop
    @Mock
    private PortfolioDao portfolioDao;
    @Mock
    private SolrEngineService solrEngineService;
<<<<<<< HEAD
=======
    @Mock
    private PortfolioPermission portfolioPermission;
>>>>>>> new-develop

    @Test
    public void get() {
        int portfolioId = 125;
        Portfolio portfolio = createMock(Portfolio.class);
        expect(portfolioDao.findByIdNotDeleted(portfolioId)).andReturn(portfolio);
<<<<<<< HEAD
        expect(portfolio.getVisibility()).andReturn(Visibility.PUBLIC);
        expect(portfolio.isDeleted()).andReturn(false);

        replayAll(portfolio);

        Portfolio result = portfolioService.get(portfolioId, null);
=======
        expect(portfolioPermission.canView(null, portfolio)).andReturn(true);

        replayAll(portfolio);

        Portfolio result = portfolioGetter.get(portfolioId, null);
>>>>>>> new-develop

        verifyAll(portfolio);

        assertSame(portfolio, result);
    }

<<<<<<< HEAD
    @Test
    public void incrementViewCount() {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(99L);
        Portfolio originalPortfolio = createMock(Portfolio.class);
        expect(portfolioDao.findById(portfolio.getId())).andReturn(originalPortfolio);
        portfolioDao.incrementViewCount(originalPortfolio);
        solrEngineService.updateIndex();

        replayAll(originalPortfolio);

        portfolioService.incrementViewCount(portfolio);

        verifyAll(originalPortfolio);
    }

    @Test
    public void incrementViewCountPortfolioNotFound() {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(99L);
        expect(portfolioDao.findById(portfolio.getId())).andReturn(null);

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
        replay(portfolioDao, solrEngineService);
=======
    private void replayAll(Object... mocks) {
        replay(portfolioDao, solrEngineService, portfolioPermission);
>>>>>>> new-develop

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

    private void verifyAll(Object... mocks) {
<<<<<<< HEAD
        verify(portfolioDao, solrEngineService);
=======
        verify(portfolioDao, solrEngineService, portfolioPermission);
>>>>>>> new-develop

        if (mocks != null) {
            for (Object object : mocks) {
                verify(object);
            }
        }
    }

}
