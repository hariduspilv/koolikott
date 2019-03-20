SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Saada oma küsimus õppevara loojale/koostajale' WHERE translationKey = 'SEND_EMAIL_SPECIFY_QUESTION' AND translationGroup = 1;
UPDATE Translation SET translation = 'Saada oma küsimus õppevara loojale/koostajale' WHERE translationKey = 'SEND_EMAIL_SPECIFY_QUESTION' AND translationGroup = 2;
UPDATE Translation SET translation = 'Süsteemis puudub õppevara looja/koostaja e-post. Täiendavate küsimuste esitamiseks pöörduge kasutajatoe poole <span style="white-space:nowrap"><strong>e-koolikott@hitsa.ee</strong></span>.' WHERE translationKey = 'SEND_EMAIL_NO_CREATOR_EMAIL' AND translationGroup = 1;
UPDATE Translation SET translation = 'Süsteemis puudub õppevara looja/koostaja e-post. Täiendavate küsimuste esitamiseks pöörduge kasutajatoe poole <span style="white-space:nowrap"><strong>e-koolikott@hitsa.ee</strong></span>.' WHERE translationKey = 'SEND_EMAIL_NO_CREATOR_EMAIL' AND translationGroup = 2;

SET foreign_key_checks = 1;