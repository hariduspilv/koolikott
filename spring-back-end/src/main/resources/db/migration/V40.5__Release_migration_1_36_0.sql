SET foreign_key_checks = 0;

ALTER TABLE LearningObject_Log ADD COLUMN saveType VARCHAR(255);

SET foreign_key_checks = 1;