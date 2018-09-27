SET foreign_key_checks = 0;

INSERT INTO Translation (translationGroup, translationKey, translation)
VALUES (1, 'LANDING_PAGE_TITLE', 'Soovitatud');
INSERT INTO Translation (translationGroup, translationKey, translation)
VALUES (3, 'LANDING_PAGE_TITLE', 'Recommended');
INSERT INTO Translation (translationGroup, translationKey, translation)
VALUES (2, 'LANDING_PAGE_TITLE', 'Soovitatud');

INSERT INTO Translation (translationGroup, translationKey, translation)
VALUES (1, 'LANDING_PAGE_DESCRIPTION', '5.oktoober on õpetajate päev ning sel päeval Minister kuulutab, et põhikooli õpikud on nüüd tasuta kõikidele terveks õppeaastaks. Neid materjale (metaandmed, mis on juba ekoolikotis sisestatud) on leitavad läbi e-koolikotti.');
INSERT INTO Translation (translationGroup, translationKey, translation)
VALUES (3, 'LANDING_PAGE_DESCRIPTION', '5.oktoober on õpetajate päev ning sel päeval Minister kuulutab, et põhikooli õpikud on nüüd tasuta kõikidele terveks õppeaastaks. Neid materjale (metaandmed, mis on juba ekoolikotis sisestatud) on leitavad läbi e-koolikotti.');
INSERT INTO Translation (translationGroup, translationKey, translation)
VALUES (2, 'LANDING_PAGE_DESCRIPTION', '5.oktoober on õpetajate päev ning sel päeval Minister kuulutab, et põhikooli õpikud on nüüd tasuta kõikidele terveks õppeaastaks. Neid materjale (metaandmed, mis on juba ekoolikotis sisestatud) on leitavad läbi e-koolikotti.');

SET foreign_key_checks = 1;