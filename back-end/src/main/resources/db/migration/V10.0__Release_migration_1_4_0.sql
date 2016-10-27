SET foreign_key_checks = 0;
ALTER TABLE LanguageTable ADD priorityOrder BIGINT;

UPDATE LanguageTable SET priorityOrder=5 WHERE code='est';
UPDATE LanguageTable SET priorityOrder=4 WHERE code='eng';
UPDATE LanguageTable SET priorityOrder=3 WHERE code='ger';
UPDATE LanguageTable SET priorityOrder=2 WHERE code='fin';
UPDATE LanguageTable SET priorityOrder=1 WHERE code='rus';


INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1,'LANGUAGE_GER','Saksa');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3,'LANGUAGE_GER','German');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2,'LANGUAGE_GER','Немецкий');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1,'LANGUAGE_FIN','Soome');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3,'LANGUAGE_FIN','Finnish');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2,'LANGUAGE_FIN','Финский');

SET foreign_key_checks = 1;
