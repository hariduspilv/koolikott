SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Check the FAQ <a href="/faq" target="_blank">HERE</a> or choose a topic you need help with' WHERE translationKey = 'CUSTOMER_SUPPORT_ASK_OR_FAQ' AND translationGroup = 3;
UPDATE Translation SET translation = 'To exit fullscreen mode use ESC or click the fullscreen button again' WHERE translationKey = 'YOU_CAN_LEAVE_PAGE_WITH_ESC' AND translationGroup = 3;


SET foreign_key_checks = 1;


