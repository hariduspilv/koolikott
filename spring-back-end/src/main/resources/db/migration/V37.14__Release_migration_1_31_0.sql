SET foreign_key_checks = 0;

call insert_translation('SEND_EMAIL_SPECIFY_QUESTION', 'Saada oma täpsustav küsimus õppevara autorile', 'Contact author/creator');
call insert_translation('SEND_EMAIL_MESSAGE_SENT', 'Sinu kiri on saadetud!', 'Your message has sent!');
call insert_translation('SEND_EMAIL_CONTENT', 'Sisu (maksimaalselt 500 tähemärki)', 'Content (maximum of 500 characters)');
call insert_translation('SEND_EMAIL_NO_CREATOR_EMAIL', 'Süsteemis puudub õppevara looja email. Täiendavate küsimuste esitamiseks pöörduge kasutajatoe poole','Creator does not have an e-mail address in e-Koolikott. For further assistance contact customer support');

SET foreign_key_checks = 1;