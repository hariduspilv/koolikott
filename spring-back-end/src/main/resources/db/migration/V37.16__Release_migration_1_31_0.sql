SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Süsteemis puudub õppevara looja email. Täiendavate küsimuste esitamiseks pöörduge kasutajatoe poole.' WHERE translationKey = 'SEND_EMAIL_NO_CREATOR_EMAIL' AND translationGroup = 1;
UPDATE Translation SET translation = 'Saada oma küsimus õppevara autorile' WHERE translationKey = 'SEND_EMAIL_SPECIFY_QUESTION' AND translationGroup = 1;
UPDATE Translation SET translation = 'Saada oma küsimus õppevara autorile' WHERE translationKey = 'SEND_EMAIL_SPECIFY_QUESTION' AND translationGroup = 2;

SET foreign_key_checks = 1;