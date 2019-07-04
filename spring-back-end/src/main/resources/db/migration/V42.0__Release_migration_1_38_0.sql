SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Kogumiku versiooni eelvade seisuga' WHERE translationKey = 'LOG_MOMENT' AND translationGroup = 1;
UPDATE Translation SET translation = 'Kogumiku versiooni eelvade seisuga' WHERE translationKey = 'LOG_MOMENT' AND translationGroup = 2;
UPDATE Translation SET translation = 'Portfolio history saved on' WHERE translationKey = 'LOG_MOMENT' AND translationGroup = 3;

SET foreign_key_checks = 1;
