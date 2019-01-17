SET foreign_key_checks = 0;

ALTER TABLE Material ADD COLUMN embedSource TEXT NULL;

SET foreign_key_checks = 1;