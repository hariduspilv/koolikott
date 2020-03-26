SET foreign_key_checks = 0;

call update_translations('TO_ADD_PORTFOLIO_PRESS_ADD_MATERIAL', 'Kogumiku loomiseks vajuta all paremal asuval (+) nupul "Loo kogumik".',  'To create portfolio click "Create portfolio" on the bottom right (+) button.', 'Для того, чтобы добавить коллекцию попали в нижний правый (+), нажмите на кнопку "Добавить в подборку".');
call update_translations('ADD_PORTFOLIO_GUIDE', 'Kogumiku loomise kohta vaata <a href="/videojuhendid" target="_blank">videojuhendit</a>.', 'Watch the <a href="/videojuhendid" target="_blank">video guide</a> about creating a portfolio.', 'Kogumiku loomise kohta vaata <a href="/videojuhendid" target="_blank">videojuhendit</a>. (RU)');
call update_translations('ADD_MATERIAL_GUIDE', 'Materjali lisamise kohta vaata <a href="/videojuhendid" target="_blank">videojuhendit</a>.', 'Watch the <a href="/videojuhendid" target="_blank">video guide</a> about adding a material.', 'Materjali lisamise kohta vaata <a href="/videojuhendid" target="_blank">videojuhendit</a>. (RU)');

SET foreign_key_checks = 1;