package ee.hm.dop.service.content;

import ee.hm.dop.dao.PortfolioDao;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.Recommendation;
import ee.hm.dop.model.User;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.utils.UserUtil;
import ee.hm.dop.utils.ValidatorUtil;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.List;

public class PortfolioAdministrationService {

    @Inject
    private PortfolioDao portfolioDao;
    @Inject
    private SolrEngineService solrEngineService;
    @Inject
    private PortfolioService portfolioService;

    public Recommendation addRecommendation(Portfolio portfolio, User loggedInUser) {
        Portfolio originalPortfolio = portfolioService.findValid(portfolio);
        UserUtil.mustBeAdmin(loggedInUser);

        Recommendation recommendation = new Recommendation();
        recommendation.setCreator(loggedInUser);
        recommendation.setAdded(DateTime.now());

        originalPortfolio.setRecommendation(recommendation);

        originalPortfolio = portfolioDao.createOrUpdate(originalPortfolio);
        solrEngineService.updateIndex();

        return originalPortfolio.getRecommendation();
    }

    public void removeRecommendation(Portfolio portfolio, User loggedInUser) {
        Portfolio originalPortfolio = portfolioService.findValid(portfolio);
        UserUtil.mustBeAdmin(loggedInUser);

        originalPortfolio.setRecommendation(null);

        portfolioDao.createOrUpdate(originalPortfolio);
        solrEngineService.updateIndex();
    }


    public List<Portfolio> getDeletedPortfolios() {
        return portfolioDao.findDeletedPortfolios();
    }

    public Long getDeletedPortfoliosCount() {
        return portfolioDao.findDeletedPortfoliosCount();
    }

    public void restore(Portfolio portfolio, User loggedInUser) {
        UserUtil.mustBeModeratorOrAdmin(loggedInUser);

        Portfolio originalPortfolio = portfolioDao.findDeletedById(portfolio.getId());
        ValidatorUtil.mustHaveEntity(originalPortfolio);

        portfolioDao.restore(originalPortfolio);
        solrEngineService.updateIndex();
    }
}
