package ee.hm.dop.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public abstract class BaseDAO {

    @Inject
    private EntityManager entityManager;

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    protected <T> TypedQuery<T> createQuery(String query, Class<T> resultClass) {
        return getEntityManager().createQuery(query, resultClass);
    }

    protected <T> T getSingleResult(TypedQuery<T> query) {
        T result = null;

        try {
            result = query.getSingleResult();
        } catch (Exception e) {
            // ignore
        }

        return result;
    }
}
