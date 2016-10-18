SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Minu materjalid' WHERE translationGroup = 1 AND translationKey = 'MY_MATERIALS';

UPDATE Translation SET translation = 'E-koolikotist' WHERE translationGroup = 1 AND translationKey = 'HEADER_TOOLTIP_HELP';
UPDATE Translation SET translation = 'About E-koolikott' WHERE translationGroup = 3 AND translationKey = 'HEADER_TOOLTIP_HELP';
UPDATE Translation SET translation = 'О E-koolikott' WHERE translationGroup = 2 AND translationKey = 'HEADER_TOOLTIP_HELP';

SET foreign_key_checks = 1;
