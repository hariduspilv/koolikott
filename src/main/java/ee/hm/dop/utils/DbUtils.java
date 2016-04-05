package ee.hm.dop.utils;

import static ee.hm.dop.guice.GuiceInjector.getInjector;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbUtils {

    private static final Logger logger = LoggerFactory.getLogger(DbUtils.class);

    public static EntityTransaction getTransaction() {
        return getEntityManager().getTransaction();
    }

    public static EntityManager getEntityManager() {
        return getInjector().getInstance(EntityManager.class);
    }

    public static void closeEntityManager() {
        getInjector().getInstance(EntityManager.class).close();
    }

    public static void emptyCache() {
        EntityManager em = getInjector().getInstance(EntityManager.class);
        EntityTransaction transaction = em.getTransaction();
        if (transaction.isActive() && !transaction.getRollbackOnly()) {
            em.flush();
            em.clear();
        }
    }

    /**
     * Rollsback transaction if transaction is marked as rollback only. Commit
     * otherwise.
     * <p>
     * If transaction is not active anymore, nothing is done.
     */
    public static void closeTransaction() {
        EntityTransaction transaction = getTransaction();

        if (transaction.isActive()) {
            if (transaction.getRollbackOnly()) {
                transaction.rollback();
            } else {
                transaction.commit();
            }
        }
    }
}
