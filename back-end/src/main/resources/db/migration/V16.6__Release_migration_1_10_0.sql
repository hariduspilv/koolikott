SET foreign_key_checks = 0;

INSERT INTO FirstReview (learningObject, reviewed, createdAt)
  SELECT
    lo.id,
    0,
    lo.added
  FROM LearningObject lo
  WHERE lo.deleted = 0;

SET foreign_key_checks = 1;