SET foreign_key_checks = 0;

CREATE TABLE UserFavorite (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  creator        BIGINT    NOT NULL,
  learningObject BIGINT    NOT NULL,
  added          TIMESTAMP NULL NOT NULL,

  UNIQUE KEY (creator, learningObject),

  FOREIGN KEY (creator)
  REFERENCES User (id)
    ON DELETE RESTRICT,

  FOREIGN KEY (learningObject)
  REFERENCES LearningObject (id)
    ON DELETE RESTRICT
);

SET foreign_key_checks = 1;