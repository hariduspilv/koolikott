SET foreign_key_checks = 0;

call update_translations('LICENSE_AGREEMENT_DIALOG_ACTION_EXIT', 'Väljun', 'Exit', 'ВЫЙТИ');
call update_translations('LICENSE_AGREEMENT_DIALOG_ACTION_NO', 'Ei nõustu', 'Disagree', 'НЕ СОГЛАСЕН');
call update_translations('LICENSE_AGREEMENT_DIALOG_ACTION_YES', 'Nõustun ja sisenen', 'Agree and enter', 'СОГЛАСЕН, ВОЙТИ');

SET foreign_key_checks = 1;