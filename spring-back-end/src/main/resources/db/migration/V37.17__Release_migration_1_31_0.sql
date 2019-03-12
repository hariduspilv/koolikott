SET foreign_key_checks = 0;

call insert_translation('SEND_EMAIL_CONTENT_COUNTER', 'Sisu (jäänud ${counter} tähemärki)', 'Content (${counter} characters remaining)');

SET foreign_key_checks = 1;