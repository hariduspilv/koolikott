SET foreign_key_checks = 0;

CREATE TABLE ReportingReason (
  id              BIGINT AUTO_INCREMENT,
  improperContent BIGINT       NOT NULL,
  reason VARCHAR(255) NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (improperContent) REFERENCES ImproperContent (id)
);

ALTER TABLE ImproperContent
  DROP COLUMN reportingReason;

SET foreign_key_checks = 1;