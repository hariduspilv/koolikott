SET foreign_key_checks = 0;

-- Moving LearningObject picture to a separate table start

CREATE TABLE Picture (
  id    BIGINT  AUTO_INCREMENT PRIMARY KEY,
  name  VARCHAR(250) NOT NULL UNIQUE,
  data  LONGBLOB NOT NULL
);

INSERT INTO Picture (name, data) SELECT sha1(lo.picture), picture FROM LearningObject lo WHERE lo.picture IS NOT NULL GROUP BY lo.picture;

ALTER TABLE LearningObject ADD pic BIGINT;

UPDATE LearningObject lo 
    SET lo.pic = (SELECT p.id FROM Picture p WHERE p.data = lo.picture)
    WHERE lo.picture IS NOT NULL;

ALTER TABLE LearningObject DROP picture;

ALTER TABLE LearningObject ADD picture BIGINT;
ALTER TABLE LearningObject ADD CONSTRAINT FOREIGN KEY (picture) REFERENCES Picture(id);

UPDATE LearningObject SET picture = pic WHERE pic is not null;

ALTER TABLE LearningObject DROP pic;

-- Moving LearningObject picture to a separate table end

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3,'TOAST_NOTIFICATION_SENT_TO_ADMIN','Notification sent to administrator');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1,'TOAST_NOTIFICATION_SENT_TO_ADMIN','Teade saadetud administraatorile');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2,'TOAST_NOTIFICATION_SENT_TO_ADMIN','Администратор уведомляется');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3,'REPORT_COUNT','Report count');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1,'REPORT_COUNT','Raporteerimiste arv');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2,'REPORT_COUNT','Количество докладов');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3,'REPORTED_BY_MULTIPLE_USERS','Multiple users');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1,'REPORTED_BY_MULTIPLE_USERS','Mitmed kasutajad');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2,'REPORTED_BY_MULTIPLE_USERS','Несколько пользователей');

SET foreign_key_checks = 1;
