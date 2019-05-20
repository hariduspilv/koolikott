SET foreign_key_checks = 0;

ALTER TABLE LearningObject_Log ADD COLUMN isAutoSaved BOOLEAN DEFAULT 0;

SET foreign_key_checks = 1;