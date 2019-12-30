SET foreign_key_checks = 0;

DROP TABLE IF EXISTS GdprProcessTerms;

ALTER TABLE Terms
    ADD createdBy BIGINT(20) NOT NULL,
    ADD agreement BIGINT(20) NOT NULL,
    ADD type VARCHAR(50),
    ADD CONSTRAINT FOREIGN KEY (createdBy) REFERENCES User (id),
    ADD CONSTRAINT FOREIGN KEY (agreement) REFERENCES Agreement (id);

ALTER TABLE Agreement
    ADD type VARCHAR(50),
    DROP COLUMN version,
    DROP COLUMN validFrom;

SET foreign_key_checks = 1;
