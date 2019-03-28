SET foreign_key_checks = 0;

call insert_translation('PORTFOLIO_PRIVATE', 'Privaatne kogumik', 'Private portfolio');
call insert_translation('PORTFOLIO_SHOW_LESS', 'Näita vähem', 'Show less');
call insert_translation('PORTFOLIO_SHOW_MORE', 'Näita veel', 'Show more');
call insert_translation('PORTFOLIO_RELATED_PORTFOLIOS', 'Seotud kogumikud', 'Related portfolios');

SET foreign_key_checks = 1;