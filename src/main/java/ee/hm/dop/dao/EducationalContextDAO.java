package ee.hm.dop.dao;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import ee.hm.dop.model.EducationalContext;

/**
 * Created by mart.laus on 6.08.2015.
 */
public class EducationalContextDAO {
    @Inject
    private EntityManager entityManager;

    public EducationalContext findEducationalContextByName(String name) {
        TypedQuery<EducationalContext> findByName = entityManager.createQuery(
                "SELECT e FROM EducationalContext e WHERE e.name = :name", EducationalContext.class);

        EducationalContext educationalContext = null;
        try {
            educationalContext = findByName.setParameter("name", name).getSingleResult();
        } catch (Exception e) {
            // ignore
        }

        return educationalContext;
    }

    public List<EducationalContext> findAll() {
        return entityManager.createQuery("from EducationalContext", EducationalContext.class).getResultList();
    }

}
