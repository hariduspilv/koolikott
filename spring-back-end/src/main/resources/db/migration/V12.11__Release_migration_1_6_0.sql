SET foreign_key_checks = 0;

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'PORTFOLIO_DELETE_MATERIAL_CONFIRM_TITLE', 'Kas oled kindel?');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'PORTFOLIO_DELETE_MATERIAL_CONFIRM_TITLE', 'Are you sure?');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'PORTFOLIO_DELETE_MATERIAL_CONFIRM_TITLE', 'Вы уверены?');

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'PORTFOLIO_DELETE_MATERIAL_CONFIRM_MESSAGE', 'Materjali eemaldamist ei ole võimalik tagasi võtta');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'PORTFOLIO_DELETE_MATERIAL_CONFIRM_MESSAGE', 'It is not possible to undo this operatio');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'PORTFOLIO_DELETE_MATERIAL_CONFIRM_MESSAGE', 'Это не возможно, чтобы отменить эту операцию');

SET foreign_key_checks = 1;
