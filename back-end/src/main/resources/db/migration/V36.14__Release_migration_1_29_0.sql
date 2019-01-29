SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Lisa pildifail (kuni 3 tk, kokku kuni 10MB)' WHERE translationKey = 'UPLOAD_FILE_INFO' AND translationGroup = 1;
UPDATE Translation SET translation = 'Add imagefile (max.3; up to 10MB combined)' WHERE translationKey = 'UPLOAD_FILE_INFO' AND translationGroup = 3;


SET foreign_key_checks = 1;


