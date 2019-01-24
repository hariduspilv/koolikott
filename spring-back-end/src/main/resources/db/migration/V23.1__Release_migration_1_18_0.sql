SET foreign_key_checks = 0;

ALTER TABLE Portfolio
ADD publishedAt TIMESTAMP NULL;

SET foreign_key_checks = 1;
