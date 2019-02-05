SET foreign_key_checks = 0;

CREATE TABLE TaxonPosition
(
  id                 BIGINT AUTO_INCREMENT,
  taxon              BIGINT NOT NULL,
  educationalContext BIGINT NOT NULL,
  domain             BIGINT NULL,
  subject            BIGINT NULL,
  module             BIGINT NULL,
  specialization     BIGINT NULL,
  topic              BIGINT NULL,
  subtopic           BIGINT NULL
);

SET foreign_key_checks = 1;


