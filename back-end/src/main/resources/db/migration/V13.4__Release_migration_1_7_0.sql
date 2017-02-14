SET foreign_key_checks = 0;

CREATE TABLE LearningObject_Taxon (
  learningObject  BIGINT,
  taxon BIGINT,
  PRIMARY KEY (learningObject, taxon),
  FOREIGN KEY (learningObject) REFERENCES LearningObject(id),
  FOREIGN KEY (taxon) REFERENCES Taxon(id)
);

INSERT INTO LearningObject_Taxon(learningObject, taxon) SELECT material, taxon from Material_Taxon WHERE taxon IS NOT NULL;
INSERT INTO LearningObject_Taxon(learningObject, taxon) SELECT portfolio, taxon from Portfolio_Taxon WHERE taxon IS NOT NULL;

SET foreign_key_checks = 1;
