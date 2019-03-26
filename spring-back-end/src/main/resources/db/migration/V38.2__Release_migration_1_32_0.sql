SET foreign_key_checks = 0;

CREATE TABLE User_InterestTaxon(
  id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  user BIGINT(20) NOT NULL,
  taxon BIGINT(20) NOT NULL,
  CONSTRAINT FOREIGN KEY (user) REFERENCES User (id),
  CONSTRAINT FOREIGN KEY (taxon) REFERENCES Taxon (id)
);

SET foreign_key_checks = 1;