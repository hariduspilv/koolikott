SET foreign_key_checks = 0;

CREATE TABLE RepositoryURL (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  baseURL    VARCHAR(255) NOT NULL,
  repository BIGINT,

  FOREIGN KEY (repository)
  REFERENCES Repository (id)
    ON DELETE RESTRICT
);

insert into RepositoryURL(id, baseURL, repository) values(1, 'koolielu.ee', 1);
insert into RepositoryURL(id, baseURL, repository) values(2, 'koolitaja.eenet.ee', 1);

SET foreign_key_checks = 1;