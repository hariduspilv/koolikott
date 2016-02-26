SET foreign_key_checks = 0;

-- TagUpVote start

CREATE TABLE TagUpVoteAux (
  id                BIGINT  AUTO_INCREMENT PRIMARY KEY,
  user              BIGINT NOT NULL,
  learningObject    BIGINT NOT NULL,
  tag               BIGINT NOT NULL,
  deleted           BOOLEAN DEFAULT FALSE,

  FOREIGN KEY (user)
  REFERENCES User (id)
    ON DELETE RESTRICT,

  FOREIGN KEY (learningObject)
  REFERENCES LearningObject (id)
    ON DELETE RESTRICT,

  FOREIGN KEY (tag)
  REFERENCES Tag (id)
    ON DELETE RESTRICT
);

INSERT INTO TagUpVoteAux (id, user, tag, deleted, learningObject) SELECT id, user, tag, deleted, if (tuv.material is not null, material, portfolio) FROM TagUpVote tuv;
DROP TABLE TagUpVote;
RENAME TABLE TagUpVoteAux TO TagUpVote;

-- TagUpVote end

SET foreign_key_checks = 1;