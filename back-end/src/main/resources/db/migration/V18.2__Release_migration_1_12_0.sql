SET foreign_key_checks = 0;

ALTER TABLE ReviewableChange
  DROP FOREIGN KEY ReviewableChange_ibfk_2;

ALTER TABLE ReviewableChange
  CHANGE COLUMN changer createdBy BIGINT NOT NULL,
  CHANGE COLUMN added createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  ADD COLUMN reviewed TINYINT(1) DEFAULT 0 NOT NULL,
  ADD reviewedBy BIGINT NULL,
  ADD reviewedAt TIMESTAMP NULL,
  ADD status VARCHAR(255) NULL,
  ADD CONSTRAINT FK_RC_CreatedBy_User_id FOREIGN KEY (createdBy) REFERENCES User (id),
  ADD CONSTRAINT FK_RC_ReviewedBy_User_id FOREIGN KEY (reviewedBy) REFERENCES User (id);
SET foreign_key_checks = 1;
