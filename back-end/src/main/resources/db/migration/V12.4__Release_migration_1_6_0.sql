SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Kogumiku kirjeldus' WHERE translationGroup = 1 AND translationKey = 'PORTFOLIO_SUMMARY';

SET foreign_key_checks = 1;
