SET foreign_key_checks = 0;

UPDATE Translation SET translation = 'Õppevara salvestamine ebaõnnestus. Täiendavate küsimuste esitamiseks pöörduge kasutajatoe poole.' WHERE translationKey = 'PORTFOLIO_SAVE_FAILED' AND translationGroup = 1;
UPDATE Translation SET translation = 'Õppevara salvestamine ebaõnnestus. Täiendavate küsimuste esitamiseks pöörduge kasutajatoe poole.' WHERE translationKey = 'PORTFOLIO_SAVE_FAILED' AND translationGroup = 2;
UPDATE Translation SET translation = 'Learningobject save failed. For further assistance contact customer support.' WHERE translationKey = 'PORTFOLIO_SAVE_FAILED' AND translationGroup = 3;

SET foreign_key_checks = 1;