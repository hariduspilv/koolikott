-- Drop tables

DROP TABLE IF EXISTS Translation;
DROP TABLE IF EXISTS TranslationGroup;
DROP TABLE IF EXISTS Material_EducationalContext;
DROP TABLE IF EXISTS Material_ResourceType;
DROP TABLE IF EXISTS Material_Description;
DROP TABLE IF EXISTS Material_Title;
DROP TABLE IF EXISTS Material_Publisher;
DROP TABLE IF EXISTS Material_Classification;
DROP TABLE IF EXISTS Material_Author;
DROP TABLE IF EXISTS LanguageString;
DROP TABLE IF EXISTS LanguageKeyCodes;
DROP TABLE IF EXISTS Material;
DROP TABLE IF EXISTS LicenseType;
DROP TABLE IF EXISTS LanguageTable;
DROP TABLE IF EXISTS IssueDate;
DROP TABLE IF EXISTS Publisher;
DROP TABLE IF EXISTS Classification;
DROP TABLE IF EXISTS EducationalContext;
DROP TABLE IF EXISTS ResourceType;
DROP TABLE IF EXISTS Author;

-- Create tables

CREATE TABLE Author(
  id      BIGINT AUTO_INCREMENT PRIMARY KEY,
  name    VARCHAR(255) NOT NULL,
  surname VARCHAR(255) NOT NULL
);

CREATE TABLE ResourceType (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  resourceType VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE EducationalContext (
  id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
  educationalContext VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE Classification (
  id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
  classificationName VARCHAR(255) NOT NULL,
  parent             BIGINT,

  FOREIGN KEY (parent)
  REFERENCES Classification (id)
    ON DELETE RESTRICT
);

CREATE TABLE Publisher (
  id      BIGINT AUTO_INCREMENT PRIMARY KEY,
  text    VARCHAR(255) NOT NULL UNIQUE,
  website VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IssueDate (
  id    BIGINT AUTO_INCREMENT PRIMARY KEY,
  day   SMALLINT,
  month SMALLINT,
  year  INTEGER
);

CREATE TABLE LanguageTable (
  id   BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  code VARCHAR(255) NOT NULL
);

CREATE TABLE LicenseType (
  id   BIGINT AUTO_INCREMENT PRIMARY KEY,
  text VARCHAR(255) NOT NULL
);

CREATE TABLE Material (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	lang BIGINT,
	issueDate BIGINT,
  licenseType BIGINT,
  source TEXT NOT NULL,

  FOREIGN KEY (lang)
  REFERENCES LanguageTable (id)
        ON DELETE RESTRICT,

  FOREIGN KEY (issueDate)
  REFERENCES IssueDate (id)
        ON DELETE RESTRICT
);

CREATE TABLE LanguageKeyCodes (
  lang BIGINT       NOT NULL,
  code VARCHAR(255) NOT NULL,

  FOREIGN KEY (lang)
        REFERENCES LanguageTable(id)
        ON DELETE RESTRICT
);

CREATE TABLE LanguageString (
  id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  lang      BIGINT,
  textValue TEXT NOT NULL,

  FOREIGN KEY (lang)
  REFERENCES LanguageTable (id)
    ON DELETE RESTRICT
);

CREATE TABLE Material_Author (
  material BIGINT NOT NULL,
  author   BIGINT NOT NULL,

  PRIMARY KEY (material, author),

  FOREIGN KEY (material)
        REFERENCES Material(id)
        ON DELETE RESTRICT,

  FOREIGN KEY (author)
        REFERENCES Author(id)
        ON DELETE RESTRICT
);

CREATE TABLE Material_Classification (
  material       BIGINT NOT NULL,
  classification BIGINT NOT NULL,

  PRIMARY KEY (material, classification),

  FOREIGN KEY (classification)
  REFERENCES Classification (id)
    ON DELETE RESTRICT,

  FOREIGN KEY (material)
  REFERENCES Material (id)
    ON DELETE RESTRICT

);

CREATE TABLE Material_Publisher (
  material  BIGINT NOT NULL,
  publisher BIGINT NOT NULL,

  PRIMARY KEY (material, publisher),

  FOREIGN KEY (publisher)
  REFERENCES Publisher (id)
    ON DELETE RESTRICT,

  FOREIGN KEY (material)
  REFERENCES Material (id)
    ON DELETE RESTRICT
);

CREATE TABLE Material_Title (
	material BIGINT NOT NULL,
	title BIGINT NOT NULL,

	PRIMARY KEY (material, title),
	
	FOREIGN KEY (material) 
        REFERENCES Material(id)
        ON DELETE RESTRICT,
	
	FOREIGN KEY (title)
        REFERENCES LanguageString(id)
        ON DELETE RESTRICT
);

CREATE TABLE Material_Description (
	material BIGINT NOT NULL,
	description BIGINT NOT NULL,

	PRIMARY KEY (material, description),
	
	FOREIGN KEY (material) 
        REFERENCES Material(id)
        ON DELETE RESTRICT,
	
	FOREIGN KEY (description)
        REFERENCES LanguageString(id)
        ON DELETE RESTRICT
);

CREATE TABLE Material_ResourceType (
  material     BIGINT NOT NULL,
  resourceType BIGINT NOT NULL,

  PRIMARY KEY (material, resourceType),

  FOREIGN KEY (material)
  REFERENCES Material (id)
    ON DELETE RESTRICT,

  FOREIGN KEY (resourceType)
  REFERENCES ResourceType (id)
    ON DELETE RESTRICT
);

CREATE TABLE Material_EducationalContext (
  material           BIGINT NOT NULL,
  educationalContext BIGINT NOT NULL,

  PRIMARY KEY (material, educationalContext),

  FOREIGN KEY (material)
  REFERENCES Material (id)
    ON DELETE RESTRICT,

  FOREIGN KEY (educationalContext)
  REFERENCES EducationalContext (id)
    ON DELETE RESTRICT
);

CREATE TABLE TranslationGroup (
  id   BIGINT AUTO_INCREMENT PRIMARY KEY,
  lang BIGINT NOT NULL,

  FOREIGN KEY (lang)
        REFERENCES LanguageTable(id)
        ON DELETE RESTRICT
);

CREATE TABLE Translation (
  translationGroup BIGINT,
  translationKey   VARCHAR(255),
  translation      TEXT NOT NULL,

  PRIMARY KEY (translationGroup, translationKey),

  FOREIGN KEY (translationGroup)
        REFERENCES TranslationGroup(id)
        ON DELETE RESTRICT
);

