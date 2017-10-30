SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Muutunud õppevara' WHERE translationKey = 'DASHBOARD_CHANGED_LEARNING_OBJECTS' and translationGroup = 1;
UPDATE Translation SET translation = 'Changed learning objects' WHERE translationKey = 'DASHBOARD_CHANGED_LEARNING_OBJECTS' and translationGroup = 3;
UPDATE Translation SET translation = 'Muutunud õppevara' WHERE translationKey = 'DASHBOARD_CHANGED_LEARNING_OBJECTS' and translationGroup = 2;

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'TYPE', 'Tüüp');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'TYPE', 'Type');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'TYPE', 'Тип');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'CHANGE_COUNT', 'Muutusi');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'CHANGE_COUNT', 'Changes');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'CHANGE_COUNT', 'Muutusi');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'CHANGER', 'Muutja(d)');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'CHANGER', 'Changer(s)');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'CHANGER', 'Muutja(d)');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'CHANGED_AT', 'Muudetud');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'CHANGED_AT', 'Changed');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'CHANGED_AT', 'Muudetud');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'NUM_CHANGERS', '%d muutjat');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'NUM_CHANGERS', '%d changers');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'NUM_CHANGERS', '%d muutjat');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'NUM_REPORTERS', '%d teatajat');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'NUM_REPORTERS', '%d reporters');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'NUM_REPORTERS', '%d teatajat');

SET foreign_key_checks = 1;
