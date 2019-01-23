SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Uus õppevara' WHERE translationKey = 'UNREVIEWED' and translationGroup = 1;
UPDATE Translation SET translation = 'New learning objects' WHERE translationKey = 'UNREVIEWED' and translationGroup = 3;
UPDATE Translation SET translation = 'Uus õppevara' WHERE translationKey = 'UNREVIEWED' and translationGroup = 2;

UPDATE Translation SET translation = 'Muudetud õppevara' WHERE translationKey = 'DASHBOARD_CHANGED_LEARNING_OBJECTS' and translationGroup = 1;
UPDATE Translation SET translation = 'Changed learning objects' WHERE translationKey = 'DASHBOARD_CHANGED_LEARNING_OBJECTS' and translationGroup = 3;
UPDATE Translation SET translation = 'Muudetud õppevara' WHERE translationKey = 'DASHBOARD_CHANGED_LEARNING_OBJECTS' and translationGroup = 2;

UPDATE Translation SET translation = 'Teatatud materialid' WHERE translationKey = 'IMPROPER_MATERIALS' and translationGroup = 1;
UPDATE Translation SET translation = 'Reported materials' WHERE translationKey = 'IMPROPER_MATERIALS' and translationGroup = 3;
UPDATE Translation SET translation = 'Teatatud materialid' WHERE translationKey = 'IMPROPER_MATERIALS' and translationGroup = 2;

UPDATE Translation SET translation = 'Teatatud kogumikud' WHERE translationKey = 'IMPROPER_PORTFOLIOS' and translationGroup = 1;
UPDATE Translation SET translation = 'Reported portfolios' WHERE translationKey = 'IMPROPER_PORTFOLIOS' and translationGroup = 3;
UPDATE Translation SET translation = 'Teatatud kogumikud' WHERE translationKey = 'IMPROPER_PORTFOLIOS' and translationGroup = 2;

SET foreign_key_checks = 1;
