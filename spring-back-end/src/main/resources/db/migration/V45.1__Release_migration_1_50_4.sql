SET foreign_key_checks = 0;

call insert_translation('LO_COPYRIGHT', 'Autoriõiguste rikkumine', 'Copyright infringement');
call insert_translation('LO_OTHER', 'Muu', 'Other');
call update_translations('LO_FORM', 'Katkine link', 'Broken link', 'Katkine link (RU)');
call update_translations('IMPROPER_REPORT_DESCRIPTION', 'Selgitus*', 'Explanation*', 'Selgitus* (RU)');

SET foreign_key_checks = 1;
