SET foreign_key_checks = 0;

CREATE TABLE FirstReview (
  id             BIGINT  AUTO_INCREMENT,
  learningObject BIGINT NOT NULL,
  reviewed       BOOLEAN NOT NULL DEFAULT FALSE,
  reviewedBy     BIGINT NULL ,
  createdAt      TIMESTAMP NULL ,
  reviewedAt     TIMESTAMP NULL ,
  PRIMARY KEY (id),
  FOREIGN KEY (learningObject) REFERENCES LearningObject (id),
  FOREIGN KEY (reviewedBy) REFERENCES User (id)
);

SET foreign_key_checks = 1;