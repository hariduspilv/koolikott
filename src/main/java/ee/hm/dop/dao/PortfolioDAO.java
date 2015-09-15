package ee.hm.dop.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import ee.hm.dop.model.Portfolio;

public class PortfolioDAO {

    @Inject
    private EntityManager entityManager;

    public Portfolio findById(long portfolioId) {
        return entityManager.find(Portfolio.class, portfolioId);
    }
}
