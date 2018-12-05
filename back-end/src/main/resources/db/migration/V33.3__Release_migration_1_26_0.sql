SET foreign_key_checks = 0;

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1,'CUSTOMER_SUPPORT_IN_CASE_OF_YES','"JAH" valiku korral avame videojuhendid sinu brauseris uuel vahelehel.');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3,'CUSTOMER_SUPPORT_IN_CASE_OF_YES','Choosing "YES" will open user manuals in a new tab');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2,'CUSTOMER_SUPPORT_IN_CASE_OF_YES','"JAH" valiku korral avame videojuhendid sinu brauseris uuel vahelehel.');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1,'CUSTOMER_SUPPORT_DID_MANUAL_HELP','Kas juhendist oli abi?');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3,'CUSTOMER_SUPPORT_DID_MANUAL_HELP','Did you find a solution from user manual');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2,'CUSTOMER_SUPPORT_DID_MANUAL_HELP','Kas juhendist oli abi?');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1,'CUSTOMER_SUPPORT_NO_NEED_MORE_HELP','Ei, küsin lisaks');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3,'CUSTOMER_SUPPORT_NO_NEED_MORE_HELP','No, more help');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2,'CUSTOMER_SUPPORT_NO_NEED_MORE_HELP','Ei, küsin lisaks');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1,'CUSTOMER_SUPPORT_YES_GOT_HELP','Jah, sain abi');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3,'CUSTOMER_SUPPORT_YES_GOT_HELP','Yes, got help');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2,'CUSTOMER_SUPPORT_YES_GOT_HELP','Jah, sain abi');

UPDATE Translation SET translation = 'Täname, oleme teie päringu kätte saanud!<br><br>Võtame teiega ühendust 1 tööpäeva jooksul <strong>${email}</strong> e-postiaadressi kaudu' WHERE translationKey = 'CUSTOMER_SUPPORT_WILL_SEND_RESPONSE' and translationGroup = 1;
UPDATE Translation SET translation = 'Thank you, we have received you question!<br><br>We will contact you in 1 working day by replying to <strong>${email}</strong>' WHERE translationKey = 'CUSTOMER_SUPPORT_WILL_SEND_RESPONSE' and translationGroup = 3;
UPDATE Translation SET translation = 'Täname, oleme teie päringu kätte saanud!<br><br>Võtame teiega ühendust 1 tööpäeva jooksul <strong>${email}</strong> e-postiaadressi kaudu' WHERE translationKey = 'CUSTOMER_SUPPORT_WILL_SEND_RESPONSE' and translationGroup = 2;




SET foreign_key_checks = 1;