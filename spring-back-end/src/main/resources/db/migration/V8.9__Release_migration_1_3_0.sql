SET foreign_key_checks = 0;

ALTER TABLE Chapter_Material DROP PRIMARY KEY, ADD PRIMARY KEY (chapter, material, materialOrder);

SET foreign_key_checks = 1;