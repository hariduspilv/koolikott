SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Õppevara tüüp' WHERE translationGroup = 1 AND translationKey = 'DETAILED_SEARCH_RESOURCE_TYPE';

SET foreign_key_checks = 1;
