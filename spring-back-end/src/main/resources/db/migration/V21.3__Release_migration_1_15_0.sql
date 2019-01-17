SET foreign_key_checks = 0;

ALTER TABLE LearningObject
    ADD CONSTRAINT FOREIGN KEY(licenseType) REFERENCES LicenseType(id);

SET foreign_key_checks = 1;
