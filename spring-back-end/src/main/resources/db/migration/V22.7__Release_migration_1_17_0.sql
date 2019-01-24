SET foreign_key_checks = 0;

UPDATE EstCoreTaxonMapping
SET nameLowercase = lower(name);

SET foreign_key_checks = 1;