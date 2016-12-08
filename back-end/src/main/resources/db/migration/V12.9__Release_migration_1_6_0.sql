SET foreign_key_checks = 0;

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'MATERIAL_FROM_BAG', 'Koolikotist');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'MATERIAL_FROM_BAG', 'From bag');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'MATERIAL_FROM_BAG', '');

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'NEW_MATERIAL', 'Uus');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'NEW_MATERIAL', 'New');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'NEW_MATERIAL', '');

SET foreign_key_checks = 1;
