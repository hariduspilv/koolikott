package ee.hm.dop.config;

import ee.hm.dop.config.db.DatabaseMigrator;
import ee.hm.dop.config.db.FlywayDbMigrator;

<<<<<<< HEAD
import ee.hm.dop.config.db.DatabaseMigrator;

public class InactiveDbMigrator implements DatabaseMigrator {
=======
public class InactiveDbMigrator extends FlywayDbMigrator {
>>>>>>> new-develop

    @Override
    public void migrate() {
        // do nothing
    }

}
