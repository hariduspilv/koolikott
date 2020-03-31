SET foreign_key_checks = 0;

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'DOMAIN_LOODUSAINED', 'Nature Study') ON DUPLICATE KEY UPDATE translation='Nature Study';
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'DOMAIN_PERFORMING_ARTS', 'Performing Arts') ON DUPLICATE KEY UPDATE translation='Performing Arts';
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'DOMAIN_INFORMATSIOONI-_JA_KOMMUNIKATSIOONITEHNOLOOGIAD', 'Information and Communication Technologies') ON DUPLICATE KEY UPDATE translation='Information and Communication Technologies';

SET foreign_key_checks = 1;