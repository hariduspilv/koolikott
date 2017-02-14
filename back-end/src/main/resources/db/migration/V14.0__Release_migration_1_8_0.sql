SET foreign_key_checks = 0;

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'PORTFOLIO_MAKE_PUBLIC', 'Kas soovid muuta kogumiku avalikuks?');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'PORTFOLIO_MAKE_PUBLIC', 'Do you wish to make the portfolio public?');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'PORTFOLIO_MAKE_PUBLIC', 'Вы хотите, чтобы сделать портфолио общественности?');

UPDATE Translation SET translation = 'Avalik kogumik on kõigile nähtav!' WHERE translationGroup = 1 AND translationKey = 'PORTFOLIO_WARNING';
UPDATE Translation SET translation = 'Public portfolio is visible to everybody!' WHERE translationGroup = 3 AND translationKey = 'PORTFOLIO_WARNING';
UPDATE Translation SET translation = 'Общественный портфель видна всем!' WHERE translationGroup = 2 AND translationKey = 'PORTFOLIO_WARNING';

UPDATE Translation SET translation = 'Välju ja tee avalikuks' WHERE translationGroup = 1 AND translationKey = 'PORTFOLIO_YES';
UPDATE Translation SET translation = 'Exit and make public' WHERE translationGroup = 3 AND translationKey = 'PORTFOLIO_YES';
UPDATE Translation SET translation = 'Выйдите и предает гласности' WHERE translationGroup = 2 AND translationKey = 'PORTFOLIO_YES';

UPDATE Translation SET translation = 'Välju avalikuks tegemata' WHERE translationGroup = 1 AND translationKey = 'PORTFOLIO_NO';
UPDATE Translation SET translation = 'Exit without changing' WHERE translationGroup = 3 AND translationKey = 'PORTFOLIO_NO';
UPDATE Translation SET translation = 'Выйти без изменения' WHERE translationGroup = 2 AND translationKey = 'PORTFOLIO_NO';

SET foreign_key_checks = 1;
