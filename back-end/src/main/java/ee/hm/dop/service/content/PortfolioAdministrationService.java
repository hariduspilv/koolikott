package ee.hm.dop.service.content;

import ee.hm.dop.dao.PortfolioDao;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.User;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.utils.UserUtil;
import ee.hm.dop.utils.ValidatorUtil;

import javax.inject.Inject;
import java.util.List;

public class PortfolioAdministrationService {

    @Inject
    private PortfolioDao portfolioDao;
<<<<<<< HEAD
    @Inject
    private SolrEngineService solrEngineService;
=======
>>>>>>> new-develop

    public List<Portfolio> getDeletedPortfolios() {
        return portfolioDao.findDeletedPortfolios();
    }

    public Long getDeletedPortfoliosCount() {
        return portfolioDao.findDeletedPortfoliosCount();
    }

<<<<<<< HEAD
    public void restore(Portfolio portfolio, User loggedInUser) {
        UserUtil.mustBeModeratorOrAdmin(loggedInUser);

        Portfolio originalPortfolio = portfolioDao.findDeletedById(portfolio.getId());
        ValidatorUtil.mustHaveEntity(originalPortfolio);

        portfolioDao.restore(originalPortfolio);
        solrEngineService.updateIndex();
    }
=======
>>>>>>> new-develop
}
