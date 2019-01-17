SET foreign_key_checks = 0;

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'MATERIALS_FIRST', 'Materjalid eespool');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'MATERIALS_FIRST', 'Materials first');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'MATERIALS_FIRST', 'Materials first');

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'PORTFOLIOS_FIRST', 'Kogumikud eespool');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'PORTFOLIOS_FIRST', 'Portfolios first');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'PORTFOLIOS_FIRST', 'Portfolios first');

UPDATE Translation set translation = 'Hiljuti loodud eespool' WHERE translationKey = 'ADDED_DATE_DESC' and translationGroup = 1;
UPDATE Translation set translation = 'Varem loodud eespool' WHERE translationKey = 'ADDED_DATE_ASC' and translationGroup = 1;

SET foreign_key_checks = 1;