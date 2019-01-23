SET foreign_key_checks = 0;

ALTER TABLE Repository ADD COLUMN used BOOLEAN NULL;

SET foreign_key_checks = 1;