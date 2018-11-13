SET foreign_key_checks = 0;

UPDATE AuthenticatedUser
SET deleted       = 1,
    loggedOut     = 0,
    declined      = 0,
    sessionTime   = DATE_ADD(loginDate, INTERVAL 1 DAY),
    sessionNumber = 1;

SET foreign_key_checks = 1;