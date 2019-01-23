SET foreign_key_checks = 0;

ALTER TABLE ImproperContent MODIFY creator BIGINT NULL;
ALTER TABLE BrokenContent MODIFY creator BIGINT NULL;

SET foreign_key_checks = 1;