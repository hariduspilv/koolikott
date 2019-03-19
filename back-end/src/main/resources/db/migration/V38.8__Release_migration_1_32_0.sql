SET foreign_key_checks = 0;

ALTER TABLE UserProfile MODIFY COLUMN role VARCHAR(60);

SET foreign_key_checks = 1;