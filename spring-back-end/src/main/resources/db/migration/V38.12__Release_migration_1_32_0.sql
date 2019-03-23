SET foreign_key_checks = 0;

ALTER TABLE UserProfile add column customRole VARCHAR(32) NULL;

SET foreign_key_checks = 1;