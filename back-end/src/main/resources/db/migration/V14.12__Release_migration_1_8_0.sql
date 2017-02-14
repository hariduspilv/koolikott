SET foreign_key_checks = 0;

ALTER TABLE dop.UploadedFile MODIFY path TEXT;
ALTER TABLE dop.UploadedFile MODIFY url TEXT;

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'MATERIAL_FILE_NAME_TOO_LONG', 'Failinimi on liiga pikk');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'MATERIAL_FILE_NAME_TOO_LONG', 'Filename too long');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'MATERIAL_FILE_NAME_TOO_LONG', 'Имя файла слишком длинное');

SET foreign_key_checks = 1;
