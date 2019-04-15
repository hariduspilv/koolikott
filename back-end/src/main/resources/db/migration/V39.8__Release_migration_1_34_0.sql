SET foreign_key_checks = 0;

call insert_translation('EMAIL_RECEIVER', 'Saaja', 'Receiver');
call insert_translation('EMAIL_LEARNINGOBJECT_TITLE', 'Õppevara pealkiri', 'Learningobject title');
call insert_translation('EMAIL_CONTENT', 'Sisu', 'Content');
call insert_translation('EMAIL_SENT_AT', 'Saadetud', 'Sent');
call insert_translation('EMAIL_SENT_EMAILS', 'Minu saadetud teated', 'My sent emails');

SET foreign_key_checks = 1;