package ee.hm.dop.db;

import static ee.hm.dop.utils.ConfigurationProperties.DATABASE_PASSWORD;
import static ee.hm.dop.utils.ConfigurationProperties.DATABASE_URL;
import static ee.hm.dop.utils.ConfigurationProperties.DATABASE_USERNAME;

import org.apache.commons.configuration.Configuration;
import org.flywaydb.core.Flyway;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class FlywayDbMigrator implements DatabaseMigrator {

    @Inject
    private Configuration configuration;

    @Override
    public void migrate() {
        Flyway flyway = new Flyway();
        flyway.setDataSource(getUrl(), getUser(), getPassword());
        flyway.setBaselineOnMigrate(true);
        flyway.migrate();
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
