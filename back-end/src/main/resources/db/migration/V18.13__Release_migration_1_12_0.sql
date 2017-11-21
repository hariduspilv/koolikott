SET foreign_key_checks = 0;

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'HTTP_WORD', 'http://');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'HTTP_WORD', 'http://');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'HTTP_WORD', 'http://');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'FILE_WORD', 'fail');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'FILE_WORD', 'file');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'FILE_WORD', 'fail');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'OR_WORD', 'või');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'OR_WORD', 'or');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'OR_WORD', 'või');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'AND_OR_AUTHOR', 'ja/või autor');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'AND_OR_AUTHOR', 'and/or author');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'AND_OR_AUTHOR', 'ja/või autor');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'SOURCE', 'allikas*');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'SOURCE', 'source*');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'SOURCE', 'allikas*');

SET foreign_key_checks = 1;
