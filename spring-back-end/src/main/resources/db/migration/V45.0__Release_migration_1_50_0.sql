SET foreign_key_checks = 0;

ALTER TABLE Portfolio CHANGE originalCreator originalOrCopiedFromDirectCreator BIGINT NOT NULL ;

SET foreign_key_checks = 1;