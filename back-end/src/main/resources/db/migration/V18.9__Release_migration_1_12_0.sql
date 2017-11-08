SET foreign_key_checks = 0;

ALTER TABLE ImproperContent
  DROP FOREIGN KEY ImproperContent_ibfk_1;
ALTER TABLE ImproperContent
  DROP FOREIGN KEY FK_ImproperContent_User_id;

SET foreign_key_checks = 1;