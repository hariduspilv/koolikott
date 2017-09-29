package ee.hm.dop.config.guice.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import ee.hm.dop.config.db.DatabaseMigrator;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

import static ee.hm.dop.utils.ConfigurationProperties.*;
import static java.lang.String.format;

/**
 * Guice provider of Entity Manager Factory.
 */
@Singleton
public class EntityManagerFactoryProvider implements Provider<EntityManagerFactory> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private Configuration configuration;

    @Inject
    private DatabaseMigrator databaseMigrator;

    private EntityManagerFactory emf;

    @Override
    public synchronized EntityManagerFactory get() {

        if (emf == null) {

            // Must be done before initiating database so schema validation does
            // not fail
            databaseMigrator.migrate();

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
        properties.put("hibernate.c3p0.idle_test_period", "400");
        properties.put("hibernate.c3p0.testConnectionOnCheckin", "true");
        properties.put("hibernate.c3p0.preferredTestQuery", "SELECT 1");
        properties.put("hibernate.c3p0.numHelperThreads", "11");
        properties.put("hibernate.cache.use_query_cache", "true");
        properties.put("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.EhCacheRegionFactory");

        properties.put("hibernate.connection.useUnicode", "true");
        properties.put("hibernate.connection.characterEncoding", "utf8");

        // Configurable options
        properties.put("hibernate.connection.url", configuration.getString(DATABASE_URL));
        properties.put("hibernate.connection.username", configuration.getString(DATABASE_USERNAME));
        properties.put("hibernate.connection.password", configuration.getString(DATABASE_PASSWORD));

        return properties;
    }
}
