package ee.hm.dop.dao;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import ee.hm.dop.model.Repository;

/**
 * Created by mart.laus on 22.07.2015.
 */
public class RepositoryDAO {

    @Inject
    private EntityManager entityManager;

    public List<Repository> findAll() {
        return entityManager.createQuery("from Repository", Repository.class).getResultList();
    }

    public void updateRepository(Repository repository) {
        entityManager.merge(repository);
    }
}
