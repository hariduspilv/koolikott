SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Vastav väärtus on lisatud materjali külge!' WHERE translationGroup = 1 AND translationKey = 'SYSTEM_TAG_DIALOG_CONTENT';
UPDATE Translation SET translation = 'Corresponding value is added to the material' WHERE translationGroup = 3 AND translationKey = 'SYSTEM_TAG_DIALOG_CONTENT';
UPDATE Translation SET translation = 'Соответствующее значение добавляется к материалу' WHERE translationGroup = 2 AND translationKey = 'SYSTEM_TAG_DIALOG_CONTENT';

SET foreign_key_checks = 1;
