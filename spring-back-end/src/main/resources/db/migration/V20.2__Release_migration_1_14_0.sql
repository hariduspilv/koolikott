SET foreign_key_checks = 0;

# lvl 1
UPDATE Chapter
SET deleted = 1
WHERE portfolio IS NULL AND parentChapter IS NULL;

# lvl 2
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

# lvl 3
UPDATE Chapter d
SET d.deleted = 1
WHERE d.parentChapter IN (
  SELECT sub.id
  FROM (
         SELECT *
         FROM
           Chapter c
         WHERE c.parentChapter IN (
           SELECT parent.id
           FROM (
                  SELECT *
                  FROM Chapter parent
                  WHERE parent.portfolio IS NULL AND parent.parentChapter IS NULL
                ) parent
         )
       ) sub
);

SET foreign_key_checks = 1;
