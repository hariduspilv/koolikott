SET foreign_key_checks = 0;

ALTER TABLE dop.UploadedFile MODIFY name TEXT NOT NULL;

SET foreign_key_checks = 1;
