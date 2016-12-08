SET foreign_key_checks = 0;

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'MATERIAL', 'Material');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'MATERIAL', 'материал');

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'SUBCHAPTER', 'Subchapter');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'SUBCHAPTER', '');

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'CHAPTER', 'Peatükk');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'CHAPTER', 'Chapter');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'CHAPTER', '');

SET foreign_key_checks = 1;
