SET foreign_key_checks = 0;

call insert_translation('EMAIL_VALIDATION_HEADER', 'E-posti aadressi kinnitamine', 'Confirm e-mail');
call insert_translation('EMAIL_VALIDATION_DIALOG_TEXT', 'Sinu e-posti aadressile <strong>${email}</strong> on saadetud 4-kohaline kinnituskood, sisesta see siia:', 'We have sent a 4 digit code to your e-mail address <strong>${email}</strong> insert it below');
call insert_translation('EMAIL_INVALID_PIN', 'Kood ei ole õige, proovi uuesti', 'Code invalid, try again');
call insert_translation('GDPR_EMAIL_REQUIRED', 'E-mail on kohustuslik', 'E-mail is required');
call insert_translation('GDPR_EMAIL_INVALID', 'E-mail pole korrektne', 'E-mail is incorrect');
call insert_translation('GDPR_EMAIL_DUPLICATE', 'E-mail on juba kasutusel', 'E-mail already in use');
call insert_translation('GDPR_EMAIL', 'Sinu e-post', 'Your e-mail');

SET foreign_key_checks = 1;
