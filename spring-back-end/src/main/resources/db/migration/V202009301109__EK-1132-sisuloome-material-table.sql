# An intermediary table to hold materials sent from Sisuloome.
# Referenced by SisuloomeMaterialAuthor.
CREATE TABLE IF NOT EXISTS SisuloomeMaterial
(
    id                 BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    createdAt          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    personalCode       VARCHAR(255),
    externalMaterialId BIGINT,
    addedAt            VARCHAR(255),
    visibility         VARCHAR(255),
    title              VARCHAR(255),
    materialUrl        VARCHAR(255),
    embedCode          VARCHAR(1023)
);