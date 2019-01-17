SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'See õppevara on kustutatud' WHERE translationGroup = 1 AND translationKey = 'ERROR_MSG_DELETED';
UPDATE Translation SET translation = 'Это исследование материалов было удалено' WHERE translationGroup = 2 AND translationKey = 'ERROR_MSG_DELETED';

SET foreign_key_checks = 1;
