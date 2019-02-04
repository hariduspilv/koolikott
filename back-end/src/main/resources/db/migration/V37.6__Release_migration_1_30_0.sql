SET foreign_key_checks = 0;

UPDATE Taxon SET Taxon.translationKey = UPPER(CONCAT(level,"_",name)) where 1=1;

SET foreign_key_checks = 1;


