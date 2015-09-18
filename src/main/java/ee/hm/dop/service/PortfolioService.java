package ee.hm.dop.service;

import java.util.List;

import javax.inject.Inject;

import ee.hm.dop.dao.PortfolioDAO;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.User;

public class PortfolioService {

    @Inject
    private PortfolioDAO portfolioDAO;

    public Portfolio get(long materialId) {
        return portfolioDAO.findById(materialId);
    }

    public List<Portfolio> getByCreator(User creator) {
        return portfolioDAO.findByCreator(creator);
    }

    public byte[] getPortfolioPicture(Portfolio portfolio) {
        return portfolioDAO.findPictureByPortfolio(portfolio);
    }

    public void incrementViewCount(Portfolio portfolio) {
        portfolioDAO.incrementViewCount(portfolio);
    }
}
