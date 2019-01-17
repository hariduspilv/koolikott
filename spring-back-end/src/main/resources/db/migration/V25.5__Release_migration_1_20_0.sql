SET foreign_key_checks = 0;

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'NO_RESULTS_FOUND', 'Õppevaras ei leidu vasteid otsingule "<strong>${query}</strong>"');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'NO_RESULTS_FOUND', 'No learning objects contain the query "<strong>${query}</strong>"');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'NO_RESULTS_FOUND', 'Õppevaras ei leidu vasteid otsingule "<strong>${query}</strong>"');

SET foreign_key_checks = 1;
