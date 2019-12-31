SET foreign_key_checks = 0;

call update_translations('PLEASE_WAIT_TEXT', 'Palun oota, sinu õppevarasid laetakse', 'Please wait, we are loading your educational content', 'Пожалуйста, подождите, ваши учебные материалы загружаются');
call update_translations('PLEASE_WAIT', 'Palun oota', 'Please wait', 'Пожалуйста, подождите ');

SET foreign_key_checks = 1;