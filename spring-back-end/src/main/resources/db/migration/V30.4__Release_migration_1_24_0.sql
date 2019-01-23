SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'HITSA lehelt' WHERE translationKey = 'USER_GUIDE_HITSA_PAGE' AND translationGroup = 1;
UPDATE Translation SET translation = 'HITSA page' WHERE translationKey = 'USER_GUIDE_HITSA_PAGE' AND translationGroup = 2;
UPDATE Translation SET translation = 'HITSA lehelt' WHERE translationKey = 'USER_GUIDE_HITSA_PAGE' AND translationGroup = 3;

SET foreign_key_checks = 1;