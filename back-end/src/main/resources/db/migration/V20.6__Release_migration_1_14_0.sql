SET foreign_key_checks = 0;

UPDATE Translation SET translation = "Kuni" WHERE translationKey = 'SIZE_UNTIL' and translationGroup = 1;

SET foreign_key_checks = 1;
