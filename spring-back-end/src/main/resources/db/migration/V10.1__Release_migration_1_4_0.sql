SET foreign_key_checks = 0;

CREATE TABLE User_Taxon(
  id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  user BIGINT(20) NOT NULL,
  taxon BIGINT(20) NOT NULL,
  CONSTRAINT FOREIGN KEY (user) REFERENCES User (id),
  CONSTRAINT FOREIGN KEY (taxon) REFERENCES Taxon (id)
);

INSERT INTO User_Taxon(user, taxon) VALUES (13, 103);
INSERT INTO User_Taxon(user, taxon) VALUES (13, 1055);
INSERT INTO User_Taxon(user, taxon) VALUES (10, 1);
INSERT INTO User_Taxon(user, taxon) VALUES (10, 1060);
INSERT INTO User_Taxon(user, taxon) VALUES (10, 1014);

SET foreign_key_checks = 1;
