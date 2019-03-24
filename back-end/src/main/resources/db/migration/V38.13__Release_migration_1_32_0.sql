SET foreign_key_checks = 0;

ALTER TABLE InstitutionEhis MODIFY COLUMN ehisId BIGINT;
ALTER TABLE InstitutionEhis MODIFY COLUMN name VARCHAR(255);

SET foreign_key_checks = 1;