SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Ei leitud' WHERE translationKey = 'NO_USERS_FOUND' and translationGroup = 1;
UPDATE Translation SET translation = 'Ei leitud' WHERE translationKey = 'NO_USERS_FOUND' and translationGroup = 2;

SET foreign_key_checks = 1;