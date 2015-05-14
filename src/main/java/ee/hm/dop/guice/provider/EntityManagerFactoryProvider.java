package ee.hm.dop.guice.provider;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Guice provider of data source.
 */
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
                throw new RuntimeException(String.format("Unable to initialize EntityManagerFactory [%s]!", properties), e);
            }
        }
        
        return emf;
    }
    
    private Map<String, String> getDatabaseProperties() {
    	Map<String, String> properties = new HashMap<>();
        properties.put("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.put("hibernate.show-sql", "true");
        
        // Configurable options
        properties.put("hibernate.connection.url", configuration.getString("db.url"));
        properties.put("hibernate.connection.username", configuration.getString("db.username"));
        properties.put("hibernate.connection.password", configuration.getString("db.password"));
                
        return properties;
    }
}
