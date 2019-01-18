SET foreign_key_checks = 0;

CREATE TABLE UserEmail(
                        id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
                        user BIGINT(20) NOT NULL,
                        email VARCHAR(250),
                        activated BOOLEAN NOT NULL,
                        createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        activatedAt TIMESTAMP NULL,
                        pin VARCHAR(4),
                        CONSTRAINT FOREIGN KEY (user) REFERENCES User (id)
);

SET foreign_key_checks = 1;
