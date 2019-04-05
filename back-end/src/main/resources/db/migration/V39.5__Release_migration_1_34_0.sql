SET foreign_key_checks = 0;

call insert_translation('ERROR_LOGIN_FAILED_HARID', 'Sisselogimine ebaõnnestus. HarID-s oled registreeritud kasutaja, kelle infost puudub isikukood. Logi sisse HarID-sse, salvesta sealse konto infosse oma isikukood ja tule siis e-Koolikotti tagasi. E-Koolikotti saad sisse logida ka ID-kaardi ja mID-ga..', 'Login failed. You are a registered user of HarID whose info is missing personal ID code. Login to HarID, save your personal ID code in you profile and return to e-Koolikott. You can also login to e-Koolikott with ID-card and mID.');

SET foreign_key_checks = 1;