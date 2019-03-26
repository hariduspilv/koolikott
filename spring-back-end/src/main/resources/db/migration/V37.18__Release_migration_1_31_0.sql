SET foreign_key_checks = 0;

CREATE TABLE InstitutionEhis
(
  id               BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  ehisId           BIGINT NOT NULL,
  name             VARCHAR(255) NOT NULL
);

SET foreign_key_checks = 1;
