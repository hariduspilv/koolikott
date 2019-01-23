SET foreign_key_checks = 0;

update Translation set translation = 'Vaata korduma kippuvaid küsimusi <a href="/faq" target="_blank">SIIT</a> või vali teema mille osas soovid abi küsida' where translationKey ='CUSTOMER_SUPPORT_ASK_OR_FAQ' and translationGroup = 1;
update Translation set translation = 'Vaata korduma kippuvaid küsimusi <a href="/faq" target="_blank">SIIT</a> või vali teema mille osas soovid abi küsida' where translationKey ='CUSTOMER_SUPPORT_ASK_OR_FAQ' and translationGroup = 2;
update Translation set translation = 'Check the FAQ <a href="/faq" target="_blank">HERE<a> or choose a topic you need help with' where translationKey ='CUSTOMER_SUPPORT_ASK_OR_FAQ' and translationGroup = 3;

SET foreign_key_checks = 1;