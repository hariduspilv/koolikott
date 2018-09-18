SET foreign_key_checks = 0;

ALTER TABLE EducationalContext ADD used boolean not null default true;
ALTER TABLE Domain ADD used boolean not null default true;
ALTER TABLE Subject ADD used boolean not null default true;
ALTER TABLE Topic ADD used boolean not null default true;
ALTER TABLE Subtopic ADD used boolean not null default true;
ALTER TABLE Specialization ADD used boolean not null default true;
ALTER TABLE Module ADD used boolean not null default true;
ALTER TABLE Taxon ADD used boolean not null default true;

SET foreign_key_checks = 1;
