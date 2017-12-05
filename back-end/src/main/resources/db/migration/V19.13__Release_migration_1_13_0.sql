SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Allikas või litsentsitüübi määramine on kohustuslik' WHERE translationKey = 'SOURCE_REQUIRED' and translationGroup = 1;

SET foreign_key_checks = 1;
