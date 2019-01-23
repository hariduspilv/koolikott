SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Nõusolek isikuandmete töötlemise kohta' WHERE translationKey = 'AGREEMENT_DIALOG_HEADER' and translationGroup = 1;
UPDATE Translation SET translation = 'Nõusolek isikuandmete töötlemise kohta' WHERE translationKey = 'AGREEMENT_DIALOG_HEADER' and translationGroup = 2;

SET foreign_key_checks = 1;
