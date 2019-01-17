SET foreign_key_checks = 0;

UPDATE Taxon
SET nameLowercase = lower(name);

SET foreign_key_checks = 1;