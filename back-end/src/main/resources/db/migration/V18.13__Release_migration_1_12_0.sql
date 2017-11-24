SET foreign_key_checks = 0;

ALTER TABLE ImproperContent
  ADD CONSTRAINT FK_IC_ReviewedBy_User_id FOREIGN KEY (reviewedBy) REFERENCES User (id);

SET foreign_key_checks = 1;