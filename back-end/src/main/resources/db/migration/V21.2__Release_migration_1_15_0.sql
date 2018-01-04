SET foreign_key_checks = 0;

ALTER TABLE LearningObject
  ADD COLUMN licenseType BIGINT NULL;

UPDATE LearningObject lo
SET lo.licenseType = (
  SELECT m.licenseType
  FROM Material m
  WHERE m.id = lo.id);

ALTER TABLE Material
  DROP COLUMN licenseType;

SET foreign_key_checks = 1;
