SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Sisesta link meedia failile' WHERE translationKey = 'ADD_MEDIA_LINK' and translationGroup = 1;
UPDATE Translation SET translation = 'Sisesta link meedia failile' WHERE translationKey = 'ADD_MEDIA_LINK' and translationGroup = 2;

SET foreign_key_checks = 1;
