SET foreign_key_checks = 0;

ALTER TABLE AuthenticatedUser
  DROP homeOrganization,
  DROP mails,
  DROP affiliations,
  DROP scopedAffiliations;

SET foreign_key_checks = 1;