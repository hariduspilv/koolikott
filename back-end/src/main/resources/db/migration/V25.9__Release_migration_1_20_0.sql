SET foreign_key_checks = 0;

UPDATE Portfolio p
  JOIN LearningObject lo ON p.id = lo.id
SET p.publishedAt = lo.added
where p.publishedAt is null;

SET foreign_key_checks = 1;
