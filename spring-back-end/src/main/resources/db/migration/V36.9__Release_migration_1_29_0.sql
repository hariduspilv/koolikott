SET foreign_key_checks = 0;

call insert_translation('ALLOWED 3 FILES', 'Lubatud kuni 3 faili', 'Allowed max 3 files');
call insert_translation('FILES_MORE_THAN_10MB', 'Lubatud kuni 10MB', 'Allowed up to 10MB');

SET foreign_key_checks = 1;
