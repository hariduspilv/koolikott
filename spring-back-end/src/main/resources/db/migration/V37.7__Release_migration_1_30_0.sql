SET foreign_key_checks = 0;

ALTER TABLE Taxon MODIFY COLUMN translationKey varchar(255) NOT NULL;

SET foreign_key_checks = 1;


