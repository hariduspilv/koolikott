SET foreign_key_checks = 0;

CREATE TABLE Licenses (
    id          BIGINT AUTO_INCREMENT,
    titleEst       VARCHAR(255) NOT NULL,
    titleRus       VARCHAR(255) NOT NULL,
    titleEng       VARCHAR(255) NOT NULL,
    contentEst         TEXT         NOT NULL,
    contentRus         TEXT         NOT NULL,
    contentEng         TEXT         NOT NULL,
    createdAt   TIMESTAMP    NOT NULL,
    PRIMARY KEY (id)
);

SET foreign_key_checks = 1;