SET foreign_key_checks = 0;

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'TOTAL', 'Total');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'TOTAL', 'Kokku');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'TOTAL', 'Total');

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'USER_DID_REVIEW', 'Vaatas üle');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'USER_DID_REVIEW', 'Reviewed');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'USER_DID_REVIEW', 'Vaatas üle');

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'USER_DID_ADD', 'Lisas');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'USER_DID_ADD', 'Added');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'USER_DID_ADD', 'Lisas');

SET foreign_key_checks = 1;
