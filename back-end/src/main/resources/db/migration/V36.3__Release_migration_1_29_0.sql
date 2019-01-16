SET foreign_key_checks = 0;

call procedure insert_translation('FULL_SCREEN_ON', 'Ava täisekraan vaade', 'Toggle full screen');
call procedure insert_translation('FULL_SCREEN_OFF', 'Sulge täisekraan vaade', 'Quit full screen');

SET foreign_key_checks = 1;
