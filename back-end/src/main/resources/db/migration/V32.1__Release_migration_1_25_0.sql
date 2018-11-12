SET foreign_key_checks = 0;

ALTER TABLE AuthenticatedUser
  ADD deleted BOOLEAN NULL DEFAULT FALSE;

SET foreign_key_checks = 1;