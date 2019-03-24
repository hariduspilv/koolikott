SET foreign_key_checks = 0;

CREATE TABLE PortfolioMaterial
(
  id        BIGINT(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  portfolio BIGINT(20) NOT NULL,
  material  BIGINT(20) NOT NULL,

  FOREIGN KEY (material) REFERENCES Material (id) ON DELETE RESTRICT ,
  FOREIGN KEY (portfolio) REFERENCES Portfolio (id) ON DELETE RESTRICT
);

SET foreign_key_checks = 1;
