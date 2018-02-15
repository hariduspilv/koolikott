SET foreign_key_checks = 0;

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'TOTAL', 'Total');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'TOTAL', 'Kokku');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'TOTAL', 'Total');

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'USER_DID_REVIEW', 'Vaatas üle');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'USER_DID_REVIEW', 'Reviewed');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'USER_DID_REVIEW', 'Vaatas üle');

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'USER_DID_ADD', 'Lisas');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'USER_DID_ADD', 'Added');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'USER_DID_ADD', 'Lisas');

UPDATE Translation SET translation = 'Kustutatud' WHERE translationKey = 'DELETED' and translationGroup = 1;
UPDATE Translation SET translation = 'Deleted' WHERE translationKey = 'DELETED' and translationGroup = 3;
UPDATE Translation SET translation = 'Kustutatud' WHERE translationKey = 'DELETED' and translationGroup = 2;

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'DELETED_LEARNING_OBJECTS', 'Kustutatud õppevara');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'DELETED_LEARNING_OBJECTS', 'Deleted learning objects');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'DELETED_LEARNING_OBJECTS', 'Kustutatud õppevara');

SET foreign_key_checks = 1;
