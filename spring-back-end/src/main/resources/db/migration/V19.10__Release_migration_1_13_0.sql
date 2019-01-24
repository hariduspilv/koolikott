SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Eesnimi' WHERE translationKey = 'MATERIAL_AUTHOR_NAME' and translationGroup = 1;
UPDATE Translation SET translation = 'Name' WHERE translationKey = 'MATERIAL_AUTHOR_NAME' and translationGroup = 3;

UPDATE Translation SET translation = 'Perekonnanimi' WHERE translationKey = 'MATERIAL_AUTHOR_SURNAME' and translationGroup = 1;
UPDATE Translation SET translation = 'Surname' WHERE translationKey = 'MATERIAL_AUTHOR_SURNAME' and translationGroup = 3;

UPDATE Translation SET translation = 'väljaandja' WHERE translationKey = 'MATERIAL_PUBLISHER' and translationGroup = 1;
UPDATE Translation SET translation = 'publisher' WHERE translationKey = 'MATERIAL_PUBLISHER' and translationGroup = 3;

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'MATERIAL_SOURCE_EMPTY', 'Materjalil peab olema kas link või fail');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'MATERIAL_SOURCE_EMPTY', 'Material has to have link or file');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'MATERIAL_SOURCE_EMPTY', 'Materjalil peab olema kas link või fail');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'IS_FILE', 'on fail');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'IS_FILE', 'is file');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'IS_FILE', 'on fail');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'CHANGE', 'muuda');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'CHANGE', 'muuda');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'CHANGE', 'muuda');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'AUTHOR', 'Autor');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'AUTHOR', 'Author');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'AUTHOR', 'Autor');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'AND_OR', 'ja/või');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'AND_OR', 'and/or');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'AND_OR', 'ja/või');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'MATERIAL_PUBLISHER_SOURCE', 'Kirjastus, allikas vmt');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'MATERIAL_PUBLISHER_SOURCE', 'Publisher, source or else');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'MATERIAL_PUBLISHER_SOURCE', 'Kirjastus, allikas vmt');

SET foreign_key_checks = 1;
