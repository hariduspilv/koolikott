package ee.hm.dop.config.db;

import static ee.hm.dop.utils.ConfigurationProperties.DATABASE_PASSWORD;
import static ee.hm.dop.utils.ConfigurationProperties.DATABASE_URL;
import static ee.hm.dop.utils.ConfigurationProperties.DATABASE_USERNAME;

import org.apache.commons.configuration2.Configuration;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/*@Component
public class FlywayDbMigrator implements FlywayMigrationStrategy {

    private static final Logger logger = LoggerFactory.getLogger(FlywayDbMigrator.class);

    @Inject
    private Configuration configuration;

    //todo replace with defaults?
    // how important is repair + migrate?
    @Override
    public void migrate(Flyway flyway) {
        flyway.setDataSource(getUrl(), getUser(), getPassword());
        flyway.setBaselineOnMigrate(true);
        flyway.setPlaceholderReplacement(false);
        try {
            flyway.setOutOfOrder(true);
            flyway.repair();
            flyway.migrate();
        } catch (final Exception e) {
            logger.error("Flyway migration failed, doing a repair and retrying ...");
            flyway.repair();
            flyway.migrate();
        }
    }

    private String getPassword() {
        return configuration.getString(DATABASE_PASSWORD);
    }

    private String getUser() {
        return configuration.getString(DATABASE_USERNAME);
    }

    private String getUrl() {
        return configuration.getString(DATABASE_URL);
    }
}*/
