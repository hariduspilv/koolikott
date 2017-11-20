SET foreign_key_checks = 0;

ALTER TABLE ImproperContent CHANGE COLUMN creator createdBy BIGINT NULL;

SET foreign_key_checks = 1;