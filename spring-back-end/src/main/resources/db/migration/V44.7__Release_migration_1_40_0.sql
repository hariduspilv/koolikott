SET foreign_key_checks = 0;

call insert_translation('MATERIAL_WARNING_IN_PORTFOLIO', 'Privaatne materjal salvestatakse kogumikku lisamata!', 'Private material will be saved but not added to the portfolio!');
call insert_translation('MATERIAL_SAVED_IN_PORTFOLIO', 'Privaatme materjal salvestatud "Minu materjalide" hulka!', 'Private material saved to "My materials"!');


SET foreign_key_checks = 1;