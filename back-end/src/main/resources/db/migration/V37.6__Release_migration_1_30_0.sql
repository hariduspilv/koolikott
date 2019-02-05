SET foreign_key_checks = 0;

UPDATE Taxon SET translationKey = UPPER(CONCAT(level,"_",name));

SET foreign_key_checks = 1;


