SET foreign_key_checks = 0;

UPDATE Translation SET translation = '(Privaatne kogumik)' WHERE translationKey = 'PORTFOLIO_PRIVATE' AND translationGroup = 1;
UPDATE Translation SET translation = '(Private portfolio)' WHERE translationKey = 'PORTFOLIO_PRIVATE' AND translationGroup = 3;
UPDATE Translation SET translation = 'NÄITA VÄHEM' WHERE translationKey = 'PORTFOLIO_SHOW_LESS' AND translationGroup = 1;
UPDATE Translation SET translation = 'SHOW LESS' WHERE translationKey = 'PORTFOLIO_SHOW_LESS' AND translationGroup = 3;
UPDATE Translation SET translation = 'NÄITA VEEL' WHERE translationKey = 'PORTFOLIO_SHOW_MORE' AND translationGroup = 1;
UPDATE Translation SET translation = 'SHOW MORE' WHERE translationKey = 'PORTFOLIO_SHOW_MORE' AND translationGroup = 3;

SET foreign_key_checks = 1;