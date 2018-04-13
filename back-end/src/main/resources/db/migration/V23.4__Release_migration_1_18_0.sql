SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Kasutaja ${user} kogumikud' WHERE translationKey = 'MYPROFILE_PAGE_TITLE_PORTFOLIOS' and translationGroup = 1;
UPDATE Translation SET translation = 'User ${user} portfolios' WHERE translationKey = 'MYPROFILE_PAGE_TITLE_PORTFOLIOS' and translationGroup = 3;

SET foreign_key_checks = 1;