SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Et saaksid e-Koolikotti kasutada, annad oma isikuandmed digitaalse õppevara infosüsteemile. Palun loe isikuandmete töötlemise teavitust ning anna oma nõusolekust märku' WHERE translationKey = 'AGREEMENT_DIALOG_TEXT' and translationGroup = 1;
UPDATE Translation SET translation = 'To use e-Koolikott you are giving your personal data to digital learning object platform. Please read information page and give your response' WHERE translationKey = 'AGREEMENT_DIALOG_TEXT' and translationGroup = 3;
UPDATE Translation SET translation = 'Et saaksid e-Koolikotti kasutada, annad oma isikuandmed digitaalse õppevara infosüsteemile. Palun loe isikuandmete töötlemise teavitust ning anna oma nõusolekust märku' WHERE translationKey = 'AGREEMENT_DIALOG_TEXT' and translationGroup = 2;

UPDATE Translation SET translation = 'Nõusolek isikuandmete tõõtlemise kohta' WHERE translationKey = 'AGREEMENT_DIALOG_HEADER' and translationGroup = 1;
UPDATE Translation SET translation = 'Nõusolek isikuandmete tõõtlemise kohta' WHERE translationKey = 'AGREEMENT_DIALOG_HEADER' and translationGroup = 2;

UPDATE Translation SET translation = 'Teave isikuandmete töötlemise kohta' WHERE translationKey = 'AGREEMENT_DIALOG_LINK_TEXT' and translationGroup = 1;
UPDATE Translation SET translation = 'Teave isikuandmete töötlemise kohta' WHERE translationKey = 'AGREEMENT_DIALOG_LINK_TEXT' and translationGroup = 2;

UPDATE Translation SET translation = 'https://projektid.hitsa.ee/display/koolikott/Isikuandmete+kaitse' WHERE translationKey = 'AGREEMENT_DIALOG_LINK_HREF';


SET foreign_key_checks = 1;