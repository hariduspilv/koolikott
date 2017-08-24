package ee.hm.dop.db;

import static ee.hm.dop.utils.ConfigurationProperties.DATABASE_PASSWORD;
import static ee.hm.dop.utils.ConfigurationProperties.DATABASE_URL;
import static ee.hm.dop.utils.ConfigurationProperties.DATABASE_USERNAME;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.configuration.Configuration;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class FlywayDbMigrator implements DatabaseMigrator {

    private static final Logger logger = LoggerFactory.getLogger(FlywayDbMigrator.class);

    @Inject
    private Configuration configuration;

    @Override
    public void migrate() {
        Flyway flyway = new Flyway();
        flyway.setDataSource(getUrl(), getUser(), getPassword());
        flyway.setBaselineOnMigrate(true);
        try {
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
}
