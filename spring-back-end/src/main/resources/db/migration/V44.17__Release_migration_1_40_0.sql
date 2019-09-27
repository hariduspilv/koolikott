SET foreign_key_checks = 0;

ALTER TABLE UserLicenceAgreement CHANGE clickedAt
    clickedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

SET foreign_key_checks = 1;