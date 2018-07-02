SET foreign_key_checks = 0;

UPDATE Translation set translation = 'Hiljuti lisatud eespool' WHERE translationKey = 'ADDED_DATE_DESC' and translationGroup = 1;
UPDATE Translation set translation = 'Varem lisatud eespool' WHERE translationKey = 'ADDED_DATE_ASC' and translationGroup = 1;

UPDATE Translation set translation = 'Recently added first' WHERE translationKey = 'ADDED_DATE_DESC' and translationGroup = 1;
UPDATE Translation set translation = 'Earliest added first' WHERE translationKey = 'ADDED_DATE_ASC' and translationGroup = 1;

SET foreign_key_checks = 1;