SET foreign_key_checks = 0;

ALTER TABLE Terms
ADD COLUMN deleted BOOLEAN DEFAULT FALSE;

SET foreign_key_checks = 1;