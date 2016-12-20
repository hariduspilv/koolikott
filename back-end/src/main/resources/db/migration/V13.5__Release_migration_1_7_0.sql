SET foreign_key_checks = 0;

ALTER TABLE ChangedLearningObject ADD added TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL;

SET foreign_key_checks = 1;
