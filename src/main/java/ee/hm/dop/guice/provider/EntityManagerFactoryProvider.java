package ee.hm.dop.guice.provider;

import static ee.hm.dop.utils.ConfigurationProperties.DATABASE_PASSWORD;
import static ee.hm.dop.utils.ConfigurationProperties.DATABASE_URL;
import static ee.hm.dop.utils.ConfigurationProperties.DATABASE_USERNAME;
import static java.lang.String.format;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * Guice provider of Entity Manager Factory.
 */
@Singleton
public class EntityManagerFactoryProvider implements Provider<EntityManagerFactory> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private Configuration configuration;

    private EntityManagerFactory emf;

    @Override
    public synchronized EntityManagerFactory get() {

        if (emf == null) {
            Map<String, String> properties = getDatabaseProperties();
            logger.info(String.format("Initializing EntityManagerFactory properties [%s]", properties));

            try {
                emf = Persistence.createEntityManagerFactory("dop", properties);
            } catch (Exception e) {
                throw new RuntimeException(format("Unable to initialize EntityManagerFactory [%s]!", properties), e);
            }
        }

        return emf;
    }

    protected Map<String, String> getDatabaseProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.put("hibernate.show_sql", "false");
        properties.put("hibernate.hbm2ddl.auto", "validate");

        properties.put("hibernate.c3p0.min_size", "5");
        properties.put("hibernate.c3p0.max_size", "100");
        properties.put("hibernate.c3p0.initialpoolsize", "5");
        properties.put("hibernate.c3p0.acquire_increment", "1");
        properties.put("hibernate.c3p0.timeout", "300");
        properties.put("hibernate.c3p0.max_statements", "50");
        properties.put("hibernate.c3p0.idle_test_period", "400");
        properties.put("hibernate.c3p0.testConnectionOnCheckin", "true");
        properties.put("hibernate.c3p0.preferredTestQuery", "SELECT 1");

        // Configurable options
        properties.put("hibernate.connection.url", configuration.getString(DATABASE_URL));
        properties.put("hibernate.connection.username", configuration.getString(DATABASE_USERNAME));
        properties.put("hibernate.connection.password", configuration.getString(DATABASE_PASSWORD));

        return properties;
    }
}
