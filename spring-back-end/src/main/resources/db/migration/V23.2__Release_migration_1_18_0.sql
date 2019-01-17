SET foreign_key_checks = 0;

UPDATE Portfolio p
  JOIN LearningObject lo ON p.id = lo.id AND lo.visibility = 'PUBLIC'
SET p.publishedAt = lo.added ;

SET foreign_key_checks = 1;