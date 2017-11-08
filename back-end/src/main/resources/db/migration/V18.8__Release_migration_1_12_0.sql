SET foreign_key_checks = 0;

ALTER TABLE  ImproperContent DROP COLUMN needs_rr;

DROP TABLE IF EXISTS BrokenContent;

SET foreign_key_checks = 1;