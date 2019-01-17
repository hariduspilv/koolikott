SET foreign_key_checks = 0;

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'REPORTING_REASON_COMMENT', 'Kommentaar');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'REPORTING_REASON_COMMENT', 'Comment');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'REPORTING_REASON_COMMENT', '');

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'REPORTING_REASON_TAG', 'Märksõna');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'REPORTING_REASON_TAG', 'Tag');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'REPORTING_REASON_TAG', '');

SET foreign_key_checks = 1;
