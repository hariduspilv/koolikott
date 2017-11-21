SET foreign_key_checks = 0;

CREATE TABLE ChapterBlock
(
  id          BIGINT PRIMARY KEY AUTO_INCREMENT,
  narrow      BOOLEAN            DEFAULT FALSE,
  htmlContent TEXT NULL
);

CREATE TABLE Chapter_ChapterBlock (
  chapter      BIGINT  NOT NULL,
  chapterBlock BIGINT  NOT NULL,
  rowOrder     INTEGER NOT NULL,
  PRIMARY KEY (chapter, chapterBlock, rowOrder),

  UNIQUE KEY (chapter, chapterBlock, rowOrder),

  FOREIGN KEY (chapter)
  REFERENCES Chapter (id)
    ON DELETE RESTRICT,

  FOREIGN KEY (chapterBlock)
  REFERENCES ChapterBlock (id)
    ON DELETE RESTRICT
);


SET foreign_key_checks = 1;