SET foreign_key_checks = 0;

-- This actually deprecates MATERIAL_ADD_TAG
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'ADD_TAG', 'Lisa võtmesõna');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'ADD_TAG', 'Add tag');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'ADD_TAG', 'Добавить тег');

SET foreign_key_checks = 1;
