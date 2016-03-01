SET foreign_key_checks = 0;

-- TagUpVote start

CREATE TABLE TagUpVoteAux (
  id                BIGINT  AUTO_INCREMENT PRIMARY KEY,
  user              BIGINT NOT NULL,
  learningObject    BIGINT NOT NULL,
  tag               BIGINT NOT NULL,
  deleted           BOOLEAN DEFAULT FALSE,

  FOREIGN KEY (user)
  REFERENCES User (id)
    ON DELETE RESTRICT,

  FOREIGN KEY (learningObject)
  REFERENCES LearningObject (id)
    ON DELETE RESTRICT,

  FOREIGN KEY (tag)
  REFERENCES Tag (id)
    ON DELETE RESTRICT
);

INSERT INTO TagUpVoteAux (id, user, tag, deleted, learningObject) SELECT id, user, tag, deleted, if (tuv.material is not null, material, portfolio) FROM TagUpVote tuv;
DROP TABLE TagUpVote;
RENAME TABLE TagUpVoteAux TO TagUpVote;

-- TagUpVote end


-- Remove Waramu taxon mappings, as they should be est-core now
DROP TABLE IF EXISTS WaramuTaxonMapping;

-- Add new translations
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3,'ADD_TO_NEW_PORTFOLIO','Add materials to new portfolio');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1,'ADD_TO_NEW_PORTFOLIO','Lisa materjalid uude kogumikku');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2,'ADD_TO_NEW_PORTFOLIO','Добавить материалы в новый портфель');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3,'ADD_MATERIAL_TO_EXISTING_PORTFOLIO','Add materials to new portfolio');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1,'ADD_MATERIAL_TO_EXISTING_PORTFOLIO','Lisa materjalid uude kogumikku');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2,'ADD_MATERIAL_TO_EXISTING_PORTFOLIO','Добавить материалы в новый портфель');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3,'CHOOSE_PORTFOLIO','Add materials to new portfolio');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1,'CHOOSE_PORTFOLIO','Lisa materjalid uude kogumikku');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2,'CHOOSE_PORTFOLIO','Добавить материалы в новый портфель');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3,'CHOOSE_PORTFOLIO_CHAPTER','Add materials to new portfolio');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1,'CHOOSE_PORTFOLIO_CHAPTER','Lisa materjalid uude kogumikku');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2,'CHOOSE_PORTFOLIO_CHAPTER','Добавить материалы в новый портфель');

SET foreign_key_checks = 1;
