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

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'REVIEWED', 'Üle vaadatud');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'REVIEWED', 'Reviewed');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'REVIEWED', 'Üle vaadatud');

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'APPROVED', 'Heaks kiidetud');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'APPROVED', 'Approved');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'APPROVED', 'Heaks kiidetud');

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'CHANGES_ACCEPTED', 'Muudatused kinnitatud');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'CHANGES_ACCEPTED', 'Changes accepted');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'CHANGES_ACCEPTED', 'Muudatused kinnitatud');

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'CHANGES_REJECTED', 'Muudatused eemaldatud');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'CHANGES_REJECTED', 'Changes rejected');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'CHANGES_REJECTED', 'Muudatused eemaldatud');

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'PUBLICIZED', 'Avalikustatud');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'PUBLICIZED', 'Publicized');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'PUBLICIZED', 'Avalikustatud');

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'NEW', 'Uus');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'NEW', 'New');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'NEW', 'Uus');

SET foreign_key_checks = 1;
