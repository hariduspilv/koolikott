SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'See on näidis alampeatükk' WHERE translationKey = 'PORTFOLIO_ENTER_SUBCHAPTER_TITLE' and translationGroup = 1;
UPDATE Translation SET translation = 'This is an example subchapter' WHERE translationKey = 'PORTFOLIO_ENTER_SUBCHAPTER_TITLE' and translationGroup = 3;
UPDATE Translation SET translation = 'See on näidis alampeatükk' WHERE translationKey = 'PORTFOLIO_ENTER_SUBCHAPTER_TITLE' and translationGroup = 2;

SET foreign_key_checks = 1;