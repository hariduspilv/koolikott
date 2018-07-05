package ee.hm.dop.config.guice.provider;

import com.google.inject.Provider;
import com.google.inject.Singleton;
import ee.hm.dop.utils.DOPFileUtils;
import org.apache.commons.configuration2.*;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;

import static java.lang.String.format;

/**
 * Guice provider of application configuration.
 */
@Singleton
public class ConfigurationProvider implements Provider<Configuration> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private CompositeConfiguration configuration;

    @Override
    public synchronized Configuration get() {

        if (configuration == null) {
            init();
        }

        return configuration;
    }

    private void init() {
        configuration = new CompositeConfiguration();

        Configuration customConfiguration = loadCustomConfiguration();
        if (customConfiguration != null) {
            configuration.addConfiguration(customConfiguration);
        }

        configuration.addConfiguration(loadDefaultConfiguration());
    }

    private Configuration loadCustomConfiguration() {

        String configurationPath = getCustomConfigurationFilePath();
        if (configurationPath != null) {
            logger.info(format("Loading custom configuration file from [%s].", configurationPath));

            try {
                File config = DOPFileUtils.getFile(configurationPath);
                Configuration configuration =
                        new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                                .configure(new Parameters().properties()
                                        .setFileName(configurationPath))
                                .getConfiguration();
                logger.info(format("Custom configuration loaded from [%s]", config.getAbsolutePath()));

                return configuration;
            } catch (Exception e) {
                throw new RuntimeException("Unable to load custom configuration!", e);
            }
        } else {
            logger.info("No custom configuration file set.");
        }
        return null;

    }

    private Configuration loadDefaultConfiguration() {
        try {
            PropertiesConfiguration config =
                    new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                            .configure(new Parameters().properties()
                                    .setFileName(getConfigurationFileName()))
                            .getConfiguration();

            URL resource = getClass().getClassLoader().getResource(getConfigurationFileName());
            logger.info(String.format("Default configuration loaded from [%s]", resource != null ? resource.toExternalForm() : null));
            return config;
        } catch (Exception e) {
            throw new RuntimeException("Unable to load default configuration!", e);
        }
    }

    private String getConfigurationFileName() {
        return "default.properties";
    }

    protected String getCustomConfigurationFilePath() {
        return System.getProperty("config");
    }
}
