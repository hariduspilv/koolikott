USE dop;

CREATE TABLE Portfolio_History
(
    id              BIGINT PRIMARY KEY,
    loId            BIGINT,
    title           VARCHAR(255) NOT NULL,
    originalCreator BIGINT       NOT NULL,
    summary         TEXT,
    createdAt       TIMESTAMP NULL,

    FOREIGN KEY (id)
        REFERENCES LearningObject (id)
        ON DELETE RESTRICT,

    FOREIGN KEY (loId)
        REFERENCES LearningObject_Snapshot (id)
        ON DELETE RESTRICT
);

CREATE TABLE LearningObject_Snapshot
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    added          TIMESTAMP          DEFAULT CURRENT_TIMESTAMP,
    deleted        BOOLEAN,
    updated        TIMESTAMP NULL     DEFAULT NULL,
    views          BIGINT    NOT NULL DEFAULT 0,
    creator        BIGINT,
    recommendation BIGINT,
    lastInteraction TIMESTAMP NULL DEFAULT NULL,
    picture        LONGBLOB           DEFAULT NULL,
    visibility VARCHAR(255) NOT NULL,
    publicationConfirmed BOOLEAN,

    FOREIGN KEY (creator)
        REFERENCES User (id)
        ON DELETE RESTRICT,

    FOREIGN KEY (recommendation)
        REFERENCES Recommendation (id)
        ON DELETE RESTRICT
);
CREATE TABLE LearningObject_Snapshot
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    portfolioHistoryId  BIGINT
);

CREATE TABLE LearningObject_CrossCurricularTheme_Snapshot
(
    learningObject       BIGINT NOT NULL,
    crossCurricularTheme BIGINT NOT NULL,

    PRIMARY KEY (learningObject, crossCurricularTheme),

    FOREIGN KEY (learningObject)
        REFERENCES LearningObject_Snapshot (id)
        ON DELETE RESTRICT,

    FOREIGN KEY (crossCurricularTheme)
        REFERENCES CrossCurricularTheme (id)
        ON DELETE RESTRICT
);

CREATE TABLE LearningObject_KeyCompetence_Snapshot
(
    learningObject BIGINT NOT NULL,
    keyCompetence  BIGINT NOT NULL,

    PRIMARY KEY (learningObject, keyCompetence),

    FOREIGN KEY (learningObject)
        REFERENCES LearningObject_Snapshot (id)
        ON DELETE RESTRICT,

    FOREIGN KEY (keyCompetence)
        REFERENCES KeyCompetence (id)
        ON DELETE RESTRICT
);

CREATE TABLE LearningObject_Tag_Snapshot
(
    learningObject BIGINT NOT NULL,
    tag            BIGINT NOT NULL,

    PRIMARY KEY (learningObject, tag),

    FOREIGN KEY (learningObject)
        REFERENCES LearningObject_Snapshot (id)
        ON DELETE RESTRICT,

    FOREIGN KEY (tag)
        REFERENCES Tag (id)
        ON DELETE RESTRICT
);

CREATE TABLE LearningObject_TargetGroup_Snapshot
(
    learningObject BIGINT NOT NULL,
    targetGroup    VARCHAR(255),

    PRIMARY KEY (learningObject, targetGroup),

    FOREIGN KEY (learningObject)
        REFERENCES LearningObject_Snapshot (id)
        ON DELETE RESTRICT,

    FOREIGN KEY (targetGroup)
        REFERENCES TargetGroup (id)
);

CREATE TABLE LearningObject_Taxon_Snapshot
(
    learningObject BIGINT,
    taxon          BIGINT,
    PRIMARY KEY (learningObject, taxon),
    FOREIGN KEY (learningObject) REFERENCES LearningObject_Snapshot (id),
    FOREIGN KEY (taxon) REFERENCES Taxon (id)
);

CREATE TABLE Chapter_Snapshot
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    title         VARCHAR(255) NOT NULL,
    textValue     TEXT,
    parentChapter BIGINT,
    portfolio     BIGINT,
    chapterOrder  INTEGER,
    deleted BOOLEAN NULL,

    FOREIGN KEY (portfolio)
        REFERENCES Portfolio_History (loId)
        ON DELETE RESTRICT,

    FOREIGN KEY (parentChapter)
        REFERENCES Chapter_Snapshot (id)
        ON DELETE RESTRICT
);

CREATE TABLE Chapter_ChapterBlock_Snapshot
(
    chapter      BIGINT  NOT NULL,
    chapterBlock BIGINT  NOT NULL,
    rowOrder     INTEGER NOT NULL,
    PRIMARY KEY (chapter, chapterBlock, rowOrder),

    UNIQUE KEY (chapter, chapterBlock, rowOrder),

    FOREIGN KEY (chapter)
        REFERENCES Chapter_Snapshot (id)
        ON DELETE RESTRICT,

    FOREIGN KEY (chapterBlock)
        REFERENCES ChapterBlock_Snapshot (id)
        ON DELETE RESTRICT
);

CREATE TABLE Chapter_Row_Snapshot
(
chapter  BIGINT  NOT NULL,
row      BIGINT  NOT NULL,
rowOrder INTEGER NOT NULL,
PRIMARY KEY (chapter, row, rowOrder),

UNIQUE KEY (chapter, row, rowOrder),

FOREIGN KEY (chapter)
REFERENCES Chapter_Snapshot (id)
ON DELETE RESTRICT
);

CREATE TABLE ChapterBlock_Snapshot
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    narrow      BOOLEAN DEFAULT FALSE,
    htmlContent TEXT NULL
);
