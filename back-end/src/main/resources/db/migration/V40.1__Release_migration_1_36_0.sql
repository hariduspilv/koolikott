USE dop;
SET foreign_key_checks = 0;

CREATE TABLE IF NOT EXISTS LearningObject_Log
(
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    learningObject       BIGINT,
    createdAt            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    added                TIMESTAMP    NULL,
    deleted              BOOLEAN      NOT NULL DEFAULT FALSE,
    updated              TIMESTAMP    NULL     DEFAULT NULL,
    views                BIGINT       NOT NULL DEFAULT 0,
    creator              BIGINT,
    recommendation       BIGINT,
    lastInteraction      TIMESTAMP    NULL     DEFAULT NULL,
    picture              BIGINT,
    visibility           VARCHAR(255) NOT NULL,
    publicationConfirmed BOOLEAN               DEFAULT FALSE,
    licenseType          BIGINT       NULL,

    FOREIGN KEY (creator)
        REFERENCES User (id)
        ON DELETE RESTRICT,

    FOREIGN KEY (recommendation)
        REFERENCES Recommendation (id)
        ON DELETE RESTRICT,

    FOREIGN KEY (licenseType)
        REFERENCES LicenseType (id),

    FOREIGN KEY (picture)
        REFERENCES Picture (id),

    FOREIGN KEY (learningObject)
        REFERENCES LearningObject (id)
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS PortfolioLog
(
    id              BIGINT PRIMARY KEY,
    title           VARCHAR(255) NOT NULL,
    originalCreator BIGINT       NOT NULL,
    summary         TEXT,
    publishedAt     TIMESTAMP    NULL,

    FOREIGN KEY (id)
        REFERENCES LearningObject_Log (id)
        ON DELETE RESTRICT,

    FOREIGN KEY (originalCreator)
        REFERENCES User (id)
        ON DELETE RESTRICT

);

CREATE TABLE IF NOT EXISTS LearningObject_CrossCurricularTheme_Log
(
    learningObject       BIGINT NOT NULL,
    crossCurricularTheme BIGINT NOT NULL,

    PRIMARY KEY (learningObject, crossCurricularTheme),

    FOREIGN KEY (learningObject)
        REFERENCES LearningObject_Log (id)
        ON DELETE RESTRICT,

    FOREIGN KEY (crossCurricularTheme)
        REFERENCES CrossCurricularTheme (id)
        ON DELETE RESTRICT
);
CREATE TABLE IF NOT EXISTS LearningObject_KeyCompetence_Log
(
    learningObject BIGINT NOT NULL,
    keyCompetence  BIGINT NOT NULL,

    PRIMARY KEY (learningObject, keyCompetence),

    FOREIGN KEY (learningObject)
        REFERENCES LearningObject_Log (id)
        ON DELETE RESTRICT,

    FOREIGN KEY (keyCompetence)
        REFERENCES KeyCompetence (id)
        ON DELETE RESTRICT
);
CREATE TABLE IF NOT EXISTS LearningObject_Tag_Log
(
    learningObject BIGINT NOT NULL,
    tag            BIGINT NOT NULL,

    PRIMARY KEY (learningObject, tag),

    FOREIGN KEY (learningObject)
        REFERENCES LearningObject_Log (id)
        ON DELETE RESTRICT,

    FOREIGN KEY (tag)
        REFERENCES Tag (id)
        ON DELETE RESTRICT
);
CREATE TABLE IF NOT EXISTS LearningObject_TargetGroup_Log
(
    learningObject BIGINT NOT NULL,
    targetGroup    BIGINT,

    PRIMARY KEY (learningObject, targetGroup),

    FOREIGN KEY (learningObject)
        REFERENCES LearningObject_Log (id)
        ON DELETE RESTRICT,

    FOREIGN KEY (targetGroup)
        REFERENCES TargetGroup (id)
);

CREATE TABLE IF NOT EXISTS LearningObject_Taxon_Log
(
    learningObject BIGINT,
    taxon          BIGINT,
    PRIMARY KEY (learningObject, taxon),
    FOREIGN KEY (learningObject) REFERENCES LearningObject_Log (id),
    FOREIGN KEY (taxon) REFERENCES Taxon (id)
);

CREATE TABLE IF NOT EXISTS Chapter_Log
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    title         VARCHAR(255) NOT NULL,
    textValue     TEXT,
    parentChapter BIGINT,
    portfolio     BIGINT,
    chapterOrder  INTEGER,
    deleted       BOOLEAN      NULL,

    FOREIGN KEY (portfolio)
        REFERENCES PortfolioLog (id)
        ON DELETE RESTRICT,

    FOREIGN KEY (parentChapter)
        REFERENCES Chapter_Log (id)
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS Chapter_ChapterBlock_Log
(
    chapter      BIGINT  NOT NULL,
    chapterBlock BIGINT  NOT NULL,
    rowOrder     INTEGER NOT NULL,
    PRIMARY KEY (chapter, chapterBlock, rowOrder),

    UNIQUE KEY (chapter, chapterBlock, rowOrder),

    FOREIGN KEY (chapter)
        REFERENCES Chapter_Log (id)
        ON DELETE RESTRICT,

    FOREIGN KEY (chapterBlock)
        REFERENCES ChapterBlock_Log (id)
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS Chapter_Row_Log
(
    chapter  BIGINT  NOT NULL,
    row      BIGINT  NOT NULL,
    rowOrder INTEGER NOT NULL,
    PRIMARY KEY (chapter, row, rowOrder),

    UNIQUE KEY (chapter, row, rowOrder),

    FOREIGN KEY (chapter)
        REFERENCES Chapter_Log (id)
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS ChapterBlock_Log
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    narrow      BOOLEAN DEFAULT FALSE,
    htmlContent TEXT NULL
);
SET foreign_key_checks = 1;