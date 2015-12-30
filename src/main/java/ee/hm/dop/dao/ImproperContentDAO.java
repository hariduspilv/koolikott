package ee.hm.dop.dao;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import ee.hm.dop.model.ImproperContent;

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

    public List<ImproperContent> findImproperPortfolios() {

        return entityManager.createQuery("FROM ImproperContent i WHERE i.portfolio IS NOT NULL", ImproperContent.class)
                .getResultList();
    }
}
