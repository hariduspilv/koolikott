SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Vali vähemalt üks põhjus' WHERE translationKey = 'MESSAGE_ERROR_IMPROPER_REPORT_REASON_REQUIRED' AND translationGroup = 1;
UPDATE Translation SET translation = 'Please choose at least one reason' WHERE translationKey = 'MESSAGE_ERROR_IMPROPER_REPORT_REASON_REQUIRED' AND translationGroup = 3;
UPDATE Translation SET translation = '' WHERE translationKey = 'MESSAGE_ERROR_IMPROPER_REPORT_REASON_REQUIRED' AND translationGroup = 2;

UPDATE Translation SET translation='Sobimatu sisu' WHERE translationKey = 'LO_CONTENT' and translationGroup = 1;
UPDATE Translation SET translation='Improper content' WHERE translationKey = 'LO_CONTENT' and translationGroup = 3;
UPDATE Translation SET translation='' WHERE translationKey = 'LO_CONTENT' and translationGroup = 2;

UPDATE Translation SET translation='Katkine vorm või viit' WHERE translationKey = 'LO_FORM' and translationGroup = 1;
UPDATE Translation SET translation='Broken form or link' WHERE translationKey = 'LO_FORM' and translationGroup = 3;
UPDATE Translation SET translation='' WHERE translationKey = 'LO_FORM' and translationGroup = 2;

UPDATE Translation SET translation='Puudus kirjeldavates andmetes' WHERE translationKey = 'LO_METADATA' and translationGroup = 1;
UPDATE Translation SET translation='Invalid metadata' WHERE translationKey = 'LO_METADATA' and translationGroup = 3;
UPDATE Translation SET translation='' WHERE translationKey = 'LO_METADATA' and translationGroup = 2;

SET foreign_key_checks = 1;
