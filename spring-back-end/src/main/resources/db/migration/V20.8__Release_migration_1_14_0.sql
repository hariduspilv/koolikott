SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Allikas on kohustuslik' WHERE translationKey = 'SOURCE_REQUIRED' and translationGroup = 1;

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'SOURCE_OR_LICENSE_TYPE_REQUIRED', 'Allikas või litsentsitüübi määramine on kohustuslik');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'SOURCE_OR_LICENSE_TYPE_REQUIRED', 'Either source or license type must be entered');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'SOURCE_OR_LICENSE_TYPE_REQUIRED', 'Allikas või litsentsitüübi määramine on kohustuslik');

SET foreign_key_checks = 1;
