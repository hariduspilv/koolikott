SET foreign_key_checks = 0;

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'SHOW_ALL_CHANGES', 'Kuva kõik muudatused');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'SHOW_ALL_CHANGES', 'Show all changes');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'SHOW_ALL_CHANGES', 'Kuva kõik muudatused');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'HIDE_ALL_CHANGES', 'Peida muudatused');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'HIDE_ALL_CHANGES', 'Hide changes');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'HIDE_ALL_CHANGES', 'Peida muudatused');

SET foreign_key_checks = 1;
