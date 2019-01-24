SET foreign_key_checks = 0;

ALTER TABLE UploadedFile MODIFY name TEXT NOT NULL;

SET foreign_key_checks = 1;
