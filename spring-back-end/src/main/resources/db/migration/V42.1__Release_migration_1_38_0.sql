SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Portfolio history' WHERE translationKey = 'LOG_HISTORY' AND translationGroup = 3;
UPDATE Translation SET translation = 'Preview of portfolio version saved on' WHERE translationKey = 'LOG_MOMENT' AND translationGroup = 3;

SET foreign_key_checks = 1;
