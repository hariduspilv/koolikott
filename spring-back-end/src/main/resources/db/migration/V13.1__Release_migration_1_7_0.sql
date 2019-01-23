SET foreign_key_checks = 0;

CREATE TABLE ChangedLearningObject (
  id BIGINT AUTO_INCREMENT,
  learningObject  BIGINT,
  changer BIGINT,
  taxon BIGINT,
  resourceType BIGINT,
  targetGroup BIGINT,
  PRIMARY KEY (id),
  FOREIGN KEY (learningObject) REFERENCES LearningObject(id),
  FOREIGN KEY (changer) REFERENCES User(id),
  FOREIGN KEY (taxon) REFERENCES Taxon(id),
  FOREIGN KEY (resourceType) REFERENCES ResourceType(id),
  FOREIGN KEY (targetGroup) REFERENCES ResourceType(id)
);

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'DASHBOARD_CHANGED_LEARNING_OBJECTS', 'Muutunud õppematerjalid');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'DASHBOARD_CHANGED_LEARNING_OBJECTS', 'Changed learning objects');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'DASHBOARD_CHANGED_LEARNING_OBJECTS', 'измененный объект обучения');

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'MESSAGE_CHANGED_LEARNING_OBJECT', 'Muutunud andmed:');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'MESSAGE_CHANGED_LEARNING_OBJECT', 'Changed data:');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'MESSAGE_CHANGED_LEARNING_OBJECT', 'Измененное данные');

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'UNDO_CHANGES', 'Eemalda kõik muudatused');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'UNDO_CHANGES', 'Remove all changes');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'UNDO_CHANGES', 'Удалить все изменения');

INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (1, 'ACCEPT_CHANGES', 'Nõustu muudatustega');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (3, 'ACCEPT_CHANGES', 'Accept changes');
INSERT INTO Translation (translationGroup, translationKey, translation) VALUES (2, 'ACCEPT_CHANGES', 'Принять изменения');

SET foreign_key_checks = 1;
