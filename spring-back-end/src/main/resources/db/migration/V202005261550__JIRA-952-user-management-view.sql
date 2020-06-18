SET foreign_key_checks = 0;

call insert_translation('USERS', 'Kasutajad', 'Users');
call insert_translation('FILTER_RESULTS', 'Filtreeri tulemusi', 'Filter results');
call insert_translation('USER_ROLE_APP', 'Kasutaja roll rakenduses', 'User role in application');
call insert_translation('USER_ROLE_PICKED', 'Kasutaja määratud roll', 'Role picked by user');
call insert_translation('USER_INTEREST', 'Kasutajale huvipakkuv haridustase', 'Educational level user is interested in');
call insert_translation('USERS_WITH_EMAIL', 'Ainult e-postiaadressiga kasutajad', 'Users with e-mail');
call insert_translation('USERS_WITHOUT_EMAIL', 'Ilma e-postiaadressita kasutajad', 'Users without e-mail');
call insert_translation('EMAIL', 'E-post', 'E-mail');
call insert_translation('APP_ROLE', 'Rakenduse roll', 'App role');
call insert_translation('EDU_LEVEL', 'Haridustase', 'Education');
call insert_translation('DOMAIN_LEVEL', 'Ainevaldkond', 'Domain');
call insert_translation('LAST_LOGIN', 'Viimane sisselogimine', 'Last login');
call insert_translation('SEARCH_USER', 'Otsi kasutajat nime järgi', 'Search user by name');
call insert_translation('SEND_EMAIL', 'Saada kiri', 'Send e-mail');

SET foreign_key_checks = 1;