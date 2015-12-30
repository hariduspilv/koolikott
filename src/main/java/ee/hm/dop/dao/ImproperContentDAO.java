package ee.hm.dop.dao;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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

        return entityManager.createQuery("FROM ImproperContent i WHERE i.portfolio IS NOT NULL", ImproperContent.class)
                .getResultList();
    }

    public List<ImproperContent> getImproperMaterials() {
        return entityManager.createQuery("FROM ImproperContent i WHERE i.material IS NOT NULL", ImproperContent.class)
                .getResultList();
    }

    public List<ImproperContent> findByMaterialAndUser(long materialId, User loggedInUser) {
        TypedQuery<ImproperContent> findByData = entityManager.createQuery(
                "FROM ImproperContent i WHERE i.material.id = :materialid AND i.creator = :user", ImproperContent.class);

        List<ImproperContent> improperContents = null;
        try {
            improperContents = findByData.setParameter("materialid", materialId).setParameter("user", loggedInUser).getResultList();
        } catch (NoResultException ex) {
            // ignore
        }

        return improperContents;
    }
}
