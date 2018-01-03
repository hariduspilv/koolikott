SET foreign_key_checks = 0;

CREATE INDEX taxon_level ON Taxon(level);

SET foreign_key_checks = 1;
