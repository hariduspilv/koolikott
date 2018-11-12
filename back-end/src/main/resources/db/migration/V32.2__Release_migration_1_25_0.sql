SET foreign_key_checks = 0;

UPDATE AuthenticatedUser
  SET deleted = 0;

SET foreign_key_checks = 1;