SET foreign_key_checks = 0;

CREATE TABLE PeerReview
(
  id BIGINT(20) PRIMARY KEY,
  url VARCHAR(500) NOT NULL
);

CREATE TABLE Material_PeerReview
(
  id BIGINT(20) PRIMARY KEY,
  material BIGINT(20) NOT NULL,
  peerReview BIGINT(20) NOT NULL,
  CONSTRAINT material_fk FOREIGN KEY (material) REFERENCES Material (id),
  CONSTRAINT peerReview__fk FOREIGN KEY (peerReview) REFERENCES PeerReview (id)
);

ALTER TABLE Material_PeerReview MODIFY id BIGINT(20) NOT NULL AUTO_INCREMENT;

ALTER TABLE PeerReview MODIFY id BIGINT(20) NOT NULL AUTO_INCREMENT;

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1,'MATERIAL_PEER_REVIEW','Retsensioon');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3,'MATERIAL_PEER_REVIEW','Review');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2,'MATERIAL_PEER_REVIEW','Oбзор');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1,'MATERIAL_TAB_NEW_PEER_REVIEW','Lisa retsensioon');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3,'MATERIAL_TAB_NEW_PEER_REVIEW','Add review');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2,'MATERIAL_TAB_NEW_PEER_REVIEW','Добавить oбзор');

SET foreign_key_checks = 1;