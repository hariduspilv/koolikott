SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'See on näidispeatükk' WHERE translationKey = 'PORTFOLIO_ENTER_CHAPTER_TITLE' and translationGroup = 1;
UPDATE Translation SET translation = 'This is an example chapter' WHERE translationKey = 'PORTFOLIO_ENTER_CHAPTER_TITLE' and translationGroup = 3;
UPDATE Translation SET translation = 'See on näidispeatükk' WHERE translationKey = 'PORTFOLIO_ENTER_CHAPTER_TITLE' and translationGroup = 2;

SET foreign_key_checks = 1;