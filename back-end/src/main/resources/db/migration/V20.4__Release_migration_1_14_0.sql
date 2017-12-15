SET foreign_key_checks = 0;

CREATE TABLE Media (
  id          BIGINT AUTO_INCREMENT,
  title       VARCHAR(255) NULL,
  url         TEXT         NOT NULL,
  source      VARCHAR(255) NULL,
  author      VARCHAR(255) NULL,
  licenseType BIGINT       NULL,
  createdBy   BIGINT       NOT NULL,
  createdAt   TIMESTAMP    NOT NULL,
  PRIMARY KEY (id)
);

ALTER TABLE Media
  ADD CONSTRAINT media_licenceType_FK
FOREIGN KEY (licenseType) REFERENCES LicenseType (id);

ALTER TABLE Media
  ADD CONSTRAINT media_createdBy_FK
FOREIGN KEY (createdBy) REFERENCES User (id);

ALTER TABLE Picture
  ADD CONSTRAINT picture_licenceType_FK
FOREIGN KEY (licenseType) REFERENCES LicenseType (id);

SET foreign_key_checks = 1;