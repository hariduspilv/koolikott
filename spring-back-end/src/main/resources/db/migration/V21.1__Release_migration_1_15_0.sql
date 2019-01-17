SET foreign_key_checks = 0;

ALTER TABLE LearningObject
  ADD COLUMN publicationConfirmed BOOLEAN DEFAULT FALSE;

UPDATE LearningObject SET publicationConfirmed = TRUE WHERE visibility = 'PUBLIC';

SET foreign_key_checks = 1;
