SET foreign_key_checks = 0;

ALTER TABLE Chapter
  ADD COLUMN deleted BOOLEAN NULL;

SET foreign_key_checks = 1;