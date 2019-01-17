SET foreign_key_checks = 0;

ALTER TABLE ImproperContent
  CHANGE COLUMN added createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL;

SET foreign_key_checks = 1;