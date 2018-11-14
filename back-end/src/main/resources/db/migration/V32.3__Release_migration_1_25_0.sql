SET foreign_key_checks = 0;

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'SESSION_IS_EXPIRING_HEADER', 'Sessioon on aegumas');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'SESSION_IS_EXPIRING_HEADER', 'Session is expiring');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'SESSION_IS_EXPIRING_HEADER', 'Sessioon on aegumas');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'SESSION_IS_EXPIRING_MESSAGE', 'Teie sessioon lõppeb ${minRemaining} minuti pärast. Kas soovite sessiooni pikendada 2h võrra?');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'SESSION_IS_EXPIRING_MESSAGE', 'Your session is expiring in ${minRemaining} minutes. Would you like to prolong session for 2h?');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'SESSION_IS_EXPIRING_MESSAGE', 'Teie sessioon lõppeb ${minRemaining} minuti pärast. Kas soovite sessiooni pikendada 2h võrra?');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'SESSION_HAS_EXPIRED_HEADER', 'Sessioon on aegunud');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'SESSION_HAS_EXPIRED_HEADER', 'Session has expired');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'SESSION_HAS_EXPIRED_HEADER', 'Sessioon on aegunud');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'SESSION_HAS_EXPIRED_MESSAGE', 'Sessioon on aegunud, olete välja logitud.');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'SESSION_HAS_EXPIRED_MESSAGE', 'Session has expired, you have been logged out.');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'SESSION_HAS_EXPIRED_MESSAGE', 'Sessioon on aegunud, olete välja logitud.');

SET foreign_key_checks = 1;