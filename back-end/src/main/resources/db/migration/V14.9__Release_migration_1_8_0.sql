CREATE TABLE Version
(
  id       BIGINT PRIMARY KEY AUTO_INCREMENT,
  version  VARCHAR(50),
  released TIMESTAMP
);

SET foreign_key_checks = 0;

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'FEED_ID', 'e-Koolikott:et');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'FEED_ID', 'e-Koolikott:en');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'FEED_ID', 'e-Koolikott:ru');

INSERT INTO Translation (translationGroup, translationKey, translation)
VALUES (1, 'FEED_TITLE', 'e-Koolikott - Uudised');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'FEED_TITLE', 'e-Koolikott - News');
INSERT INTO Translation (translationGroup, translationKey, translation)
VALUES (2, 'FEED_TITLE', 'e-Koolikott - Новости');

INSERT INTO Translation (translationGroup, translationKey, translation)
VALUES (1, 'FEED_MATERIAL_TITLE', 'Uus material "%s"');
INSERT INTO Translation (translationGroup, translationKey, translation)
VALUES (3, 'FEED_MATERIAL_TITLE', 'New material "%s"');
INSERT INTO Translation (translationGroup, translationKey, translation)
VALUES (2, 'FEED_MATERIAL_TITLE', 'Новый материал "%s"');

INSERT INTO Translation (translationGroup, translationKey, translation)
VALUES (1, 'FEED_PORTFOLIO_TITLE', 'Uus portfoolio "%s"');
INSERT INTO Translation (translationGroup, translationKey, translation)
VALUES (3, 'FEED_PORTFOLIO_TITLE', 'New portfolio "%s"');
INSERT INTO Translation (translationGroup, translationKey, translation)
VALUES (2, 'FEED_PORTFOLIO_TITLE', 'Новый портфель "%s"');

INSERT INTO Translation (translationGroup, translationKey, translation)
VALUES (1, 'FEED_VERSION_TITLE', 'Uus versioon "%s"');
INSERT INTO Translation (translationGroup, translationKey, translation)
VALUES (3, 'FEED_VERSION_TITLE', 'New version "%s"');
INSERT INTO Translation (translationGroup, translationKey, translation)
VALUES (2, 'FEED_VERSION_TITLE', 'Новая версия "%s"');

SET foreign_key_checks = 1;
