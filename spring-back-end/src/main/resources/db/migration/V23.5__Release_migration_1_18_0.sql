SET foreign_key_checks = 0;

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'DELETE_TAG', 'Kustuta');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'DELETE_TAG', 'Delete');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'DELETE_TAG', 'Kustuta');

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'DELETE_TAG_WAIT', 'Palun oota, võtmesõna kustutatakse');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'DELETE_TAG_WAIT', 'Please wait, tag is being deleted');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'DELETE_TAG_WAIT', 'Palun oota, võtmesõna kustutatakse');

SET foreign_key_checks = 1;