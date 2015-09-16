package ee.hm.dop.dao;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.User;

public class PortfolioDAO {

    @Inject
    private EntityManager entityManager;

    public Portfolio findById(long portfolioId) {
        return entityManager.find(Portfolio.class, portfolioId);
    }

    public List<Portfolio> findByCreator(User creator) {
        String query = "SELECT p FROM Portfolio p WHERE p.creator.id = :creatorId order by created desc";
        TypedQuery<Portfolio> findAllByCreator = entityManager.createQuery(query, Portfolio.class);
        return findAllByCreator.setParameter("creatorId", creator.getId()).getResultList();
    }
}
