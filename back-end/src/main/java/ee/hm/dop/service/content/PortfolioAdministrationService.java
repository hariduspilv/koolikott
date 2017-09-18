package ee.hm.dop.service.content;

import ee.hm.dop.dao.PortfolioDao;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.User;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.utils.UserUtil;

import javax.inject.Inject;
import java.util.List;

public class PortfolioAdministrationService {

    @Inject
    private PortfolioDao portfolioDao;
    @Inject
    private SolrEngineService solrEngineService;

    public List<Portfolio> getDeletedPortfolios() {
        return portfolioDao.findDeletedPortfolios();
    }

    public Long getDeletedPortfoliosCount() {
        return portfolioDao.findDeletedPortfoliosCount();
    }

    public void restore(Portfolio portfolio, User loggedInUser) {
        UserUtil.mustBeModeratorOrAdmin(loggedInUser);

        Portfolio originalPortfolio = portfolioDao.findDeletedById(portfolio.getId());
        validateEntity(originalPortfolio);

        portfolioDao.restore(originalPortfolio);
        solrEngineService.updateIndex();
    }

    private void validateEntity(Portfolio originalPortfolio) {
        if (originalPortfolio == null) {
            throw new RuntimeException("Portfolio not found");
        }
    }
}
