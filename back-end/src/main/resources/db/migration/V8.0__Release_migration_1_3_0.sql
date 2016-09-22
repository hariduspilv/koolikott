SET foreign_key_checks = 0;

UPDATE TagUpVote SET deleted = false WHERE deleted IS NULL;
ALTER TABLE TagUpVote MODIFY deleted BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE LearningObject SET deleted = false WHERE deleted IS NULL;
ALTER TABLE LearningObject MODIFY deleted BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE ImproperContent SET deleted = false WHERE deleted IS NULL;
ALTER TABLE ImproperContent MODIFY deleted BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE BrokenContent SET deleted = false WHERE deleted IS NULL;
ALTER TABLE BrokenContent MODIFY deleted BOOLEAN NOT NULL DEFAULT FALSE;

SET foreign_key_checks = 1;