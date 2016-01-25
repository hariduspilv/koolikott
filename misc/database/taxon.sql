-- Educational Context
insert into Taxon(id, name, level) values (1, 'PRESCHOOLEDUCATION', 'EDUCATIONAL_CONTEXT');
insert into EducationalContext(id) values (1);
insert into Taxon(id, name, level) values (2, 'BASICEDUCATION', 'EDUCATIONAL_CONTEXT');
insert into EducationalContext(id) values (2);
insert into Taxon(id, name, level) values (3, 'SECONDARYEDUCATION', 'EDUCATIONAL_CONTEXT');
insert into EducationalContext(id) values (3);
insert into Taxon(id, name, level) values (4, 'VOCATIONALEDUCATION', 'EDUCATIONAL_CONTEXT');
insert into EducationalContext(id) values (4);


-- Domain

-- Preschool
insert into Taxon(id, name, level) values (100, 'Me_and_the_environment', 'DOMAIN');
insert into Domain(id, educationalContext) values (100, 1);
insert into Taxon(id, name, level) values (101, 'Language_and_speech', 'DOMAIN');
insert into Domain(id, educationalContext) values (101, 1);
insert into Taxon(id, name, level) values (102, 'Estonian', 'DOMAIN');
insert into Domain(id, educationalContext) values (102, 1);
insert into Taxon(id, name, level) values (103, 'Mathematics', 'DOMAIN');
insert into Domain(id, educationalContext) values (103, 1);
insert into Taxon(id, name, level) values (104, 'Arts', 'DOMAIN');
insert into Domain(id, educationalContext) values (104, 1);
insert into Taxon(id, name, level) values (105, 'Music', 'DOMAIN');
insert into Domain(id, educationalContext) values (105, 1);
insert into Taxon(id, name, level) values (106, 'Movement', 'DOMAIN');
insert into Domain(id, educationalContext) values (106, 1);

-- Basicschool
insert into Taxon(id, name, level) values (107, 'Language_and_literature', 'DOMAIN');
insert into Domain(id, educationalContext) values (107, 2);
insert into Taxon(id, name, level) values (108, 'Foreign_language', 'DOMAIN');
insert into Domain(id, educationalContext) values (108, 2);
insert into Taxon(id, name, level) values (109, 'Mathematics', 'DOMAIN');
insert into Domain(id, educationalContext) values (109, 2);
insert into Taxon(id, name, level) values (110, 'Natural_sciences', 'DOMAIN');
insert into Domain(id, educationalContext) values (110, 2);
insert into Taxon(id, name, level) values (111, 'Social_studies', 'DOMAIN');
insert into Domain(id, educationalContext) values (111, 2);
insert into Taxon(id, name, level) values (112, 'Arts', 'DOMAIN');
insert into Domain(id, educationalContext) values (112, 2);
insert into Taxon(id, name, level) values (113, 'Technology', 'DOMAIN');
insert into Domain(id, educationalContext) values (113, 2);
insert into Taxon(id, name, level) values (114, 'Physical_education', 'DOMAIN');
insert into Domain(id, educationalContext) values (114, 2);
insert into Taxon(id, name, level) values (115, 'Electives', 'DOMAIN');
insert into Domain(id, educationalContext) values (115, 2);

-- Secondary school
insert into Taxon(id, name, level) values (118, 'Language_and_literature', 'DOMAIN');
insert into Domain(id, educationalContext) values (118, 3);
insert into Taxon(id, name, level) values (119, 'Foreign_language', 'DOMAIN');
insert into Domain(id, educationalContext) values (119, 3);
insert into Taxon(id, name, level) values (120, 'Mathematics', 'DOMAIN');
insert into Domain(id, educationalContext) values (120, 3);
insert into Taxon(id, name, level) values (121, 'Natural_sciences', 'DOMAIN');
insert into Domain(id, educationalContext) values (121, 3);
insert into Taxon(id, name, level) values (122, 'Social_studies', 'DOMAIN');
insert into Domain(id, educationalContext) values (122, 3);
insert into Taxon(id, name, level) values (123, 'Arts', 'DOMAIN');
insert into Domain(id, educationalContext) values (123, 3);
insert into Taxon(id, name, level) values (124, 'Physical_education', 'DOMAIN');
insert into Domain(id, educationalContext) values (124, 3);
insert into Taxon(id, name, level) values (125, 'Electives', 'DOMAIN');
insert into Domain(id, educationalContext) values (125, 3);


