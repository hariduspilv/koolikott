
# old logic cleanup
DELETE FROM Row_Material WHERE row in (SELECT row from Chapter_Row WHERE chapter in (select id from Chapter WHERE deleted = 1));
DELETE FROM Chapter_Row WHERE chapter in (select id from Chapter WHERE deleted = 1);
DELETE FROM ContentRow WHERE id in (SELECT row from Chapter_Row WHERE chapter in (select id from Chapter WHERE deleted = 1));

# new logic cleanup
DELETE FROM Chapter_ChapterBlock WHERE chapter in (select id from Chapter WHERE portfolio is NULL);
DELETE FROM ChapterBlock WHERE id in (SELECT chapterBlock from Chapter_ChapterBlock WHERE chapter in (select id from Chapter WHERE portfolio is NULL));

# chapter cleanup
DELETE d from Chapter d
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
                  WHERE deleted = 1
                ) parent
         )
       ) sub
);

DELETE c FROM Chapter c
WHERE c.parentChapter IN (
  SELECT parent.id
  FROM (
         SELECT *
         FROM Chapter parent
         WHERE deleted = 1
       ) parent
);

DELETE from Chapter
WHERE deleted = 1;