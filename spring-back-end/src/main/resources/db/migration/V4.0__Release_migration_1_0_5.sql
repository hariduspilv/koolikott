SET foreign_key_checks = 0;

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3,'DETAILED_SEARCH_LANGUAGE','Language');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1,'DETAILED_SEARCH_LANGUAGE','Keel');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2,'DETAILED_SEARCH_LANGUAGE','язык');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3,'DETAILED_SEARCH_ISSUE_DATE','Published (since)');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1,'DETAILED_SEARCH_ISSUE_DATE','Avaldatud (alates)');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2,'DETAILED_SEARCH_ISSUE_DATE','Опубликовано (с)');

SET foreign_key_checks = 1;
