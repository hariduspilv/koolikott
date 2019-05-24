SET foreign_key_checks = 0;

call insert_translation('LOG_NO_LOGS', 'Kogumikul ajaloo versioonid puuduvad', 'This portfolio does not have any previously saved versions. ');

UPDATE Translation SET translation = 'Kogumiku salvestamine ebaõnnestus. Täiendavate küsimuste esitamiseks pöörduge kasutajatoe poole.' WHERE translationKey = 'PORTFOLIO_SAVE_FAILED' AND translationGroup = 1;
UPDATE Translation SET translation = 'Kogumiku salvestamine ebaõnnestus. Täiendavate küsimuste esitamiseks pöörduge kasutajatoe poole.' WHERE translationKey = 'PORTFOLIO_SAVE_FAILED' AND translationGroup = 2;
UPDATE Translation SET translation = 'Portfolio save failed. For further assistance contact customer support.' WHERE translationKey = 'PORTFOLIO_SAVE_FAILED' AND translationGroup = 3;

UPDATE Translation SET translation = 'Muudatuste salvestamine ebaõnnestus. Täiendavate küsimuste esitamiseks pöörduge kasutajatoe poole.' WHERE translationKey = 'PORTFOLIO_AUTOSAVE_FAILED' AND translationGroup = 1;
UPDATE Translation SET translation = 'Muudatuste salvestamine ebaõnnestus. Täiendavate küsimuste esitamiseks pöörduge kasutajatoe poole.' WHERE translationKey = 'PORTFOLIO_AUTOSAVE_FAILED' AND translationGroup = 2;
UPDATE Translation SET translation = 'Portfolio save failed. For further assistance contact customer support.' WHERE translationKey = 'PORTFOLIO_AUTOSAVE_FAILED' AND translationGroup = 3;

UPDATE Translation SET translation = 'Vali kogumiku versioon, mida soovid taastada' WHERE translationKey = 'LOG_CHOOSE' AND translationGroup = 1;
UPDATE Translation SET translation = 'Vali kogumiku versioon, mida soovid taastada' WHERE translationKey = 'LOG_CHOOSE' AND translationGroup = 2;
UPDATE Translation SET translation = 'Choose version to restore' WHERE translationKey = 'LOG_CHOOSE' AND translationGroup = 3;

UPDATE Translation SET translation = 'Kogumiku ajaloo taastamine' WHERE translationKey = 'LOG_HISTORY' AND translationGroup = 1;
UPDATE Translation SET translation = 'Kogumiku ajaloo taastamine' WHERE translationKey = 'LOG_HISTORY' AND translationGroup = 2;
UPDATE Translation SET translation = 'Portfolio history restore' WHERE translationKey = 'LOG_HISTORY' AND translationGroup = 3;

UPDATE Translation SET translation = 'Kogumiku versioon seisuga' WHERE translationKey = 'LOG_MOMENT' AND translationGroup = 1;
UPDATE Translation SET translation = 'Kogumiku versioon seisuga' WHERE translationKey = 'LOG_MOMENT' AND translationGroup = 2;
UPDATE Translation SET translation = 'Portfolio history saved on' WHERE translationKey = 'LOG_MOMENT' AND translationGroup = 3;

SET foreign_key_checks = 1;
