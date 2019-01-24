SET foreign_key_checks = 0;

CREATE TABLE User_Agreement(
  id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  user BIGINT(20) NOT NULL,
  agreement BIGINT(20) NOT NULL,
  CONSTRAINT FOREIGN KEY (user) REFERENCES User (id),
  CONSTRAINT FOREIGN KEY (agreement) REFERENCES Agreement (id)
);

SET foreign_key_checks = 1;