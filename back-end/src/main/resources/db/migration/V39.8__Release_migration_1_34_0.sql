SET foreign_key_checks = 0;

call insert_translation('EMAIL_RECEIVER', 'Saaja', 'Receiver');
call insert_translation('EMAIL_LEARNINGOBJECT_TITLE', 'Õppevara pealkiri', 'Learningobject title');
call insert_translation('EMAIL_CONTENT', 'Sisu', 'Content');
call insert_translation('EMAIL_SENT_AT', 'Saadetud', 'Sent');
call insert_translation('EMAIL_SENT_EMAILS', 'Minu saadetud teated', 'My sent emails');
call insert_translation('EMAIL_SENT_EMAILS_CREATOR', 'Õppevara loojale/koostajale saadetud küsimus', 'Question sent to author/creator');

SET foreign_key_checks = 1;