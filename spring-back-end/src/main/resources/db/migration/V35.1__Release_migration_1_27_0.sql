SET foreign_key_checks = 0;

CREATE TABLE Faq (
  id          BIGINT AUTO_INCREMENT,
  questionEst       VARCHAR(255) NOT NULL,
  questionRus       VARCHAR(255) NOT NULL,
  questionEng       VARCHAR(255) NOT NULL,
  answerEst         TEXT         NOT NULL,
  answerRus         TEXT         NOT NULL,
  answerEng         TEXT         NOT NULL,
  createdAt   TIMESTAMP    NOT NULL,
  PRIMARY KEY (id)
);

SET foreign_key_checks = 1;