SET foreign_key_checks = 0;
ALTER TABLE LanguageTable ADD priorityOrder BIGINT;

UPDATE LanguageTable SET priorityOrder=5 WHERE code='est';
UPDATE LanguageTable SET priorityOrder=4 WHERE code='eng';
UPDATE LanguageTable SET priorityOrder=3 WHERE code='ger';
UPDATE LanguageTable SET priorityOrder=2 WHERE code='fin';
UPDATE LanguageTable SET priorityOrder=1 WHERE code='rus';

UPDATE Translation SET translation = 'Saksa' WHERE  translationGroup =1 AND translationKey = 'LANGUAGE_GER';
UPDATE Translation SET translation = 'German' WHERE  translationGroup =3 AND translationKey = 'LANGUAGE_GER';
UPDATE Translation SET translation = 'Немецкий' WHERE  translationGroup =2 AND translationKey = 'LANGUAGE_GER';

UPDATE Translation SET translation = 'Soome' WHERE  translationGroup =1 AND translationKey = 'LANGUAGE_FIN';
UPDATE Translation SET translation = 'Finnish' WHERE  translationGroup =3 AND translationKey = 'LANGUAGE_FIN';
UPDATE Translation SET translation = 'Финский' WHERE  translationGroup =2 AND translationKey = 'LANGUAGE_FIN';

SET foreign_key_checks = 1;
