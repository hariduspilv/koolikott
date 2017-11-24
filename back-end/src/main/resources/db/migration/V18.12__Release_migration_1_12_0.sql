SET foreign_key_checks = 0;

ALTER TABLE ImproperContent
  ADD CONSTRAINT FK_IC_CreatedBy_User_id FOREIGN KEY (createdBy) REFERENCES User (id);

SET foreign_key_checks = 1;