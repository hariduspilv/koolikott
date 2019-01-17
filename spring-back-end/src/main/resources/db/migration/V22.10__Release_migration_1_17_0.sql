SET foreign_key_checks = 0;

ALTER TABLE Taxon
    ADD nameLowercase  VARCHAR(255) NULL;

SET foreign_key_checks = 1;