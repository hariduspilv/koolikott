package ee.hm.dop.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.spring.ConfigurationPropertySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

@Slf4j
@Configuration
public class PropertiesConfig {

    @Autowired
    public void propertySourcesPlaceholderConfigurer(ConfigurableEnvironment env) throws ConfigurationException {
        ConfigurationPropertySource defaultProps =
                new ConfigurationPropertySource("test properties", new Configurations().properties("test.properties"));
        log.info("loaded default props : " + defaultProps.getPropertyNames().length);
        env.getPropertySources().addLast(defaultProps);
    }
}