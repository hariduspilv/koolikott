SET foreign_key_checks = 0;

CREATE TABLE PortfolioMaterial
(
  id        BIGINT(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  portfolio BIGINT(20) NOT NULL,
  material  BIGINT(20) NOT NULL,

  CONSTRAINT material_fk FOREIGN KEY (material) REFERENCES Material (id),
  CONSTRAINT portfolio__fk FOREIGN KEY (portfolio) REFERENCES Portfolio (id)
);

SET foreign_key_checks = 1;
