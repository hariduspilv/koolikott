SET foreign_key_checks = 0;

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1,'ADD_TO_FAVORITE','Lisa lemmikuks');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3,'ADD_TO_FAVORITE','Add to favorites');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2,'ADD_TO_FAVORITE','Добавить в избранное');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1,'REMOVE_FROM_FAVORITES','Eemalda lemmikutest');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3,'REMOVE_FROM_FAVORITES','Remove from favorites');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2,'REMOVE_FROM_FAVORITES','Удалить из избранного');

SET foreign_key_checks = 1;
