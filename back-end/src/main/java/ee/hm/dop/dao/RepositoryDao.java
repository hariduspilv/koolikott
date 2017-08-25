package ee.hm.dop.dao;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import ee.hm.dop.model.Repository;

/**
 * Created by mart.laus on 22.07.2015.
 */
public class RepositoryDao extends AbstractDao<Repository> {

    public void updateRepository(Repository repository) {
        entityManager.merge(repository);
    }
}
