SET foreign_key_checks = 0;

CREATE TABLE EmailToCreator
(
  id               BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
  senderName       VARCHAR(250) NOT NULL,
  senderEmail      VARCHAR(250) NOT NULL,
  message          TEXT         NOT NULL,
  sentSuccessfully BOOLEAN      NOT NULL DEFAULT 0,
  sentAt           TIMESTAMP    NULL,
  createdAt        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  user             BIGINT       NULL,
  errorMessage     VARCHAR(250) NULL,
  sentTries        INT          NOT NULL,
  FOREIGN KEY (user) REFERENCES User (id)
);

SET foreign_key_checks = 1;
