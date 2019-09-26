SET foreign_key_checks = 0;

ALTER TABLE Portfolio ADD COLUMN isCopy BOOLEAN NOT NULL DEFAULT FALSE;

SET foreign_key_checks = 1;