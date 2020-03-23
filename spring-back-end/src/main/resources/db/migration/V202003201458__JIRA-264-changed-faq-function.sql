SET foreign_key_checks = 0;

ALTER TABLE Faq CHANGE questionEst titleEst VARCHAR(255) NOT NULL;
ALTER TABLE Faq CHANGE questionRus titleRus VARCHAR(255) NOT NULL;
ALTER TABLE Faq CHANGE questionEng titleEng VARCHAR(255) NOT NULL;
ALTER TABLE Faq CHANGE answerEst contentEst TEXT NOT NULL;
ALTER TABLE Faq CHANGE answerRus contentRus TEXT NOT NULL;
ALTER TABLE Faq CHANGE answerEng contentEng TEXT NOT NULL;

SET foreign_key_checks = 1;