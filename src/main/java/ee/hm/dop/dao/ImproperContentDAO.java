package ee.hm.dop.dao;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import ee.hm.dop.model.ImproperContent;
import ee.hm.dop.model.User;

/**
 * Created by mart on 29.12.15.
 */
public class ImproperContentDAO {

    @Inject
    private EntityManager entityManager;

    public ImproperContent update(ImproperContent improperContent) {
        ImproperContent merged;
        merged = entityManager.merge(improperContent);
        entityManager.persist(merged);
        return merged;
    }

    public List<ImproperContent> getImproperPortfolios() {

        return entityManager.createQuery("FROM ImproperContent i WHERE i.portfolio IS NOT NULL AND i.deleted = false", ImproperContent.class)
                .getResultList();
    }

    public List<ImproperContent> getImproperMaterials() {
        return entityManager.createQuery("FROM ImproperContent i WHERE i.material IS NOT NULL AND i.deleted = false", ImproperContent.class)
                .getResultList();
    }

    public List<ImproperContent> findByMaterialAndUser(long materialId, User loggedInUser) {
        TypedQuery<ImproperContent> findByData = entityManager.createQuery(
                "FROM ImproperContent i WHERE i.material.id = :materialid AND i.creator = :user AND i.deleted = false", ImproperContent.class);

        List<ImproperContent> improperContents = null;
        try {
            improperContents = findByData.setParameter("materialid", materialId).setParameter("user", loggedInUser).getResultList();
        } catch (NoResultException ex) {
            // ignore
        }

        return improperContents;
    }

    public List<ImproperContent> findByPortfolioAndUser(long portfolioId, User loggedInUser) {
        TypedQuery<ImproperContent> findByData = entityManager.createQuery(
                "FROM ImproperContent i WHERE i.portfolio.id = :portfolioId AND i.creator = :user AND i.deleted = false", ImproperContent.class);

        List<ImproperContent> improperContents = null;
        try {
            improperContents = findByData.setParameter("portfolioId", portfolioId).setParameter("user", loggedInUser).getResultList();
        } catch (NoResultException ex) {
            // ignore
        }

        return improperContents;
    }

    public void deleteImproperPortfolios(Long id) {
        Query query = entityManager.createQuery("update ImproperContent i set i.deleted = true where i.portfolio.id = :id");
        query.setParameter("id", id);
        query.executeUpdate();
    }

    public void deleteImproperMaterials(Long id) {
        Query query = entityManager.createQuery("update ImproperContent i set i.deleted = true where i.material.id = :id");
        query.setParameter("id", id);
        query.executeUpdate();
    }
}
