SET foreign_key_checks = 0;

UPDATE Translation set translation = 'Pealkirjades (sh peatükkide pealkirjades)' WHERE translationKey = 'GROUPS_TITLES' and translationGroup = 1;
UPDATE Translation set translation = 'Titles (incl chapter titles)' WHERE translationKey = 'GROUPS_TITLES' and translationGroup = 3;

SET foreign_key_checks = 1;
