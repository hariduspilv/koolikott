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

call insert_translation('LICENSES_TAB', 'Litsentsitingimused', 'License terms');
call insert_translation('LICENSES_HEADER', 'Litsentsitingimused', 'License terms');
call insert_translation('ADD_LICENSES', 'Lisa litsentsitingimus', 'Add License terms');
call insert_translation('LICENSE_NOTIFY', 'Teavita kasutajaid uuest litsentitingimuste versioonist', 'Notify users of the license terms');

SET foreign_key_checks = 1;