package ee.hm.dop.guice.provider;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Singleton;

/**
 * Guice provider of Entity Manager Factory.
 */
@Singleton
public class EntityManagerFactoryTestProvider extends EntityManagerFactoryProvider {

    @Override
    protected Map<String, String> getDatabaseProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put("hibernate.connection.driver_class", "org.h2.Driver");
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.hbm2ddl.auto", "create");
        properties.put("hibernate.connection.url", "jdbc:h2:mem:test;MODE=MySQL");

        return properties;
    }
}
