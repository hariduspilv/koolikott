SET foreign_key_checks = 0;

ALTER TABLE Chapter
  ADD COLUMN deleted BOOLEAN NULL;

UPDATE Chapter
SET deleted = 1
WHERE portfolio IS NULL AND parentChapter IS NULL;

UPDATE Chapter c
SET c.deleted = 1
WHERE c.parentChapter IN (
  SELECT parent.id
  FROM (
         SELECT *
         FROM Chapter parent
         WHERE parent.portfolio IS NULL AND parent.parentChapter IS NULL
       ) parent
);


SET foreign_key_checks = 1;