SET foreign_key_checks = 0;

CREATE TABLE Portfolio_Taxon (
  portfolio  BIGINT,
  taxon BIGINT,
  PRIMARY KEY (portfolio, taxon),
  FOREIGN KEY (portfolio) REFERENCES Portfolio(id),
  FOREIGN KEY (taxon) REFERENCES Taxon(id)
);

INSERT INTO Portfolio_Taxon(portfolio, taxon) SELECT id, taxon from Portfolio WHERE taxon IS NOT NULL;

ALTER TABLE Portfolio DROP FOREIGN KEY `Portfolio_ibfk_3`;

ALTER TABLE Portfolio DROP COLUMN taxon;

SET foreign_key_checks = 1;
