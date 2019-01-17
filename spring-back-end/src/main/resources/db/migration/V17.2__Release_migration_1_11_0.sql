SET foreign_key_checks = 0;

UPDATE ImproperContent
SET status = 'ACCEPTED'
WHERE reviewed = 1;

SET foreign_key_checks = 1;