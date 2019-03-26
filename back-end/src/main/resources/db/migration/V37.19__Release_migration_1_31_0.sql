SET foreign_key_checks = 0;

ALTER TABLE InstitutionEhis ADD area VARCHAR(255) NULL;
CREATE INDEX institution_ehis_id ON InstitutionEhis(ehisId);

SET foreign_key_checks = 1;