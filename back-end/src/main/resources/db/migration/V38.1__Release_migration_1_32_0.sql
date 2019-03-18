SET foreign_key_checks = 0;

CREATE TABLE User_Institution(
  id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  user BIGINT(20) NOT NULL,
  institution BIGINT(20) NOT NULL,
  CONSTRAINT FOREIGN KEY (user) REFERENCES User (id),
  CONSTRAINT FOREIGN KEY (institution) REFERENCES InstitutionEhis (id)
);

SET foreign_key_checks = 1;