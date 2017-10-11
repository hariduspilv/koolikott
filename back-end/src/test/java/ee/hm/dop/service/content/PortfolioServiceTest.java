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
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EasyMockRunner.class)
public class PortfolioServiceTest {

    @TestSubject
    private PortfolioService portfolioService = new PortfolioService();
    @Mock
    private PortfolioDao portfolioDao;
    @Mock
    private SolrEngineService solrEngineService;
    @Mock
    private PortfolioPermission portfolioPermission;

    @Test
    public void get() {
        int portfolioId = 125;
        Portfolio portfolio = createMock(Portfolio.class);
        expect(portfolioDao.findByIdNotDeleted(portfolioId)).andReturn(portfolio);
        expect(portfolioPermission.canView(null, portfolio)).andReturn(true);

        replayAll(portfolio);

        Portfolio result = portfolioService.get(portfolioId, null);

        verifyAll(portfolio);

        assertSame(portfolio, result);
    }

    private void replayAll(Object... mocks) {
        replay(portfolioDao, solrEngineService, portfolioPermission);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

    private void verifyAll(Object... mocks) {
        verify(portfolioDao, solrEngineService, portfolioPermission);

        if (mocks != null) {
            for (Object object : mocks) {
                verify(object);
            }
        }
    }

}
