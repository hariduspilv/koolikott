SET foreign_key_checks = 0;

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'BUTTON_ADD_MEDIA', 'Lisa meedia');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'BUTTON_ADD_MEDIA', 'Add media');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'BUTTON_ADD_MEDIA', 'Lisa meedia');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'BUTTON_UPDATE_MEDIA', 'Muuda meediat');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'BUTTON_UPDATE_MEDIA', 'Update media');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'BUTTON_UPDATE_MEDIA', 'Muuda meediat');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'ADD_MEDIA', 'Lisa meedia');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'ADD_MEDIA', 'Add media');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'ADD_MEDIA', 'Lisa meedia');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'EDIT_MEDIA', 'Muuda meediat');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'EDIT_MEDIA', 'Update media');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'EDIT_MEDIA', 'Muuda meediat');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'DETAILS', 'Andmed');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'DETAILS', 'Details');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'DETAILS', 'Andmed');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'ADD_MEDIA_LINK', 'Sisesta link media failile');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'ADD_MEDIA_LINK', 'Insert link to a media file');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'ADD_MEDIA_LINK', 'Sisesta link media failile');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'MEDIA_URL_IS_INVALID', 'Meedia URL on vigane');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'MEDIA_URL_IS_INVALID', 'Media URL is invalid');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'MEDIA_URL_IS_INVALID', 'Meedia URL on vigane');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'MEDIA_URL_IS_REQUIRED', 'Meedia URL on kohustuslik');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'MEDIA_URL_IS_REQUIRED', 'Media URL is required');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'MEDIA_URL_IS_REQUIRED', 'Meedia URL on kohustuslik');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'TITLE_IS_REQUIRED', 'Pealkiri on kohustuslik');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'TITLE_IS_REQUIRED', 'Title is required');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'TITLE_IS_REQUIRED', 'Pealkiri on kohustuslik');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'MEDIA_URL_IS_NOT_ACCEPTED_BY_EXTENSION', 'Lisatud meediafaili formaat ei ole kasutatav. Sobivad formaadid ja allikad: %s.');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'MEDIA_URL_IS_NOT_ACCEPTED_BY_EXTENSION', 'Inserted media file format is not accepted. Acceptable formats and sources: %s.');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'MEDIA_URL_IS_NOT_ACCEPTED_BY_EXTENSION', 'Lisatud meediafaili formaat ei ole kasutatav. Sobivad formaadid ja allikad: %s.');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'LICENSE_TYPE_IS_REQUIRED', 'Litsentsi tüüp kohustuslik');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'LICENSE_TYPE_IS_REQUIRED', 'License type is required');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'LICENSE_TYPE_IS_REQUIRED', 'Litsentsi tüüp kohustuslik');

# Possessive form is written with apostrophe and S. An S without an apostrophe designates plural.
# @see https://en.wikipedia.org/wiki/English_possessive
UPDATE Translation SET translation = "Author's name is required" WHERE translationKey = 'AUTHOR_NAME_REQUIRED' and translationGroup = 3;

SET foreign_key_checks = 1;