-- Computer Science
insert into Taxon(id, name, level) values (129, 'Computer_science', 'DOMAIN');
insert into Domain(id, educationalContext) values (129, 4);


-- Subject


-- Basic Education

-- Language and literature
insert into Taxon(id, name, level) values (1000, 'Estonian', 'SUBJECT');
insert into Subject(id, domain) values (1000, 107);
insert into Taxon(id, name, level) values (1001, 'Russian', 'SUBJECT');
insert into Subject(id, domain) values (1001, 107);
insert into Taxon(id, name, level) values (1002, 'Literature', 'SUBJECT');
insert into Subject(id, domain) values (1002, 107);

-- Foreign language
insert into Taxon(id, name, level) values (1003, 'Estonian', 'SUBJECT');
insert into Subject(id, domain) values (1003, 108);
insert into Taxon(id, name, level) values (1004, 'English', 'SUBJECT');
insert into Subject(id, domain) values (1004, 108);
insert into Taxon(id, name, level) values (1005, 'French', 'SUBJECT');
insert into Subject(id, domain) values (1005, 108);
insert into Taxon(id, name, level) values (1006, 'German', 'SUBJECT');
insert into Subject(id, domain) values (1006, 108);
insert into Taxon(id, name, level) values (1007, 'Russian', 'SUBJECT');
insert into Subject(id, domain) values (1007, 108);
insert into Taxon(id, name, level) values (1008, 'Swedish', 'SUBJECT');
insert into Subject(id, domain) values (1008, 108);
insert into Taxon(id, name, level) values (1009, 'Finnish', 'SUBJECT');
insert into Subject(id, domain) values (1009, 108);
insert into Taxon(id, name, level) values (1010, 'Latin', 'SUBJECT');
insert into Subject(id, domain) values (1010, 108);
insert into Taxon(id, name, level) values (1011, 'Chinese', 'SUBJECT');
insert into Subject(id, domain) values (1011, 108);
insert into Taxon(id, name, level) values (1012, 'Japanese', 'SUBJECT');
insert into Subject(id, domain) values (1012, 108);
insert into Taxon(id, name, level) values (1013, 'Other', 'SUBJECT');
insert into Subject(id, domain) values (1013, 108);

-- Mathematics
insert into Taxon(id, name, level) values (1014, 'Mathematics', 'SUBJECT');
insert into Subject(id, domain) values (1014, 109);

-- Natural sciences
insert into Taxon(id, name, level) values (1015, 'Science', 'SUBJECT');
insert into Subject(id, domain) values (1015, 110);
insert into Taxon(id, name, level) values (1016, 'Biology', 'SUBJECT');
insert into Subject(id, domain) values (1016, 110);
insert into Taxon(id, name, level) values (1017, 'Geography', 'SUBJECT');
insert into Subject(id, domain) values (1017, 110);
insert into Taxon(id, name, level) values (1018, 'Physics', 'SUBJECT');
insert into Subject(id, domain) values (1018, 110);
insert into Taxon(id, name, level) values (1019, 'Chemistry', 'SUBJECT');
insert into Subject(id, domain) values (1019, 110);

-- Social studies
insert into Taxon(id, name, level) values (1020, 'Human_studies', 'SUBJECT');
insert into Subject(id, domain) values (1020, 111);
insert into Taxon(id, name, level) values (1021, 'History', 'SUBJECT');
insert into Subject(id, domain) values (1021, 111);
insert into Taxon(id, name, level) values (1022, 'Civics_and_citizenship', 'SUBJECT');
insert into Subject(id, domain) values (1022, 111);

