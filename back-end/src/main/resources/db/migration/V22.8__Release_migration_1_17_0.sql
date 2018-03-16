SET foreign_key_checks = 0;

ALTER TABLE EstCoreTaxonMapping MODIFY nameLowercase VARCHAR(255) NOT NULL;

SET foreign_key_checks = 1;