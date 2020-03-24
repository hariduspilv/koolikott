SET foreign_key_checks = 0;

call insert_translation('FAQ_ORDER_CHANGED', 'Järjekord salvestatud!', 'Order saved!');
call insert_translation('FAQ_ORDER_CHANGE_FAIL', 'Järjekorra salvestamisel tekkis viga!', 'Save failed!');
call insert_translation('FAQ_ORDER_UPDATE', 'Uuenda KKK järjekorda', 'Update FAQ order');

SET foreign_key_checks = 1;