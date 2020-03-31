SET foreign_key_checks = 0;

call insert_translation('THIS_MATERIAL_IS_PRIVATE', 'See materjal on privaatne', 'This material is private');
call insert_translation('SHARE_PRIVATE_MATERIAL', 'Selleks, et materjali jagada, tuleb selle staatust muuta. Seda on võimalik teha staatuse ikooni kaudu', 'You have to change the visibility state of the material to share it. The state can be changed using the state icon');
call update_translations('SHARE_PRIVATE_PORTFOLIO', 'Selleks, et kogumikku jagada, tuleb selle staatust muuta. Seda on võimalik teha staatuse ikooni kaudu', 'You have to change the visibility state of the portfolio to share it. The state can be changed using the state icon', 'Selleks, et kogumikku jagada, tuleb selle staatust muuta. Seda on võimalik teha staatuse ikooni kaudu (RU)');

SET foreign_key_checks = 1;