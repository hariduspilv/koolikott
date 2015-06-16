-- Drop tables

DROP TABLE IF EXISTS Translation;
DROP TABLE IF EXISTS TranslationGroup;
DROP TABLE IF EXISTS Material_Author;
DROP TABLE IF EXISTS LanguageString;
DROP TABLE IF EXISTS LanguageKeyCodes;
DROP TABLE IF EXISTS Material;
DROP TABLE IF EXISTS LanguageTable;
DROP TABLE IF EXISTS IssueDate;
DROP TABLE IF EXISTS Author;

-- Create tables

CREATE TABLE Author(
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(255) NOT NULL,
	surname varchar(255) NOT NULL
);

CREATE TABLE IssueDate (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	day SMALLINT,
	month SMALLINT,
	year INTEGER
);

CREATE TABLE LanguageTable (
	id   BIGINT AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(255) NOT NULL,
	code VARCHAR(255) NOT NULL
);

CREATE TABLE Material (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	lang BIGINT,
	issueDate BIGINT,
    source TEXT NOT NULL,

	FOREIGN KEY (lang)
	REFERENCES LanguageTable(id)
        ON DELETE RESTRICT,

	FOREIGN KEY (issueDate)
	REFERENCES IssueDate(id)
        ON DELETE RESTRICT
);

CREATE TABLE LanguageKeyCodes (
	lang BIGINT NOT NULL,
	code VARCHAR(255) NOT NULL,

	FOREIGN KEY (lang)
        REFERENCES LanguageTable(id)
        ON DELETE RESTRICT
);

CREATE TABLE LanguageString (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	lang BIGINT,
	textValue TEXT NOT NULL,
	material BIGINT,
	
	FOREIGN KEY (lang)
        REFERENCES LanguageTable(id)
        ON DELETE RESTRICT,

	FOREIGN KEY (material)
        REFERENCES Material(id)
        ON DELETE RESTRICT
);

CREATE TABLE Material_Author (
	material BIGINT NOT NULL,
	author BIGINT NOT NULL,

	PRIMARY KEY (material, author),
	
	FOREIGN KEY (material) 
        REFERENCES Material(id)
        ON DELETE RESTRICT,
	
	FOREIGN KEY (author)
        REFERENCES Author(id)
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

CREATE TABLE TranslationGroup (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	lang BIGINT NOT NULL,
	
	FOREIGN KEY (lang)
        REFERENCES LanguageTable(id)
        ON DELETE RESTRICT
);

CREATE TABLE Translation (
	translationGroup BIGINT,
	translationKey VARCHAR(255),
	translation TEXT not null,
	
	primary key (translationGroup, translationKey),
	
	FOREIGN KEY (translationGroup)
        REFERENCES TranslationGroup(id)
        ON DELETE RESTRICT
);

