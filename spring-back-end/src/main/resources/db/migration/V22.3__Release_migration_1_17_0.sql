SET foreign_key_checks = 0;

ALTER TABLE Repository MODIFY used BOOLEAN NOT NULL;

SET foreign_key_checks = 1;