SET foreign_key_checks = 0;

CREATE TABLE TaxonPosition
(
  id                 BIGINT AUTO_INCREMENT PRIMARY KEY ,
  taxon              BIGINT NOT NULL,
  educationalContext BIGINT NOT NULL,
  domain             BIGINT NULL,
  subject            BIGINT NULL,
  module             BIGINT NULL,
  specialization     BIGINT NULL,
  topic              BIGINT NULL,
  subtopic           BIGINT NULL
);

ALTER TABLE TaxonPosition
  ADD CONSTRAINT FK_TaxonPosition_taxon FOREIGN KEY (taxon) REFERENCES Taxon (id),
  ADD CONSTRAINT FK_TaxonPosition_educationalContext FOREIGN KEY (educationalContext) REFERENCES Taxon (id),
  ADD CONSTRAINT FK_TaxonPosition_domain FOREIGN KEY (taxon) REFERENCES Taxon (id),
  ADD CONSTRAINT FK_TaxonPosition_subject FOREIGN KEY (taxon) REFERENCES Taxon (id),
  ADD CONSTRAINT FK_TaxonPosition_module FOREIGN KEY (taxon) REFERENCES Taxon (id),
  ADD CONSTRAINT FK_TaxonPosition_specialization FOREIGN KEY (taxon) REFERENCES Taxon (id),
  ADD CONSTRAINT FK_TaxonPosition_topic FOREIGN KEY (taxon) REFERENCES Taxon (id),
  ADD CONSTRAINT FK_TaxonPosition_subtopic FOREIGN KEY (taxon) REFERENCES Taxon (id);

SET foreign_key_checks = 1;

