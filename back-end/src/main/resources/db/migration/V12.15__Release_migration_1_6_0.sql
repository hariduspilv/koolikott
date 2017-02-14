SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Tühista' WHERE translationGroup = 1 AND translationKey = 'TOOLBAR_ADD_BACK';
UPDATE Translation SET translation = 'Cancel' WHERE translationGroup = 3 AND translationKey = 'TOOLBAR_ADD_BACK';
UPDATE Translation SET translation = 'Отставить' WHERE translationGroup = 2 AND translationKey = 'TOOLBAR_ADD_BACK';

SET foreign_key_checks = 1;
