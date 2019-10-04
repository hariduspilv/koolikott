SET foreign_key_checks = 0;

call update_translations ('PORTFOLIO_PRIVATE_FROM', 'kogumikust (privaatne)', 'portfolio(private)', 'kogumikust (privaatne) (RU)');
call update_translations ('PORTFOLIO_DELETED_FROM', 'kogumikust (kustutatud)', 'portfolio(kustutatud)', 'kogumikust (kustutatud) (RU)');

SET foreign_key_checks = 1;