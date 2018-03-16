SET foreign_key_checks = 0;

CREATE INDEX Taxon_nameLowercase ON Taxon (nameLowercase);

SET foreign_key_checks = 1;