package ee.hm.dop.dao;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.User;

public class PortfolioDAO {

    @Inject
    private EntityManager entityManager;

    public Portfolio findById(long portfolioId) {
        TypedQuery<Portfolio> findById = entityManager.createQuery("SELECT p FROM Portfolio p WHERE p.id = :id",
                Portfolio.class);

        TypedQuery<Portfolio> query = findById.setParameter("id", portfolioId);
        return getSingleResult(query);
    }

    public List<Portfolio> findByCreator(User creator) {
        String query = "SELECT p FROM Portfolio p WHERE p.creator.id = :creatorId order by created desc";
        TypedQuery<Portfolio> findAllByCreator = entityManager.createQuery(query, Portfolio.class);
        return findAllByCreator.setParameter("creatorId", creator.getId()).getResultList();
    }

    private Portfolio getSingleResult(TypedQuery<Portfolio> query) {
        Portfolio singleResult = null;

        try {
            singleResult = query.getSingleResult();
        } catch (NoResultException ex) {
            // ignore
        }

        return singleResult;
    }
}
