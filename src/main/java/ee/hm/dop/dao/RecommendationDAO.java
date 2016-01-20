package ee.hm.dop.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.joda.time.DateTime;

import ee.hm.dop.model.Material;
import ee.hm.dop.model.Recommendation;

public class RecommendationDAO {

    @Inject
    private EntityManager entityManager;

    public Recommendation findMaterialRecommendation(Material material) {
        TypedQuery<Recommendation> findReccomendation = entityManager
                .createQuery("SELECT ul FROM Reccomendation ul WHERE ul.material = :mid", Recommendation.class);

        Recommendation reccomendation = null;
        try {
            findReccomendation.setParameter("mid", material);
            reccomendation = findReccomendation.getSingleResult();
        } catch (NoResultException ex) {
            // ignore
        }

        return reccomendation;
    }

    public void deleteMaterialRecommendation(Material material) {
        Query query = entityManager.createQuery("DELETE Recommendation r WHERE r.material = :mid");
        query.setParameter("mid", material);
        query.executeUpdate();
    }

    public Recommendation update(Recommendation reccomendation) {
        if (reccomendation.getId() == null) {
            reccomendation.setAdded(DateTime.now());
        }
        Recommendation merged = entityManager.merge(reccomendation);
        entityManager.persist(merged);
        return merged;
    }

}
