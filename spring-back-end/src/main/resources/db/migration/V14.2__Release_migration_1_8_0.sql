SET foreign_key_checks = 0;

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'PICK_MATERIAL', 'Vali materjal');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'PICK_MATERIAL', 'Pick material');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'PICK_MATERIAL', 'Выберите материал');

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'REMOVE_PICKED_MATERIAL', 'Eemalda materjal');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'REMOVE_PICKED_MATERIAL', 'Remove material');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'REMOVE_PICKED_MATERIAL', 'Удалить материал');

SET foreign_key_checks = 1;
