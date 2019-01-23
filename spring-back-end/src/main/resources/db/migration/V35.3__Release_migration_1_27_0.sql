SET foreign_key_checks = 0;

call insert_translation('FAQ_SAVED', 'Sisuplokk salvestatud', 'FAQ Saved');
call insert_translation('FAQ_SAVE_FAILED', 'Sisuploki salvestamine ebaõnnestus', 'FAQ Save Failed');
call insert_translation('FAQ_DELETED', 'Sisuplokk kustutatud', 'FAQ Deleted');
call insert_translation('CUSTOMER_SUPPORT_ASK_OR_FAQ', 'Vaata korduma kippuvaid küsimusi <a href="/faq">siit</a> või vali teema mille osas soovid abi küsida', 'Check the FAQ <a href="/faq">here</a> or choose a topic you need help with');

SET foreign_key_checks = 1;