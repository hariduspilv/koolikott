SET foreign_key_checks = 0;

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'PORTFOLIO_METADATA_TOOLBAR_EDIT_LABEL', 'Muuda kogumikku');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'PORTFOLIO_METADATA_TOOLBAR_EDIT_LABEL', 'Edit portfolio');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'PORTFOLIO_METADATA_TOOLBAR_EDIT_LABEL', 'Изменить резюме');

UPDATE Translation SET translation = 'Lisa kogumik' WHERE translationKey = 'PORTFOLIO_METADATA_TOOLBAR_LABEL' and translationGroup = 1;
UPDATE Translation SET translation = 'Add portfolio' WHERE translationKey = 'PORTFOLIO_METADATA_TOOLBAR_LABEL' and translationGroup = 3;
UPDATE Translation SET translation = 'Добавить резюме' WHERE translationKey = 'PORTFOLIO_METADATA_TOOLBAR_LABEL' and translationGroup = 2;

UPDATE Translation SET translation = 'Väljaandja' WHERE translationKey = 'PUBLISHER' and translationGroup = 1;
UPDATE Translation SET translation = 'Väljaandjad' WHERE translationKey = 'PUBLISHERS' and translationGroup = 1;


SET foreign_key_checks = 1;