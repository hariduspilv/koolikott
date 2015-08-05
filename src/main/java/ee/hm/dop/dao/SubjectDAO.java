package ee.hm.dop.dao;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import ee.hm.dop.model.Subject;

public class SubjectDAO {

    @Inject
    private EntityManager entityManager;

    public List<Subject> findAll() {
        return entityManager.createQuery("from Subject", Subject.class).getResultList();
    }

}
