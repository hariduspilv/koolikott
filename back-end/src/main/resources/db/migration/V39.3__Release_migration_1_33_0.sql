SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Probleemi kirjeldus (kuni 500 tähemärki)' WHERE translationKey = 'CUSTOMER_SUPPORT_MESSAGE_PLACEHOLDER' AND translationGroup = 1;
UPDATE Translation SET translation = 'Description of your issue (max. 500 characters)' WHERE translationKey = 'CUSTOMER_SUPPORT_MESSAGE_PLACEHOLDER' AND translationGroup = 3;

SET foreign_key_checks = 1;