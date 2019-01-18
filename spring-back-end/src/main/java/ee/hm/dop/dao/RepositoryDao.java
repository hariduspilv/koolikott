package ee.hm.dop.dao;

import ee.hm.dop.model.Repository;

@org.springframework.stereotype.Repository
public class RepositoryDao extends AbstractDao<Repository> {

    public void updateRepository(Repository repository) {
        entityManager.merge(repository);
    }
}
