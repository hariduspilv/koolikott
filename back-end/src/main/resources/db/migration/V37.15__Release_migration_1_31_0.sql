SET foreign_key_checks = 0;

call insert_translation('TYPE_ALL', 'Kõik õppevaratüübid', 'All learningobject types ');
call insert_translation('TYPE_MATERIAL', 'Materjalid', 'Materials');
call insert_translation('TYPE_PORTFOLIO', 'Kogumikud', 'Portfolios');

SET foreign_key_checks = 1;