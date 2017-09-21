package ee.hm.dop.service.content;

import ee.hm.dop.dao.PortfolioDao;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.Recommendation;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.Role;
import ee.hm.dop.service.solr.SolrEngineService;
import org.easymock.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.easymock.EasyMock.*;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(EasyMockRunner.class)
public class PortfolioAdministrationServiceTest {

    @TestSubject
    private PortfolioAdministrationService portfolioAdministrationService = new PortfolioAdministrationService();
    @Mock
    private PortfolioDao portfolioDao;
    @Mock
    private SolrEngineService solrEngineService;
    @Mock
    private PortfolioService portfolioService;

    @Test
    public void addRecommendation() {
        Capture<Portfolio> capturedPortfolio = newCapture();

        User user = new User();
        user.setId(111L);
        user.setRole(Role.USER);

        User admin = new User();
        admin.setId(222L);
        admin.setRole(Role.ADMIN);

        Portfolio portfolio = new Portfolio();
        portfolio.setId(1L);
        portfolio.setTitle("Super title");

        Portfolio originalPortfolio = new Portfolio();
        originalPortfolio.setId(1L);
        originalPortfolio.setTitle("Super title");
        originalPortfolio.setCreator(user);

        expect(portfolioService.findValid(portfolio)).andReturn(originalPortfolio);
        expectPortfolioUpdate(capturedPortfolio);
        solrEngineService.updateIndex();

        replayAll();

        Recommendation returnedRecommendation = portfolioAdministrationService.addRecommendation(portfolio, admin);

        verifyAll();

        Recommendation recommendation = capturedPortfolio.getValue().getRecommendation();
        assertNotNull(recommendation);
        assertEquals(admin.getId(), recommendation.getCreator().getId());
        assertEquals(recommendation, returnedRecommendation);
    }

    @Test
    public void removeRecommendation() {
        Capture<Portfolio> capturedPortfolio = newCapture();

        User user = new User();
        user.setId(111L);
        user.setRole(Role.USER);

        User admin = new User();
        admin.setId(222L);
        admin.setRole(Role.ADMIN);

        Portfolio portfolio = new Portfolio();
        portfolio.setId(1L);
        portfolio.setTitle("Super title");

        Portfolio originalPortfolio = new Portfolio();
        originalPortfolio.setId(1L);
        originalPortfolio.setTitle("Super title");
        originalPortfolio.setCreator(user);

        expect(portfolioService.findValid(portfolio)).andReturn(originalPortfolio);
        expectPortfolioUpdate(capturedPortfolio);
        solrEngineService.updateIndex();

        replayAll();

        portfolioAdministrationService.removeRecommendation(portfolio, admin);

        verifyAll();

        Recommendation recommendation = capturedPortfolio.getValue().getRecommendation();
        assertNull(recommendation);
    }

    private void expectPortfolioUpdate(Capture<Portfolio> capturedPortfolio) {
        expect(portfolioDao.createOrUpdate(EasyMock.capture(capturedPortfolio))).andAnswer(capturedPortfolio::getValue);
    }

    private void replayAll(Object... mocks) {
        replay(portfolioService, portfolioDao, solrEngineService);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

    private void verifyAll(Object... mocks) {
        verify(portfolioService, portfolioDao, solrEngineService);

        if (mocks != null) {
            for (Object object : mocks) {
                verify(object);
            }
        }
    }
}
