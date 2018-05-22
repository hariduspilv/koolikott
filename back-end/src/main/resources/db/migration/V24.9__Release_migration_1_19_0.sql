SET foreign_key_checks = 0;

ALTER TABLE User_Agreement
  ADD COLUMN agreed BOOLEAN NOT NULL,
  ADD COLUMN createdAt TIMESTAMP NOT NULL;

SET foreign_key_checks = 1;