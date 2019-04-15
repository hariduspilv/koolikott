SET foreign_key_checks = 0;

ALTER TABLE EmailToCreator ADD COLUMN senderId BIGINT NULL;

ALTER TABLE EmailToCreator ADD CONSTRAINT FK_ETC_Sender_id FOREIGN KEY (senderId) REFERENCES User (id);

CREATE INDEX emailToCreator_sender_id on EmailToCreator(senderId);

SET foreign_key_checks = 1;
