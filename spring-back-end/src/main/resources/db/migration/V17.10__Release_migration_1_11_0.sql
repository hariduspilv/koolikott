SET foreign_key_checks = 0;

ALTER TABLE FirstReview
  ADD status VARCHAR(255) NULL;

UPDATE FirstReview
SET status = 'ACCEPTED'
WHERE reviewed = 1;

SET foreign_key_checks = 1;
