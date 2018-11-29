SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Täname, oleme teie päringu kätte saanud! Võtame teiega ühendust 1 tööpäeva jooksul ${email} e-postiaadressi kaudu' WHERE translationKey = 'CUSTOMER_SUPPORT_WILL_SEND_RESPONSE' and translationGroup = 1;
UPDATE Translation SET translation = 'Thank you, we have received you question! We will contact you in 1 working day by replying to ${email}' WHERE translationKey = 'CUSTOMER_SUPPORT_WILL_SEND_RESPONSE' and translationGroup = 3;
UPDATE Translation SET translation = 'Täname, oleme teie päringu kätte saanud! Võtame teiega ühendust 1 tööpäeva jooksul ${email} e-postiaadressi kaudu' WHERE translationKey = 'CUSTOMER_SUPPORT_WILL_SEND_RESPONSE' and translationGroup = 2;

SET foreign_key_checks = 1;