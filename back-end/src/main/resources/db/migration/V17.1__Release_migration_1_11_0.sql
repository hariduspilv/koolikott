SET foreign_key_checks = 0;

ALTER TABLE ImproperContent
  CHANGE COLUMN deleted reviewed TINYINT(1) DEFAULT '0' NOT NULL, #rename only
  ADD reviewedBy BIGINT NULL,
  ADD reviewedAt TIMESTAMP NULL,
  ADD status VARCHAR(255) NULL,
  ADD CONSTRAINT FK_ImproperContent_User_id FOREIGN KEY (reviewedBy) REFERENCES User (id);

SET foreign_key_checks = 1;