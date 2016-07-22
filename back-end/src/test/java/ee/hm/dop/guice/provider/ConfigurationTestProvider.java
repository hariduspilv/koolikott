package ee.hm.dop.guice.provider;

import com.google.inject.Singleton;

/**
 * Guice provider of application configuration.
 */
@Singleton
public class ConfigurationTestProvider extends ConfigurationProvider {

    private static String TEST_CONFIGURATION_FILE_NAME = "test.properties";

    @Override
    protected String getCustonConfigurationFilePath() {
        return TEST_CONFIGURATION_FILE_NAME;
    }
}
