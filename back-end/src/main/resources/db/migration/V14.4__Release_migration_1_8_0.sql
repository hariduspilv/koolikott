SET foreign_key_checks = 0;

INSERT INTO LicenseType (name) VALUES ('Youtube');

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'LICENSETYPE_YOUTUBE', 'Youtube');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'LICENSETYPE_YOUTUBE', 'Youtube');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'LICENSETYPE_YOUTUBE', 'Youtube');

SET foreign_key_checks = 1;
