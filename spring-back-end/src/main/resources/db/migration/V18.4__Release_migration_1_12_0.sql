SET foreign_key_checks = 0;

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'CHANGED_LINK', 'Õppevara link. Enne oli:');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'CHANGED_LINK', 'Learning object link. Previously:');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'CHANGED_LINK', 'Õppevara link. Enne oli:');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'ADDED_ONE_TAXON', 'Õppevara liigitus. Lisatud väärtus:');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'ADDED_ONE_TAXON', 'Learning object taxon: Added value:');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'ADDED_ONE_TAXON', 'Õppevara liigitus. Lisatud väärtus:');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'ADDED_MULTIPLE_TAXONS', 'Õppevara liigitus. Lisatud %d väärtust:');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'ADDED_MULTIPLE_TAXONS', 'Learning object taxons: Added %d values:');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'ADDED_MULTIPLE_TAXONS', 'Õppevara liigitus. Lisatud %d väärtust:');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'CHANGED_LINK_AND_ADDED_ONE_TAXON', 'Õppevara link ja liigitus. Lisatud väärtus:');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'CHANGED_LINK_AND_ADDED_ONE_TAXON', 'Learning object link and taxon: Added value:');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'CHANGED_LINK_AND_ADDED_ONE_TAXON', 'Õppevara link ja liigitus. Lisatud väärtus:');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'CHANGED_LINK_AND_ADDED_MULTIPLE_TAXONS', 'Õppevara link ja liigitus. Lisatud %d väärtust:');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'CHANGED_LINK_AND_ADDED_MULTIPLE_TAXONS', 'Learning object link and taxons: Added %d values:');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'CHANGED_LINK_AND_ADDED_MULTIPLE_TAXONS', 'Õppevara link ja liigitus. Lisatud %d väärtust:');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'CHANGED_LINK_ROW', 'Enne oli:');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'CHANGED_LINK_ROW', 'Previously:');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'CHANGED_LINK_ROW', 'Enne oli:');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'ACCEPT_CHANGE', 'Nõustu muudatusega');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'ACCEPT_CHANGE', 'Accept change');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'ACCEPT_CHANGE', 'Nõustu muudatusega');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'UNDO_CHANGE', 'Eemalda muudatus');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'UNDO_CHANGE', 'Undo change');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'UNDO_CHANGE', 'Eemalda muudatus');

UPDATE Translation SET translation = 'See õppevara on muutunud' WHERE translationKey = 'MESSAGE_CHANGED_LEARNING_OBJECT' and translationGroup = 1;
UPDATE Translation SET translation = 'This learning object has been changed' WHERE translationKey = 'MESSAGE_CHANGED_LEARNING_OBJECT' and translationGroup = 3;
UPDATE Translation SET translation = 'See õppevara on muutunud' WHERE translationKey = 'MESSAGE_CHANGED_LEARNING_OBJECT' and translationGroup = 2;

SET foreign_key_checks = 1;
