SET foreign_key_checks = 0;

call insert_translation('TERMS_TAB', 'Kasutustingimused', 'Terms and conditions');
call insert_translation('TERMS_HEADER', 'Kasutustingimused', 'Terms and conditions');
call insert_translation('ADD_TERMS', 'Lisa kasutustingimus', 'Add Terms and conditions');
call insert_translation('ARE_YOU_SURE_CANCEL', 'Kas oled kindel, et tahad katkestada', 'Are you sure you want to cancel');
call insert_translation('GDPR_NOTIFY_FAILED', 'GDPR versiooni uuendus ebaõnnestus ', 'Failed to update GDPR version');
UPDATE Translation SET translation = 'Täisekraanist väljumiseks vajuta X nupule või klaviatuuril Esc-klahvi' WHERE translationKey = 'YOU_CAN_LEAVE_PAGE_WITH_ESC' AND translationGroup = 1;
UPDATE Translation SET translation = 'Täisekraanist väljumiseks vajuta X nupule või klaviatuuril Esc-klahvi (RU)' WHERE translationKey = 'YOU_CAN_LEAVE_PAGE_WITH_ESC' AND translationGroup = 2;
UPDATE Translation SET translation = 'To exit fullscreen mode use ESC or click the X button' WHERE translationKey = 'YOU_CAN_LEAVE_PAGE_WITH_ESC' AND translationGroup = 3;

SET foreign_key_checks = 1;