-- Arts
insert into Taxon(id, name, level) values (1023, 'Art', 'SUBJECT');
insert into Subject(id, domain) values (1023, 112);
insert into Taxon(id, name, level) values (1024, 'Music', 'SUBJECT');
insert into Subject(id, domain) values (1024, 112);

-- Technology
insert into Taxon(id, name, level) values (1025, 'Technology', 'SUBJECT');
insert into Subject(id, domain) values (1025, 113);
insert into Taxon(id, name, level) values (1026, 'Handicraft', 'SUBJECT');
insert into Subject(id, domain) values (1026, 113);
insert into Taxon(id, name, level) values (1027, 'Home_economics', 'SUBJECT');
insert into Subject(id, domain) values (1027, 113);

-- Physical education
insert into Taxon(id, name, level) values (1028, 'Physical_education', 'SUBJECT');
insert into Subject(id, domain) values (1028, 114);

-- Electives
insert into Taxon(id, name, level) values (1029, 'Religious_studies', 'SUBJECT');
insert into Subject(id, domain) values (1029, 115);
insert into Taxon(id, name, level) values (1030, 'Informatics', 'SUBJECT');
insert into Subject(id, domain) values (1030, 115);
insert into Taxon(id, name, level) values (1031, 'Career_studies', 'SUBJECT');
insert into Subject(id, domain) values (1031, 115);
insert into Taxon(id, name, level) values (1032, 'Economics_and_business_studies', 'SUBJECT');
insert into Subject(id, domain) values (1032, 115);
insert into Taxon(id, name, level) values (1033, 'Other', 'SUBJECT');
insert into Subject(id, domain) values (1033, 115);

-- End of Basic Education Subjects

-- Secondary Education

-- Language and literature
insert into Taxon(id, name, level) values (1050, 'Estonian', 'SUBJECT');
insert into Subject(id, domain) values (1050, 118);
insert into Taxon(id, name, level) values (1051, 'Russian', 'SUBJECT');
insert into Subject(id, domain) values (1051, 118);
insert into Taxon(id, name, level) values (1052, 'Literature', 'SUBJECT');
insert into Subject(id, domain) values (1052, 118);

-- Foreign language
insert into Taxon(id, name, level) values (1053, 'Estonian', 'SUBJECT');
insert into Subject(id, domain) values (1053, 119);
insert into Taxon(id, name, level) values (1054, 'English', 'SUBJECT');
insert into Subject(id, domain) values (1054, 119);
insert into Taxon(id, name, level) values (1055, 'French', 'SUBJECT');
insert into Subject(id, domain) values (1055, 119);
insert into Taxon(id, name, level) values (1056, 'German', 'SUBJECT');
insert into Subject(id, domain) values (1056, 119);
insert into Taxon(id, name, level) values (1057, 'Russian', 'SUBJECT');
insert into Subject(id, domain) values (1057, 119);
insert into Taxon(id, name, level) values (1058, 'Swedish', 'SUBJECT');
insert into Subject(id, domain) values (1058, 119);
insert into Taxon(id, name, level) values (1059, 'Finnish', 'SUBJECT');
insert into Subject(id, domain) values (1059, 119);
insert into Taxon(id, name, level) values (1060, 'Latin', 'SUBJECT');
insert into Subject(id, domain) values (1060, 119);
insert into Taxon(id, name, level) values (1061, 'Chinese', 'SUBJECT');
insert into Subject(id, domain) values (1061, 119);
insert into Taxon(id, name, level) values (1062, 'Japanese', 'SUBJECT');
insert into Subject(id, domain) values (1062, 119);
insert into Taxon(id, name, level) values (1063, 'Other', 'SUBJECT');
insert into Subject(id, domain) values (1063, 119);

-- Mathematics
insert into Taxon(id, name, level) values (1064, 'Mathematics', 'SUBJECT');
insert into Subject(id, domain) values (1064, 120);

