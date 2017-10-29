SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Muutunud õppevara' WHERE translationKey = 'DASHBOARD_CHANGED_LEARNING_OBJECTS' and translationGroup = 1;
UPDATE Translation SET translation = 'Changed learning objects' WHERE translationKey = 'DASHBOARD_CHANGED_LEARNING_OBJECTS' and translationGroup = 3;
UPDATE Translation SET translation = 'Muutunud õppevara' WHERE translationKey = 'DASHBOARD_CHANGED_LEARNING_OBJECTS' and translationGroup = 2;

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'NUM_CHANGERS', '%d muutjat');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'NUM_CHANGERS', '%d changers');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'NUM_CHANGERS', '%d muutjat');

SET foreign_key_checks = 1;
