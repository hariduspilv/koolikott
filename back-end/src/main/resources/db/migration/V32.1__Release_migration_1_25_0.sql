SET foreign_key_checks = 0;

ALTER TABLE AuthenticatedUser
  ADD deleted BOOLEAN NULL DEFAULT FALSE,
  ADD loggedOut BOOLEAN NULL DEFAULT FALSE,
  ADD declined BOOLEAN NULL DEFAULT FALSE,
  ADD sessionTime TIMESTAMP NULL,
  ADD sessionNumber INTEGER NULL;

SET foreign_key_checks = 1;