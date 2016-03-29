SET foreign_key_checks = 0;

-- Moving LearningObject picture to a separate table start

CREATE TABLE Picture (
  id    BIGINT  AUTO_INCREMENT PRIMARY KEY,
  name  VARCHAR(250) NOT NULL UNIQUE,
  data  LONGBLOB NOT NULL
);

INSERT INTO Picture (name, data) SELECT sha1(lo.picture), picture FROM LearningObject lo WHERE lo.picture IS NOT NULL GROUP BY lo.picture;

ALTER TABLE LearningObject ADD pic BIGINT;

UPDATE LearningObject lo 
    SET lo.pic = (SELECT p.id FROM Picture p WHERE p.data = lo.picture)
    WHERE lo.picture IS NOT NULL;

ALTER TABLE LearningObject DROP picture;

ALTER TABLE LearningObject
    ADD picture BIGINT,
    ADD CONSTRAINT FOREIGN KEY (picture) REFERENCES Picture(id) ON DELETE RESTRICT;

UPDATE LearningObject SET picture = pic WHERE pic is not null;

ALTER TABLE LearningObject DROP pic;

-- Moving LearningObject picture to a separate table end

SET foreign_key_checks = 1;
