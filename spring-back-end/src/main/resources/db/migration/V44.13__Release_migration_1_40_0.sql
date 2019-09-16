SET foreign_key_checks = 0;

call insert_translation('PORTFOLIO_COPIED', 'Tuletatud', 'Copied from');
call insert_translation('PORTFOLIO_PRIVATE_FROM', 'kogumikust(privaatne)', 'portfolio(private)');
call insert_translation('PORTFOLIO_DELETED_FROM', 'kogumikust(kustutatud)', 'portfolio(deleted)');
call insert_translation('PORTFOLIO_COPIED_FROM', 'kogumikust', 'portfolio');
call insert_translation('PORTFOLIO_COPY_MAKER', 'Lisaja', 'Copier');

SET foreign_key_checks = 1;