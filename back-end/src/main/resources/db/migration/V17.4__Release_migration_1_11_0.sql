SET foreign_key_checks = 0;

ALTER TABLE ImproperContent
  ADD reportingReason VARCHAR(255) NULL,
  CHANGE COLUMN reason reportingText VARCHAR(255) NULL;

SET foreign_key_checks = 1;