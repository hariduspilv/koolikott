SET foreign_key_checks = 0;

INSERT IGNORE INTO LicenseType(id, name) VALUES (10, 'CCBYSA30');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'CCBYSA30', 'Autorile viitamine + Jagamine samadel tingimustel 3.0 Eesti (CC BY-SA 3.0 EE)');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'CCBYSA30', 'Attribution-ShareAlike 3.0 Estonia (CC BY-SA 3.0 EE)');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'CCBYSA30', '«Attribution-ShareAlike» («Атрибуция — На тех же условиях») 3.0 Эстония (CC BY-SA 3.0 EE)');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'LICENSETYPE_LONG_NAME_CCBYSA30', 'Autorile viitamine + Jagamine samadel tingimustel 3.0 Eesti (CC BY-SA 3.0 EE)');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'LICENSETYPE_LONG_NAME_CCBYSA30', 'Attribution-ShareAlike 3.0 Estonia (CC BY-SA 3.0 EE)');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'LICENSETYPE_LONG_NAME_CCBYSA30', '«Attribution-ShareAlike» («Атрибуция — На тех же условиях») 3.0 Эстония (CC BY-SA 3.0 EE)');

SET foreign_key_checks = 1;