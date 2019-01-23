SET foreign_key_checks = 0;

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'SYSTEM_TAG_DIALOG_TITLE', 'Süsteemne märksõna valitud');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'SYSTEM_TAG_DIALOG_TITLE', 'System tag selected');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'SYSTEM_TAG_DIALOG_TITLE', 'Система тегов выбрана');

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'SYSTEM_TAG_DIALOG_CONTENT', 'Valisid süsteemse märksõna. Vastav väärtus on lisatud materjali külge!');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'SYSTEM_TAG_DIALOG_CONTENT', 'System tag selected. Corresponding value is added to the material');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'SYSTEM_TAG_DIALOG_CONTENT', 'Системный тег не выбран. Соответствующее значение добавляется к материалу');

SET foreign_key_checks = 1;
