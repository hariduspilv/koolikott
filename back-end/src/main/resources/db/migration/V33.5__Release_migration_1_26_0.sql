SET foreign_key_checks = 0;

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1,'CUSTOMER_SUPPORT_THANK_YOU','Täname!');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3,'CUSTOMER_SUPPORT_THANK_YOU','Thank You!');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2,'CUSTOMER_SUPPORT_THANK_YOU','Täname!');

UPDATE Translation SET translation = 'Vali teema, mille osas soovid abi küsida' WHERE translationKey = 'CUSTOMER_SUPPORT_CHOOSE_TOPIC' and translationGroup = 1;
UPDATE Translation SET translation = 'Choose a topic you need help with' WHERE translationKey = 'CUSTOMER_SUPPORT_CHOOSE_TOPIC' and translationGroup = 3;
UPDATE Translation SET translation = 'Vali teema, mille osas soovid abi küsida' WHERE translationKey = 'CUSTOMER_SUPPORT_CHOOSE_TOPIC' and translationGroup = 2;

SET foreign_key_checks = 1;