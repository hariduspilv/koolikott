SET foreign_key_checks = 0;

CREATE TABLE UserManuals (
  id          BIGINT AUTO_INCREMENT,
  title       VARCHAR(255) NOT NULL,
  url         TEXT         NOT NULL,
  textUrl     TEXT		     NULL,
  createdBy   BIGINT       NOT NULL,
  createdAt   TIMESTAMP    NOT NULL,
  PRIMARY KEY (id)
);

ALTER TABLE UserManuals
  ADD CONSTRAINT FK_UM_CreatedBy_User_id FOREIGN KEY (createdBy) REFERENCES User (id);

SET foreign_key_checks = 1;