use dop;

UPDATE LearningObject_Taxon SET taxon = 4 where taxon = 162;
UPDATE User_Taxon SET taxon = 4 where taxon = 162;
UPDATE LearningObject_Taxon SET taxon = 4 where taxon = 163;
UPDATE User_Taxon SET taxon = 4 where taxon = 163;
UPDATE LearningObject_Taxon SET taxon = 4 where taxon = 164;
UPDATE User_Taxon SET taxon = 4 where taxon = 164;
UPDATE LearningObject_Taxon SET taxon = 4 where taxon = 165;
UPDATE User_Taxon SET taxon = 4 where taxon = 165;
UPDATE LearningObject_Taxon SET taxon = 4 where taxon = 166;
UPDATE User_Taxon SET taxon = 4 where taxon = 166;
UPDATE LearningObject_Taxon SET taxon = 4 where taxon = 167;
UPDATE User_Taxon SET taxon = 4 where taxon = 167;

UPDATE Domain set used = 0 where id in (162, 163, 164, 165, 166, 167);
UPDATE Taxon set used = 0 where id in (162, 163, 164, 165, 166, 167);