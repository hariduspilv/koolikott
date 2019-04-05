SET foreign_key_checks = 0;

call insert_translation('ERROR_LOGIN_FAILED_HARID', 'Sisselogimine ebaõnnestus. Oled HarID-s kasutaja, kellel puudub isikukood.
HarID kaudu e-Koolikotti sisenemiseks logi esmalt HarID-sse sisse ning salvesta oma kontole isikukood. Seejärel sulge browseri aken ja logi uues aknas eKoolikotti HarID konto kaudu.',
'Login failed. You are a registered user of HarID whose info is missing personal ID code. Login to HarID, save your personal ID code in you profile, close browser and return to e-Koolikott in new browser window with HarID.');

SET foreign_key_checks = 1;