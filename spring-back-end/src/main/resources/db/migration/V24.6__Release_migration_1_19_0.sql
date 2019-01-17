SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Sisene või loo konto' WHERE translationKey = 'LOGIN_CHOOSE_LOGIN_METHOD' and translationGroup = 1;
UPDATE Translation SET translation = 'Login or create account' WHERE translationKey = 'LOGIN_CHOOSE_LOGIN_METHOD' and translationGroup = 3;

SET foreign_key_checks = 1;