package ee.hm.dop.utils;

import static ee.hm.dop.guice.GuiceInjector.getInjector;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class DbUtils {

    public static EntityTransaction getTransaction() {
        return getInjector().getInstance(EntityManager.class).getTransaction();
    }
}
