SET foreign_key_checks = 0;

CREATE TABLE UserLicenceAgreement(
    id               BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    user             BIGINT(20) NOT NULL,
    licenceAgreement BIGINT(20) NOT NULL,
    agreed           BOOLEAN    NOT NULL,
    disagreed        BOOLEAN    NOT NULL,
    clickedAt        TIMESTAMP  NOT NULL,
    CONSTRAINT FOREIGN KEY (user) REFERENCES User (id),
    CONSTRAINT FOREIGN KEY (licenceAgreement) REFERENCES LicenceAgreement (id)
);

SET foreign_key_checks = 1;