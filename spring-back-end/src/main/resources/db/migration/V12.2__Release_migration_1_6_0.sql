SET foreign_key_checks = 0;

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'THIS_IS_PRIVATE', 'See kogumik on privaatne');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'THIS_IS_PRIVATE', 'This portfolio is private');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'THIS_IS_PRIVATE', 'Этот портфель является частным');

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'SHARE_PRIVATE_PORTFOLIO', 'Selleks et kogumikku jagada, tuleb selle staatust muuta');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'SHARE_PRIVATE_PORTFOLIO', 'You have to change the state to share this portfolio');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'SHARE_PRIVATE_PORTFOLIO', 'Вы должны изменить состояние, чтобы поделиться этим портфелем');

SET foreign_key_checks = 1;
