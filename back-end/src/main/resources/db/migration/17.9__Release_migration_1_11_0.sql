SET foreign_key_checks = 0;

ALTER TABLE LearningObject
  ADD visibility VARCHAR(255) NOT NULL;

UPDATE LearningObject lo join Material m
    on lo.id = m.id
set lo.visibility = 'PUBLIC';

UPDATE LearningObject lo join Portfolio p
    on lo.id = p.id
set lo.visibility = p.visibility;

UPDATE LearningObject lo join ChapterObject c
    on lo.id = c.id
set lo.visibility = 'PUBLIC';

ALTER TABLE Portfolio DROP visibility;

SET foreign_key_checks = 1;
