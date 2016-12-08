SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Kogumik loodud! Täienda seda võtmesõnadega ja jätkake alloleva tööriistariba abil peatükkide, alampeatükkide, sisutekstide ja materjalide lisamisega.' WHERE translationGroup = 1 AND translationKey = 'NEW_PORTFOLIO_CREATED_MESSAGE';

SET foreign_key_checks = 1;
