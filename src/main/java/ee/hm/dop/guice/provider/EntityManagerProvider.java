package ee.hm.dop.guice.provider;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Guice provider of Entity Manager. It cannot be Singleton otherwise all
 * threads will share the same Entity Manager object and therefore the same
 * transaction.
 */
public class EntityManagerProvider implements Provider<EntityManager> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final ThreadLocal<EntityManager> ENTITY_MANAGER_CACHE = new ThreadLocal<EntityManager>();

    @Inject
    private EntityManagerFactory emf;

    @Override
    public synchronized EntityManager get() {
        EntityManager entityManager = ENTITY_MANAGER_CACHE.get();
        if (entityManager == null) {
            ENTITY_MANAGER_CACHE.set(entityManager = emf.createEntityManager());
        }
        logger.info(String.format("Initializing EntityManager [%s]", entityManager));

        return entityManager;
    }
}