SET foreign_key_checks = 0;

CREATE TABLE UserTourData (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  user BIGINT(20) NOT NULL,
  editTour TINYINT(1) DEFAULT '0' NOT NULL,
  generalTour TINYINT(1) DEFAULT '0' NOT NULL,
  PRIMARY KEY (id, user),
  FOREIGN KEY (user) REFERENCES User(id)
);

SET foreign_key_checks = 1;
