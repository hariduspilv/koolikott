SET foreign_key_checks = 0;

CREATE TABLE TargetGroup (
  id    BIGINT AUTO_INCREMENT PRIMARY KEY,
  name  VARCHAR(255) NOT NULL,
  label VARCHAR(255) NOT NULL,
  UNIQUE (name)
);

insert into TargetGroup(name, label) values ('ZERO_FIVE', 'PRESCHOOL');
insert into TargetGroup(name, label) values ('SIX_SEVEN', 'PRESCHOOL');

insert into TargetGroup(name, label) values ('GRADE1', 'LEVEL1');
insert into TargetGroup(name, label) values ('GRADE2', 'LEVEL1');
insert into TargetGroup(name, label) values ('GRADE3', 'LEVEL1');

insert into TargetGroup(name, label) values ('GRADE4', 'LEVEL2');
insert into TargetGroup(name, label) values ('GRADE5', 'LEVEL2');
insert into TargetGroup(name, label) values ('GRADE6', 'LEVEL2');

insert into TargetGroup(name, label) values ('GRADE7', 'LEVEL3');
insert into TargetGroup(name, label) values ('GRADE8', 'LEVEL3');
insert into TargetGroup(name, label) values ('GRADE9', 'LEVEL3');

insert into TargetGroup(name, label) values ('GYMNASIUM', 'LEVEL_GYMNASIUM');

ALTER TABLE LearningObject_TargetGroup
  ADD targetGroup_id BIGINT;

UPDATE LearningObject_TargetGroup lt JOIN TargetGroup tg ON tg.name=lt.targetGroup SET lt.targetGroup_id=tg.id;

ALTER TABLE LearningObject_TargetGroup
  DROP PRIMARY KEY,
  ADD CONSTRAINT PRIMARY KEY (learningObject, targetGroup_id);

ALTER TABLE LearningObject_TargetGroup DROP COLUMN targetGroup;
ALTER TABLE LearningObject_TargetGroup CHANGE COLUMN `targetGroup_id` `targetGroup` BIGINT NOT NULL;
ALTER TABLE LearningObject_TargetGroup ADD CONSTRAINT FOREIGN KEY (targetGroup) REFERENCES TargetGroup(id);

SET foreign_key_checks = 1;
