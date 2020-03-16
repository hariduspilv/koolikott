package ee.hm.dop.config.db;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.stereotype.Component;

@Component
public class FlywayDbMigrator implements FlywayMigrationStrategy {

    private static final Logger logger = LoggerFactory.getLogger(FlywayDbMigrator.class);

    @Override
    public void migrate(Flyway flyway) {
        flyway.setIgnoreMissingMigrations(true);
        try {
            flyway.repair();
            flyway.migrate();
        } catch (final Exception e) {
            logger.error("Flyway migration failed, doing a repair and retrying ...");
            flyway.repair();
            flyway.migrate();
        }
    }
}
