package ee.hm.dop.service;

import javax.inject.Inject;

import ee.hm.dop.dao.PortfolioDAO;
import ee.hm.dop.model.Portfolio;

public class PortfolioService {

    @Inject
    private PortfolioDAO portfolioDAO;

    public Portfolio get(long materialId) {
        return portfolioDAO.findById(materialId);
    }
}