-- Natural sciences
insert into Taxon(id, name, level) values (1065, 'Science', 'SUBJECT');
insert into Subject(id, domain) values (1065, 121);
insert into Taxon(id, name, level) values (1066, 'Biology', 'SUBJECT');
insert into Subject(id, domain) values (1066, 121);
insert into Taxon(id, name, level) values (1067, 'Geography', 'SUBJECT');
insert into Subject(id, domain) values (1067, 121);
insert into Taxon(id, name, level) values (1068, 'Physics', 'SUBJECT');
insert into Subject(id, domain) values (1068, 121);
insert into Taxon(id, name, level) values (1069, 'Chemistry', 'SUBJECT');
insert into Subject(id, domain) values (1069, 121);

-- Social studies
insert into Taxon(id, name, level) values (1070, 'Human_studies', 'SUBJECT');
insert into Subject(id, domain) values (1070, 122);
insert into Taxon(id, name, level) values (1071, 'History', 'SUBJECT');
insert into Subject(id, domain) values (1071, 122);
insert into Taxon(id, name, level) values (1072, 'Civics_and_citizenship', 'SUBJECT');
insert into Subject(id, domain) values (1072, 122);

-- Arts
insert into Taxon(id, name, level) values (1073, 'Art', 'SUBJECT');
insert into Subject(id, domain) values (1073, 123);
insert into Taxon(id, name, level) values (1074, 'Music', 'SUBJECT');
insert into Subject(id, domain) values (1074, 123);

-- Physical education
insert into Taxon(id, name, level) values (1078, 'Physical_education', 'SUBJECT');
insert into Subject(id, domain) values (1078, 124);

-- Electives
insert into Taxon(id, name, level) values (1079, 'Religious_studies', 'SUBJECT');
insert into Subject(id, domain) values (1079, 125);
insert into Taxon(id, name, level) values (1080, 'Informatics', 'SUBJECT');
insert into Subject(id, domain) values (1080, 125);
insert into Taxon(id, name, level) values (1081, 'Career_studies', 'SUBJECT');
insert into Subject(id, domain) values (1081, 125);
insert into Taxon(id, name, level) values (1082, 'Economics_and_business_studies', 'SUBJECT');
insert into Subject(id, domain) values (1082, 125);
insert into Taxon(id, name, level) values (1083, 'Media_studies', 'SUBJECT');
insert into Subject(id, domain) values (1083, 125);
insert into Taxon(id, name, level) values (1084, 'National_defence', 'SUBJECT');
insert into Subject(id, domain) values (1084, 125);
insert into Taxon(id, name, level) values (1085, 'Basics_of_inquiry', 'SUBJECT');
insert into Subject(id, domain) values (1085, 125);
insert into Taxon(id, name, level) values (1086, 'Philosophy', 'SUBJECT');
insert into Subject(id, domain) values (1086, 125);
insert into Taxon(id, name, level) values (1087, 'Other', 'SUBJECT');
insert into Subject(id, domain) values (1087, 125);

-- End of Secondary Education Subjects

-- Specialization

-- Start of Specialization for Computer Science

insert into Taxon(id, name, level) values (20000, 'Computers_and_Networks', 'SPECIALIZATION');
insert into Specialization(id, domain) values (20000, 129);

-- End of Specialization for Computer Science
-- End of Secondary Education Subjects



-- Module

-- Start of Module for Computer And Networks

insert into Taxon(id, name, level) values (20100, 'Majanduse_alused', 'MODULE');
insert into Module(id, specialization) values (20100, 20000);
insert into Taxon(id, name, level) values (20101, 'Ettevõtluse_alused', 'MODULE');
insert into Module(id, specialization) values (20101, 20000);
insert into Taxon(id, name, level) values (20102, 'Õiguse_alused', 'MODULE');
insert into Module(id, specialization) values (20102, 20000);
insert into Taxon(id, name, level) values (20103, 'IT_õigus', 'MODULE');
insert into Module(id, specialization) values (20103, 20000);
insert into Taxon(id, name, level) values (20104, 'Kommunikatsioon', 'MODULE');
insert into Module(id, specialization) values (20104, 20000);

