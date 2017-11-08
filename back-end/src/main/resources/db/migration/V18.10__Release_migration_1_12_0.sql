SET foreign_key_checks = 0;

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'IMPROPER', 'Teatatud õppevara');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'IMPROPER', 'Reported learning objects');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'IMPROPER', 'Teatatud õppevara');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'DELETED', 'Kustutatud õppevara');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'DELETED', 'Deleted learning objects');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'DELETED', 'Kustutatud õppevara');

DELETE FROM Translation WHERE translationKey = 'DELETED_PORTFOLIOS';
DELETE FROM Translation WHERE translationKey = 'DELETED_MATERIALS';
DELETE FROM Translation WHERE translationKey = 'BROKEN_MATERIALS';
DELETE FROM Translation WHERE translationKey = 'IMPROPER_MATERIALS';
DELETE FROM Translation WHERE translationKey = 'IMPROPER_PORTFOLIOS';

DELETE FROM Translation WHERE translationKey = 'DASHBOARD_DELETED_PORTFOLIOS';
DELETE FROM Translation WHERE translationKey = 'DASHBOARD_DELETED_MATERIALS';
DELETE FROM Translation WHERE translationKey = 'DASHBOARD_IMRPOPER_MATERIALS';
DELETE FROM Translation WHERE translationKey = 'DASHBOARD_IMRPOPER_PORTFOLIOS';
DELETE FROM Translation WHERE translationKey = 'DASHBOARD_UNREVIEWED';
UPDATE Translation SET translationKey = 'CHANGED_LEARNING_OBJECTS' WHERE translationKey = 'DASHBOARD_CHANGED_LEARNING_OBJECTS';

SET foreign_key_checks = 1;