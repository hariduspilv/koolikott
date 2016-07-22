SET foreign_key_checks = 0;

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1,'UPLOADED_PICTURE_TOO_BIG','Üleslaetud pilt on liiga suur');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3,'UPLOADED_PICTURE_TOO_BIG','Uploaded picture is too big');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2,'UPLOADED_PICTURE_TOO_BIG','Изображение слишком большой');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1,'LAST_NAME_REQUIRED','Perekonnanimi on nõutud');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3,'LAST_NAME_REQUIRED','Last name is required');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2,'LAST_NAME_REQUIRED','Фамилия требуется');

SET foreign_key_checks = 1;
