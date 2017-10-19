SET foreign_key_checks = 0;

# Retroactively add estonian phrases as placeholders for missing russian translations

UPDATE Translation
SET translation = 'Teavita ebasobivast võtmesõnast'
WHERE translationKey = 'TAG_TOOLTIP_REPORT_AS_IMPROPER'
AND translationGroup = 2;

UPDATE Translation
SET translation = 'Vali vähemalt üks põhjus'
WHERE translationKey = 'MESSAGE_ERROR_IMPROPER_REPORT_REASON_REQUIRED'
AND translationGroup = 2;

UPDATE Translation
SET translation = 'Sobimatu sisu'
WHERE translationKey = 'LO_CONTENT'
AND translationGroup = 2;

UPDATE Translation
SET translation = 'Katkine vorm või viit'
WHERE translationKey = 'LO_FORM'
AND translationGroup = 2;

UPDATE Translation
SET translation = 'Puudus kirjeldavates andmetes'
WHERE translationKey = 'LO_METADATA'
AND translationGroup = 2;

UPDATE Translation
SET translation = 'Teavita ebasobivast kommentaarist'
WHERE translationKey = 'COMMENT_TOOLTIP_REPORT_AS_IMPROPER'
AND translationGroup = 2;

UPDATE Translation
SET translation = 'Selgitus'
WHERE translationKey = 'IMPROPER_REPORT_DESCRIPTION'
AND translationGroup = 2;

UPDATE Translation
SET translation = 'Erinevad põhjused'
WHERE translationKey = 'MULTIPLE_REASONS'
AND translationGroup = 2;
UPDATE Translation
SET translation = 'Kuva kogu põhjuse tekst'
WHERE translationKey = 'SHOW_ALL_REPORT_DETAILS'
AND translationGroup = 2;

UPDATE Translation
SET translation = 'Peida põhjuse tekst'
WHERE translationKey = 'HIDE_ALL_REPORT_DETAILS'
AND translationGroup = 2;

UPDATE Translation
SET translation = 'Ei leidnud kasutajat'
WHERE translationKey = 'ERROR_USERS_NOT_FOUND'
AND translationGroup = 2;

UPDATE Translation
SET translation = 'Märgi ülevaadatuks'
WHERE translationKey = 'BUTTON_REVIEW'
AND translationGroup = 2;

UPDATE Translation
SET translation = 'See õppevara on üle vaatamata'
WHERE translationKey = 'ERROR_MSG_UNREVIEWED'
AND translationGroup = 2;

UPDATE Translation
SET translation = 'See kogumik on üle vaatamata'
WHERE translationKey = 'ERROR_MSG_UNREVIEWED_PORTFOLIO'
AND translationGroup = 2;

UPDATE Translation
SET translation = 'Ei leidnud ühtegi õppevara'
WHERE translationKey = 'ERROR_LEARNING_OBJECTS_NOT_FOUND'
AND translationGroup = 2;

UPDATE Translation
SET translation = 'Üle vaatamata õppevara'
WHERE translationKey = 'DASHBOARD_UNREVIEWED'
AND translationGroup = 2;

UPDATE Translation
SET translation = 'Üle vaatamata õppevara'
WHERE translationKey = 'UNREVIEWED'
AND translationGroup = 2;

UPDATE Translation
SET translation = 'Lisas'
WHERE translationKey = 'ADDED_BY'
AND translationGroup = 2;

UPDATE Translation
SET translation = 'Lisatud'
WHERE translationKey = 'ADDED_AT'
AND translationGroup = 2;

SET foreign_key_checks = 1;
