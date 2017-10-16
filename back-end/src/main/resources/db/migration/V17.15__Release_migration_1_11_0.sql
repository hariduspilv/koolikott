SET foreign_key_checks = 0;

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'MESSAGE_ERROR_IMPROPER_REPORT_REASON_REQUIRED', 'Vali vähemalt üks põhjus');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'MESSAGE_ERROR_IMPROPER_REPORT_REASON_REQUIRED', 'Please choose at least one reason');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'MESSAGE_ERROR_IMPROPER_REPORT_REASON_REQUIRED', '');

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'LO_CONTENT', 'Sobimatu sisu');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'LO_CONTENT', 'Improper content');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'LO_CONTENT', '');

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'LO_FORM', 'Katkine vorm või viit');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'LO_FORM', 'Broken form or link');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'LO_FORM', '');

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'LO_METADATA', 'Puudus kirjeldavates andmetes');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'LO_METADATA', 'Invalid metadata');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'LO_METADATA', '');

SET foreign_key_checks = 1;
