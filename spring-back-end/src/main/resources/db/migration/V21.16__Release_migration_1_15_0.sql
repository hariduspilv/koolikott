SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Materjal on kustutatud' WHERE translationKey = 'MATERIAL_IS_DELETED' and translationGroup = 1;
UPDATE Translation SET translation = 'Materjal on kustutatud' WHERE translationKey = 'MATERIAL_IS_DELETED' and translationGroup = 3;
UPDATE Translation SET translation = 'Materjal on kustutatud' WHERE translationKey = 'MATERIAL_IS_DELETED' and translationGroup = 2;

SET foreign_key_checks = 1;
