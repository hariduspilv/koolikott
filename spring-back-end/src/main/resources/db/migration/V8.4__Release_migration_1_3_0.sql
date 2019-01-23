SET foreign_key_checks = 0;

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1,'ADDED_TO_FAVORITES','Lisatud lemmikutesse');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3,'ADDED_TO_FAVORITES','Added to favorites');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2,'ADDED_TO_FAVORITES','Добавлено в избранное');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1,'REMOVED_FROM_FAVORITES','Lemmikutest eemaldatud');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3,'REMOVED_FROM_FAVORITES','Removed from favorites');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2,'REMOVED_FROM_FAVORITES','Удалены из избранного');

SET foreign_key_checks = 1;
