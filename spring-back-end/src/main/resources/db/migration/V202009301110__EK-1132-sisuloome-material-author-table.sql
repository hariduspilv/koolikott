# Belongs to SisuloomeMaterial
CREATE TABLE IF NOT EXISTS SisuloomeMaterialAuthor
(
    id                BIGINT UNSIGNED UNIQUE NOT NULL AUTO_INCREMENT PRIMARY KEY,
    sisuloomeMaterial BIGINT UNSIGNED,
    createdAt         TIMESTAMP              NOT NULL DEFAULT CURRENT_TIMESTAMP,
    name              VARCHAR(255),
    surname           VARCHAR(255),
    CONSTRAINT FOREIGN KEY (sisuloomeMaterial) REFERENCES SisuloomeMaterial (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);