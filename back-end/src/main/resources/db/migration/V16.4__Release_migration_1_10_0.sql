SET foreign_key_checks = 0;

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'BUTTON_REVIEW', 'Märgi ülevaadatuks');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'BUTTON_REVIEW', 'Mark as reviewed');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'BUTTON_REVIEW', '');

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'ERROR_MSG_UNREVIEWED', 'See õppevara on üle vaatamata');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'ERROR_MSG_UNREVIEWED', 'This learning object has not been reviewed');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'ERROR_MSG_UNREVIEWED', '');

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'ERROR_MSG_UNREVIEWED_PORTFOLIO', 'See kogumik on üle vaatamata');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'ERROR_MSG_UNREVIEWED_PORTFOLIO', 'This portfolio has not been reviewed');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'ERROR_MSG_UNREVIEWED_PORTFOLIO', '');

SET foreign_key_checks = 1;
