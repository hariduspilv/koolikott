SET foreign_key_checks = 0;

UPDATE Repository set Repository.used = true;

SET foreign_key_checks = 1;