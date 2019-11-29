SET foreign_key_checks = 0;

call insert_translation('AGREE_TO', 'Nõustun E-koolikoti', 'Agree to the');
call insert_translation('WITH_TERMS', 'kasutustingimustega', 'terms');
call insert_translation('WITH_LICENSES', 'litsentsitingimustega', 'licenses');
call insert_translation('AGREEMENT_REQUIRED', ' *', 'of E-koolikott *');

SET foreign_key_checks = 1;