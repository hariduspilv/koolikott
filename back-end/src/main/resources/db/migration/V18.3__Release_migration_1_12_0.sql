SET foreign_key_checks = 0;

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'TYPE', 'Tüüp');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'TYPE', 'Type');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'TYPE', 'Тип');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'CHANGE_COUNT', 'Muutusi');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'CHANGE_COUNT', 'Changes');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'CHANGE_COUNT', 'Muutusi');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'CHANGER', 'Muutja(d)');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'CHANGER', 'Changer(s)');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'CHANGER', 'Muutja(d)');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'CHANGED_AT', 'Muudetud');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'CHANGED_AT', 'Changed');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'CHANGED_AT', 'Muudetud');

SET foreign_key_checks = 1;
