package ee.hm.dop.guice.provider;

import java.net.URL;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * Guice provider of application configuration.
 */
@Singleton
public class ConfigurationProvider implements Provider<Configuration> {

    private static Logger logger = LoggerFactory.getLogger(ConfigurationProvider.class);
    private static String DEFAULT_CONFIGURATION_FILE_NAME = "default.properties";

    private Configuration configuration;

    @Override
    public synchronized Configuration get() {

        if (configuration == null) {
            try {
                URL resource = getClass().getClassLoader().getResource(DEFAULT_CONFIGURATION_FILE_NAME);
                configuration = new PropertiesConfiguration(resource);
                logger.info(String.format("Default configuration loaded from [%s]", resource.toExternalForm()));
            } catch (Exception e) {
                throw new RuntimeException("Unable to load default configuration!", e);
            }
        }

        return configuration;
    }
}
