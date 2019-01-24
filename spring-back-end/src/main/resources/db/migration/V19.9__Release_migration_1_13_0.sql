SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Additional data' WHERE translationKey = 'MATERIAL_ADDITIONAL_DATA' and translationGroup = 3;

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'MATERIAL_IS_IN', 'Materjal on');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'MATERIAL_IS_IN', 'Material is in');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'MATERIAL_IS_IN', 'Materjal on');

SET foreign_key_checks = 1;
