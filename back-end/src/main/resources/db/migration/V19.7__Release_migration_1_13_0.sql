SET foreign_key_checks = 0;

ALTER TABLE Picture
  ADD COLUMN licenseType BIGINT NULL,
  ADD COLUMN author VARCHAR(255) NULL,
  ADD COLUMN source VARCHAR(255) NULL;

ALTER TABLE Picture DROP INDEX `name`;

CREATE INDEX Picture_name_index on Picture (name);
CREATE INDEX Thumbnail_name_index on Thumbnail (name);

SET foreign_key_checks = 1;