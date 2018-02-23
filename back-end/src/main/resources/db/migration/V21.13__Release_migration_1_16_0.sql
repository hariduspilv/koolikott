SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Sulge infotekst' WHERE translationKey = 'CLOSE_INFO_TEXT' and translationGroup = 1;
UPDATE Translation SET translation = 'Close info' WHERE translationKey = 'CLOSE_INFO_TEXT' and translationGroup = 3;
UPDATE Translation SET translation = 'Sulge infotekst' WHERE translationKey = 'CLOSE_INFO_TEXT' and translationGroup = 2;

UPDATE Translation SET translation = 'Kokku' WHERE translationKey = 'TOTAL' and translationGroup = 1;
UPDATE Translation SET translation = 'Total' WHERE translationKey = 'TOTAL' and translationGroup = 3;
UPDATE Translation SET translation = 'Kokku' WHERE translationKey = 'TOTAL' and translationGroup = 2;

SET foreign_key_checks = 1;
