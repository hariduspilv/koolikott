package ee.hm.dop.config;

import ee.hm.dop.config.db.DatabaseMigrator;
import ee.hm.dop.config.db.FlywayDbMigrator;

public class InactiveDbMigrator extends FlywayDbMigrator {

    @Override
    public void migrate() {
        // do nothing
    }

}
