SET foreign_key_checks = 0;

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'SOURCE_BIG', 'Allikas');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'SOURCE_BIG', 'Source');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'SOURCE_BIG', 'Allikas');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'I_AM_AUTHOR', 'Olen ise autor');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'I_AM_AUTHOR', 'I am the author');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'I_AM_AUTHOR', 'Olen ise autor');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'AUTHOR_WORD', 'Autor');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'AUTHOR_WORD', 'Author');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'AUTHOR_WORD', 'Autor');

SET foreign_key_checks = 1;
