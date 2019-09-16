SET foreign_key_checks = 0;

CREATE TABLE LicenceAgreement (
    id        BIGINT        AUTO_INCREMENT PRIMARY KEY,
    validFrom TIMESTAMP     NOT NULL,
    createdAt TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    createdBy BIGINT        NOT NULL,
    version   VARCHAR(255)  NOT NULL,
    url       VARCHAR(2000) NULL,
    deleted   BOOLEAN       NOT NULL DEFAULT FALSE,
    FOREIGN KEY (createdBy) REFERENCES User (id)
);

SET foreign_key_checks = 1;