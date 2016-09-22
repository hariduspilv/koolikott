package ee.hm.dop.dao;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import ee.hm.dop.service.RepositoryService;
import org.slf4j.LoggerFactory;

public abstract class BaseDAO<T> {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private EntityManager entityManager;

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    protected <T2> TypedQuery<T2> createQuery(String query, Class<T2> clazz) {
        return getEntityManager().createQuery(query, clazz);
    }

    protected T getSingleResult(TypedQuery<T> query) {
        T result = null;

        try {
            result = query.getSingleResult();
        } catch (NoResultException e) {
            logger.debug("Query had no results.");
        }

        return result;
    }

    protected <T2> T2 getSingleResult(TypedQuery<T2> query, Class<T2> clazz) {
        T2 result = null;

        try {
            result = query.getSingleResult();
        } catch (NoResultException e) {
            logger.debug("Query had no results.");
        }

        return result;
    }

    protected void flush() {
        getEntityManager().flush();
    }

    public T update(T entity) {
        T merged = getEntityManager().merge(entity);
        getEntityManager().persist(merged);
        return merged;
    }

    public void remove(T entity) {
        getEntityManager().remove(entity);
    }
}
