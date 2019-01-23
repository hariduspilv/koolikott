SET foreign_key_checks = 0;

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'AUTHOR_NAME_REQUIRED', 'Autori nimi on kohustuslik');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'AUTHOR_NAME_REQUIRED', 'Authors name is required');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'AUTHOR_NAME_REQUIRED', 'Autori nimi on kohustuslik');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'SOURCE_REQUIRED', 'Allikas on kohustuslik');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'SOURCE_REQUIRED', 'Source is required');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'SOURCE_REQUIRED', 'Allikas on kohustuslik');

SET foreign_key_checks = 1;
