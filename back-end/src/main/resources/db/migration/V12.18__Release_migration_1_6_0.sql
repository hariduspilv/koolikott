SET foreign_key_checks = 0;

CREATE TABLE ChapterObject (
  id   BIGINT AUTO_INCREMENT PRIMARY KEY,
  text TEXT,

  FOREIGN KEY (id)
  REFERENCES LearningObject (id)
    ON DELETE RESTRICT
);

ALTER TABLE Chapter_Material
  RENAME TO Row_Material;

ALTER TABLE Row_Material
  ADD id BIGINT NOT NULL AUTO_INCREMENT UNIQUE;

CREATE TABLE ContentRow (
  id       BIGINT AUTO_INCREMENT PRIMARY KEY,
  material BIGINT,
  chapter  BIGINT
);

CREATE TABLE Chapter_Row (
  chapter  BIGINT  NOT NULL,
  row      BIGINT  NOT NULL,
  rowOrder INTEGER NOT NULL,
  PRIMARY KEY (chapter, row, rowOrder),

  UNIQUE KEY (chapter, row, rowOrder),

  FOREIGN KEY (chapter)
  REFERENCES Chapter (id)
    ON DELETE RESTRICT
);

INSERT INTO ContentRow (id, material, chapter) SELECT
                                                 Row_Material.id,
                                                 Row_Material.material,
                                                 Row_Material.chapter
                                               FROM Row_Material;

INSERT INTO Chapter_Row (chapter, row, rowOrder) SELECT
                                                   Row_Material.chapter,
                                                   ContentRow.id,
                                                   Row_Material.materialOrder
                                                 FROM Row_Material
                                                   JOIN ContentRow
                                                     ON Row_Material.material = ContentRow.material AND
                                                        Row_Material.chapter = ContentRow.chapter;

ALTER TABLE Row_Material
  CHANGE id row BIGINT;

ALTER TABLE Row_Material
  DROP FOREIGN KEY Row_Material_ibfk_1;

ALTER TABLE Row_Material
  DROP PRIMARY KEY,
  ADD CONSTRAINT PRIMARY KEY (row, material);

ALTER TABLE Row_Material
  DROP chapter;

ALTER TABLE Row_Material
  ADD CONSTRAINT FK_Row_Material_Content_Row FOREIGN KEY (row) REFERENCES ContentRow (id)
  ON DELETE RESTRICT;

ALTER TABLE Chapter_Row
  ADD CONSTRAINT FK_Chapter_Row_Content_Row FOREIGN KEY (row)
REFERENCES ContentRow (id)
  ON DELETE RESTRICT;

ALTER TABLE ContentRow
  DROP chapter;

ALTER TABLE ContentRow
  DROP material;

DROP INDEX id
ON Row_Material;

UPDATE Row_Material
SET materialOrder = 0
WHERE materialOrder > 0;

SET foreign_key_checks = 1;