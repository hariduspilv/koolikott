SET foreign_key_checks = 0;

call update_translations('GDPR_EMAIL_REQUIRED', 'E-post on kohustuslik', 'E-mail is required', 'E-post on kohustuslik (RU)');
call update_translations('GDPR_EMAIL_INVALID', 'E-post pole korrektne', 'E-mail is incorrect', 'E-post pole korrektne (RU)');

SET foreign_key_checks = 1;
