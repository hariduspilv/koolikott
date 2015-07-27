package ee.hm.dop.utils;

import static ee.hm.dop.guice.GuiceInjector.getInjector;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class DbUtils {

    public static EntityTransaction getTransaction() {
        return getEntityManager().getTransaction();
    }

    public static EntityManager getEntityManager() {
        return getInjector().getInstance(EntityManager.class);
    }

    public static void closeEntityManager() {
        getInjector().getInstance(EntityManager.class).close();
    }

    /**
     * Rollsback transaction if transaction is marked as rollback only. Commit otherwise.
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
