SET foreign_key_checks = 0;

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1,'ERROR_MSG_IMPROPER_AND_BROKEN','Raporteeritud ebasobivaks ja katkiseks');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3,'ERROR_MSG_IMPROPER_AND_BROKEN','Reported as inappropriate and broken');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2,'ERROR_MSG_IMPROPER_AND_BROKEN','Сообщается, как неприемлемое и сломаны');

UPDATE Translation SET translation = 'Raporteeritud ebasobivaks' WHERE translationGroup = 1 AND translationKey = 'ERROR_MSG_IMPROPER';
UPDATE Translation SET translation = 'Reported as inappropriate' WHERE translationGroup = 3 AND translationKey = 'ERROR_MSG_IMPROPER';
UPDATE Translation SET translation = 'Сообщается, как нарушение' WHERE translationGroup = 2 AND translationKey = 'ERROR_MSG_IMPROPER';

UPDATE Translation SET translation = 'Raporteeritud katkiseks' WHERE translationGroup = 1 AND translationKey = 'ERROR_MSG_BROKEN';
UPDATE Translation SET translation = 'Reported as broken' WHERE translationGroup = 3 AND translationKey = 'ERROR_MSG_BROKEN';
UPDATE Translation SET translation = 'Сообщается, как неработающую' WHERE translationGroup = 2 AND translationKey = 'ERROR_MSG_BROKEN';

SET foreign_key_checks = 1;
