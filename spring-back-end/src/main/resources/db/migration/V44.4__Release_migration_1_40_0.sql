SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Nõustun' WHERE translationKey = 'AGREEMENT_DIALOG_ACTION_YES' AND translationGroup = 1;
UPDATE Translation SET translation = 'Nõustun' WHERE translationKey = 'AGREEMENT_DIALOG_ACTION_YES' AND translationGroup = 2;
UPDATE Translation SET translation = 'Agree' WHERE translationKey = 'AGREEMENT_DIALOG_ACTION_YES' AND translationGroup = 3;

UPDATE Translation SET translation = 'Et saaksid E-Koolikotti kasutada, on meil vaja Sinu isikuandmeid (eelkõige kontaktandmeid). Palun loe alltoodud teavet ja nõusoleku teksti isikuandmete töötlemise kohta. Nõustumisega kinnitad, et oled teabe ja nõusoleku teksti põhjalikult läbi lugenud ja sellega nõus.' WHERE translationKey = 'AGREEMENT_DIALOG_TEXT' AND translationGroup = 1;
UPDATE Translation SET translation = 'Et saaksid E-Koolikotti kasutada, on meil vaja Sinu isikuandmeid (eelkõige kontaktandmeid). Palun loe alltoodud teavet ja nõusoleku teksti isikuandmete töötlemise kohta. Nõustumisega kinnitad, et oled teabe ja nõusoleku teksti põhjalikult läbi lugenud ja sellega nõus.' WHERE translationKey = 'AGREEMENT_DIALOG_TEXT' AND translationGroup = 2;
UPDATE Translation SET translation = 'To use E-Koolikott you are giving your personal data to digital learning object platform. Please read information page and give your response. By clicking Agree, you confirm that you have read information carefully and agreed to our terms.' WHERE translationKey = 'AGREEMENT_DIALOG_TEXT' AND translationGroup = 3;

UPDATE Translation SET translation = 'Teave isikuandmete töötlemise kohta ja nõusoleku tekst' WHERE translationKey = 'AGREEMENT_DIALOG_LINK_TEXT' AND translationGroup = 1;
UPDATE Translation SET translation = 'Teave isikuandmete töötlemise kohta ja nõusoleku tekst' WHERE translationKey = 'AGREEMENT_DIALOG_LINK_TEXT' AND translationGroup = 2;
UPDATE Translation SET translation = 'Full description of personal data protection' WHERE translationKey = 'AGREEMENT_DIALOG_LINK_TEXT' AND translationGroup = 3;




SET foreign_key_checks = 1;