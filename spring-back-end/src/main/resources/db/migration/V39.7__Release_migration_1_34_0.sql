SET foreign_key_checks = 0;

ALTER TABLE EmailToCreator ADD COLUMN learningObjectId BIGINT NULL;

ALTER TABLE EmailToCreator ADD CONSTRAINT FK_ETC_Learning_object_id FOREIGN KEY (learningObjectId) REFERENCES LearningObject(id);

CREATE INDEX learning_object_id on EmailToCreator(learningObjectId);

SET foreign_key_checks = 1;
