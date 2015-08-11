package ee.hm.dop.dao;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import ee.hm.dop.model.EducationalContext;

/**
 * Created by mart.laus on 6.08.2015.
 */
public class EducationalContextDAO {
    @Inject
    private EntityManager entityManager;

    public List<EducationalContext> findAll() {
        return entityManager.createQuery("from EducationalContext", EducationalContext.class).getResultList();
    }
}