-- End of Module for Computer And Networks



-- Topic

-- Start of topics for Basic Education

insert into Taxon(id, name, level) values (10000, 'Basic_history', 'TOPIC');
insert into Topic(id, subject) values (10000, 1021);
insert into Taxon(id, name, level) values (10001, 'Estonian_history', 'TOPIC');
insert into Topic(id, subject) values (10001, 1021);

-- End of topics for Basic Education

-- Start of topics for Preschool

insert into Taxon(id, name, level) values (10100, 'Preschool_Topic1', 'TOPIC');
insert into Topic(id, domain) values (10100, 100);
insert into Taxon(id, name, level) values (10101, 'Preschool_Topic2', 'TOPIC');
insert into Topic(id, domain) values (10101, 100);

-- End of topics for Preschool

-- Start of topics for Secondary Education

insert into Taxon(id, name, level) values (10200, 'Secondary_Education_Topic1', 'TOPIC');
insert into Topic(id, subject) values (10200, 1079);
insert into Taxon(id, name, level) values (10201, 'Secondary_Education_Topic2', 'TOPIC');
insert into Topic(id, subject) values (10201, 1079);

-- End of topics for Secondary Education

-- Start of topics for Vocational Education

insert into Taxon(id, name, level) values (10300, 'Vocational_Education_Topic1', 'TOPIC');
insert into Topic(id, module) values (10300, 20100);

-- End of topics for Vocational Education



-- Subtopic

-- Start of subtopics for Basic History

insert into Taxon(id, name, level) values (15000, 'Ajaarvamine', 'SUBTOPIC');
insert into Subtopic(id, topic) values (15000, 10000);
insert into Taxon(id, name, level) values (15001, 'Ajalooallikad', 'SUBTOPIC');
insert into Subtopic(id, topic) values (15001, 10000);
insert into Taxon(id, name, level) values (15002, 'Eluolu', 'SUBTOPIC');
insert into Subtopic(id, topic) values (15002, 10000);
insert into Taxon(id, name, level) values (15003, 'Ajaloosündmused_ja_ajaloolised_isikud', 'SUBTOPIC');
insert into Subtopic(id, topic) values (15003, 10000);

-- End of subtopics for Basic History

-- Start of subtopics for Estonian History

insert into Taxon(id, name, level) values (15010, 'Muinasaeg', 'SUBTOPIC');
insert into Subtopic(id, topic) values (15010, 10001);
insert into Taxon(id, name, level) values (15011, 'Tähtsamad_arheloogilised_kultuurid', 'SUBTOPIC');
insert into Subtopic(id, topic) values (15011, 10001);
insert into Taxon(id, name, level) values (15012, 'Eesti_ühiskonf_muinasaja_lõpul', 'SUBTOPIC');
insert into Subtopic(id, topic) values (15012, 10001);
insert into Taxon(id, name, level) values (15013, 'Keskaeg', 'SUBTOPIC');
insert into Subtopic(id, topic) values (15013, 10001);

-- End of subtopics for Estonian History

-- Start of subtopics for Preschool_Topic1

insert into Taxon(id, name, level) values (15020, 'Subtopic_for_Preschool_Topic1', 'SUBTOPIC');
insert into Subtopic(id, topic) values (15020, 10100);

-- End of subtopics for Preschool_Topic1

-- Start of subtopics for Secondary_Education_Topic1

insert into Taxon(id, name, level) values (15030, 'Subtopic_for_Secondary_Education_Topic1', 'SUBTOPIC');
insert into Subtopic(id, topic) values (15030, 10200);

-- End of subtopics for Secondary_Education_Topic1

-- Start of subtopics for Secondary_Education_Topic1

insert into Taxon(id, name, level) values (15040, 'Subtopic_for_Vocational_Education', 'SUBTOPIC');
insert into Subtopic(id, topic) values (15040, 10300);

-- End of subtopics for Secondary_Education_Topic1




