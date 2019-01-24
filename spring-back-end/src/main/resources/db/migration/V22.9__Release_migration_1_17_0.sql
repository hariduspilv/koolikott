SET foreign_key_checks = 0;

CREATE INDEX EstCoreTaxonMapping_nameLowercase ON EstCoreTaxonMapping (nameLowercase);

SET foreign_key_checks = 1;