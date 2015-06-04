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

    public static void clearEntityManager() {
        getEntityManager().clear();
    }
}
