SET foreign_key_checks = 0;

call update_translations('ADDED_BY', 'Lisaja', 'Added by', 'Lisaja');
call insert_translation('IMPORTED', 'imporditud', 'imported');

SET foreign_key_checks = 1;