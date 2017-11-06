SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Muudetud õppevara' WHERE translationKey = 'SHOW_ALL_CHANGES' and translationGroup = 1;
UPDATE Translation SET translation = 'Changed learning objects' WHERE translationKey = 'SHOW_ALL_CHANGES' and translationGroup = 3;
UPDATE Translation SET translation = 'Muudetud õppevara' WHERE translationKey = 'SHOW_ALL_CHANGES' and translationGroup = 2;

UPDATE Translation SET translation = 'Uus õppevara' WHERE translationKey = 'HIDE_ALL_CHANGES' and translationGroup = 1;
UPDATE Translation SET translation = 'New learning objects' WHERE translationKey = 'HIDE_ALL_CHANGES' and translationGroup = 3;
UPDATE Translation SET translation = 'Uus õppevara' WHERE translationKey = 'HIDE_ALL_CHANGES' and translationGroup = 2;

SET foreign_key_checks = 1;
