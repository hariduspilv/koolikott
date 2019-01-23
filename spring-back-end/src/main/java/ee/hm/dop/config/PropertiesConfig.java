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
                new ConfigurationPropertySource("default properties", new Configurations().properties("default.properties"));
        ConfigurationPropertySource customProps =
                new ConfigurationPropertySource("custom properties", new Configurations().properties(System.getProperty("config")));
        log.info("loaded default props : " + defaultProps.getPropertyNames().length);
        log.info("loaded custom props : " + customProps.getPropertyNames().length);
        env.getPropertySources().addFirst(customProps);
        env.getPropertySources().addLast(defaultProps);
    }
}