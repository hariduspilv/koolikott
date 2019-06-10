SET foreign_key_checks = 0;

call insert_translation('LEAVE_PAGE_DIALOG_HEADER', 'Muudatused on salvestamata', 'Unsaved changes');
call insert_translation('LEAVE_PAGE_DIALOG_CONFIRMATION', 'Kas oled kindel, et soovid lahkuda ilma muudatusi salvestamata?', 'Leave without saving?');
call insert_translation('LEAVE_PAGE_DIALOG_YES', 'Jah', 'Yes');
call insert_translation('LEAVE_PAGE_DIALOG_NO', 'Ei', 'No');

SET foreign_key_checks = 1;