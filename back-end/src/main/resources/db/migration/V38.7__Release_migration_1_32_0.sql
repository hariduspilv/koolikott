SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Andmed uuendatud' WHERE translationKey = 'USER_PROFILE_UPDATED' AND translationGroup = 1;

SET foreign_key_checks = 1;