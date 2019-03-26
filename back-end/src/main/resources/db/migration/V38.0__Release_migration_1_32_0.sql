SET foreign_key_checks = 0;

CREATE TABLE UserProfile(
  id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  user BIGINT(20) NOT NULL,
  lastUpdate TIMESTAMP NULL,
  role VARCHAR(10),
  CONSTRAINT FOREIGN KEY (user) REFERENCES User (id)
);

SET foreign_key_checks = 1;