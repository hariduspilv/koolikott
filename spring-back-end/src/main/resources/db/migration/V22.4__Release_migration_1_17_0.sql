SET foreign_key_checks = 0;

ALTER TABLE Repository CHANGE isEstonianPublisher contentIsEmbeddable BOOLEAN NOT NULL ;

SET foreign_key_checks = 1;