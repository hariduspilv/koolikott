SET foreign_key_checks = 0;

ALTER TABLE User ADD COLUMN location TEXT NULL;

SET foreign_key_checks = 1;