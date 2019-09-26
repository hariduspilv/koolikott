SET foreign_key_checks = 0;

ALTER TABLE Portfolio
    ADD COLUMN copiedFromDirect BIGINT NULL;
ALTER TABLE Portfolio
    ADD COLUMN copiedFromOriginal BIGINT NULL;

SET foreign_key_checks = 1;