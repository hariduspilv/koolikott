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
insert into Taxon(id, name, level) values (116, 'Cross-curricular_themes', 'DOMAIN');
insert into Domain(id, educationalContext) values (116, 2);
insert into Taxon(id, name, level) values (117, 'Key_competences', 'DOMAIN');
insert into Domain(id, educationalContext) values (117, 2);

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
insert into Taxon(id, name, level) values (126, 'Cross-curricular_themes', 'DOMAIN');
insert into Domain(id, educationalContext) values (126, 3);
insert into Taxon(id, name, level) values (127, 'Key_competences', 'DOMAIN');
insert into Domain(id, educationalContext) values (127, 3);

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

-- Cross-curricular themes
insert into Taxon(id, name, level) values (1034, 'Lifelong_learning_and_career_planning', 'SUBJECT');
insert into Subject(id, domain) values (1034, 116);
insert into Taxon(id, name, level) values (1035, 'Environment_and_sustainable_development', 'SUBJECT');
insert into Subject(id, domain) values (1035, 116);
insert into Taxon(id, name, level) values (1036, 'Civic_initiative_and_entrepreneurship', 'SUBJECT');
insert into Subject(id, domain) values (1036, 116);
insert into Taxon(id, name, level) values (1037, 'Cultural_identity', 'SUBJECT');
insert into Subject(id, domain) values (1037, 116);
insert into Taxon(id, name, level) values (1038, 'Information_environment', 'SUBJECT');
insert into Subject(id, domain) values (1038, 116);
insert into Taxon(id, name, level) values (1039, 'Technology_and_innovation', 'SUBJECT');
insert into Subject(id, domain) values (1039, 116);
insert into Taxon(id, name, level) values (1040, 'Health_and_safety', 'SUBJECT');
insert into Subject(id, domain) values (1040, 116);
insert into Taxon(id, name, level) values (1041, 'Values_and_moral', 'SUBJECT');
insert into Subject(id, domain) values (1041, 116);

-- Key competences
insert into Taxon(id, name, level) values (1042, 'Cultural_and_value_competence', 'SUBJECT');
insert into Subject(id, domain) values (1042, 117);
insert into Taxon(id, name, level) values (1043, 'Social_and_citizenship_competence', 'SUBJECT');
insert into Subject(id, domain) values (1043, 117);
insert into Taxon(id, name, level) values (1044, 'Self_management_competence', 'SUBJECT');
insert into Subject(id, domain) values (1044, 117);
insert into Taxon(id, name, level) values (1045, 'Learning_to_learn_competence', 'SUBJECT');
insert into Subject(id, domain) values (1045, 117);
insert into Taxon(id, name, level) values (1046, 'Communication_competence', 'SUBJECT');
insert into Subject(id, domain) values (1046, 117);
insert into Taxon(id, name, level) values (1047, 'Mathematics_natural_sciences_and_technology_competence', 'SUBJECT');
insert into Subject(id, domain) values (1047, 117);
insert into Taxon(id, name, level) values (1048, 'Entrepreneurship_competence', 'SUBJECT');
insert into Subject(id, domain) values (1048, 117);
insert into Taxon(id, name, level) values (1049, 'Digital_competence', 'SUBJECT');
insert into Subject(id, domain) values (1049, 117);

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

-- Cross-curricular themes
insert into Taxon(id, name, level) values (1088, 'Lifelong_learning_and_career_planning', 'SUBJECT');
insert into Subject(id, domain) values (1088, 126);
insert into Taxon(id, name, level) values (1089, 'Environment_and_sustainable_development', 'SUBJECT');
insert into Subject(id, domain) values (1089, 126);
insert into Taxon(id, name, level) values (1090, 'Civic_initiative_and_entrepreneurship', 'SUBJECT');
insert into Subject(id, domain) values (1090, 126);
insert into Taxon(id, name, level) values (1091, 'Cultural_identity', 'SUBJECT');
insert into Subject(id, domain) values (1091, 126);
insert into Taxon(id, name, level) values (1092, 'Information_environment', 'SUBJECT');
insert into Subject(id, domain) values (1092, 126);
insert into Taxon(id, name, level) values (1093, 'Technology_and_innovation', 'SUBJECT');
insert into Subject(id, domain) values (1093, 126);
insert into Taxon(id, name, level) values (1094, 'Health_and_safety', 'SUBJECT');
insert into Subject(id, domain) values (1094, 126);
insert into Taxon(id, name, level) values (1095, 'Values_and_moral', 'SUBJECT');
insert into Subject(id, domain) values (1095, 126);

-- Key competences
insert into Taxon(id, name, level) values (1096, 'Cultural_and_value_competence', 'SUBJECT');
insert into Subject(id, domain) values (1096, 127);
insert into Taxon(id, name, level) values (1097, 'Social_and_citizenship_competence', 'SUBJECT');
insert into Subject(id, domain) values (1097, 127);
insert into Taxon(id, name, level) values (1098, 'Self_management_competence', 'SUBJECT');
insert into Subject(id, domain) values (1098, 127);
insert into Taxon(id, name, level) values (1099, 'Learning_to_learn_competence', 'SUBJECT');
insert into Subject(id, domain) values (1099, 127);
insert into Taxon(id, name, level) values (1100, 'Communication_competence', 'SUBJECT');
insert into Subject(id, domain) values (1100, 127);
insert into Taxon(id, name, level) values (1101, 'Mathematics_natural_sciences_and_technology_competence', 'SUBJECT');
insert into Subject(id, domain) values (1101, 127);
insert into Taxon(id, name, level) values (1102, 'Entrepreneurship_competence', 'SUBJECT');
insert into Subject(id, domain) values (1102, 127);
insert into Taxon(id, name, level) values (1103, 'Digital_competence', 'SUBJECT');
insert into Subject(id, domain) values (1103, 127);

-- End of Secondary Education Subjects

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
-- End of Secondary Education Subjects

-- EstCore Taxon mapping

insert into EstCoreTaxonMapping(id, taxon, name) values (1, 1, 'preschoolEducation');
insert into EstCoreTaxonMapping(id, taxon, name) values (2, 2, 'basicEducation');
insert into EstCoreTaxonMapping(id, taxon, name) values (3, 3, 'secondaryEducation');
insert into EstCoreTaxonMapping(id, taxon, name) values (4, 4, 'vocationalEducation');
insert into EstCoreTaxonMapping(id, taxon, name) values (100, 100, 'Mina ja keskkond');
insert into EstCoreTaxonMapping(id, taxon, name) values (101, 101, 'Keel ja kõne');
insert into EstCoreTaxonMapping(id, taxon, name) values (102, 102, 'Eesti keel kui teine keel');
insert into EstCoreTaxonMapping(id, taxon, name) values (103, 103, 'Matemaatika');
insert into EstCoreTaxonMapping(id, taxon, name) values (104, 104, 'Kunst');
insert into EstCoreTaxonMapping(id, taxon, name) values (105, 105, 'Muusika');
insert into EstCoreTaxonMapping(id, taxon, name) values (106, 106, 'Liikumine');
insert into EstCoreTaxonMapping(id, taxon, name) values (107, 107, 'Language and literature');
insert into EstCoreTaxonMapping(id, taxon, name) values (108, 108, 'Foreign language');
insert into EstCoreTaxonMapping(id, taxon, name) values (109, 109, 'Mathematics');
insert into EstCoreTaxonMapping(id, taxon, name) values (110, 110, 'Natural sciences');
insert into EstCoreTaxonMapping(id, taxon, name) values (111, 111, 'Social studies');
insert into EstCoreTaxonMapping(id, taxon, name) values (112, 112, 'Arts');
insert into EstCoreTaxonMapping(id, taxon, name) values (113, 113, 'Technology');
insert into EstCoreTaxonMapping(id, taxon, name) values (114, 114, 'Physical education');
insert into EstCoreTaxonMapping(id, taxon, name) values (115, 115, 'Electives');
insert into EstCoreTaxonMapping(id, taxon, name) values (116, 116, 'Cross-curricular themes');
insert into EstCoreTaxonMapping(id, taxon, name) values (117, 117, 'Key competences');
insert into EstCoreTaxonMapping(id, taxon, name) values (118, 118, 'Language and literature');
insert into EstCoreTaxonMapping(id, taxon, name) values (119, 119, 'Foreign language');
insert into EstCoreTaxonMapping(id, taxon, name) values (120, 120, 'Mathematics');
insert into EstCoreTaxonMapping(id, taxon, name) values (121, 121, 'Natural sciences');
insert into EstCoreTaxonMapping(id, taxon, name) values (122, 122, 'Social studies');
insert into EstCoreTaxonMapping(id, taxon, name) values (123, 123, 'Arts');
insert into EstCoreTaxonMapping(id, taxon, name) values (124, 124, 'Physical education');
insert into EstCoreTaxonMapping(id, taxon, name) values (125, 125, 'Electives');
insert into EstCoreTaxonMapping(id, taxon, name) values (126, 126, 'Cross-curricular themes');
insert into EstCoreTaxonMapping(id, taxon, name) values (127, 127, 'Key competences');
insert into EstCoreTaxonMapping(id, taxon, name) values (129, 129, 'Computer science');
insert into EstCoreTaxonMapping(id, taxon, name) values (1000, 1000, 'Estonian');
insert into EstCoreTaxonMapping(id, taxon, name) values (1001, 1001, 'Russian');
insert into EstCoreTaxonMapping(id, taxon, name) values (1002, 1002, 'Literature');
insert into EstCoreTaxonMapping(id, taxon, name) values (1003, 1003, 'Estonian');
insert into EstCoreTaxonMapping(id, taxon, name) values (1004, 1004, 'English');
insert into EstCoreTaxonMapping(id, taxon, name) values (1005, 1005, 'French');
insert into EstCoreTaxonMapping(id, taxon, name) values (1006, 1006, 'German');
insert into EstCoreTaxonMapping(id, taxon, name) values (1007, 1007, 'Russian');
insert into EstCoreTaxonMapping(id, taxon, name) values (1008, 1008, 'Swedish');
insert into EstCoreTaxonMapping(id, taxon, name) values (1009, 1009, 'Finnish');
insert into EstCoreTaxonMapping(id, taxon, name) values (1010, 1010, 'Latin');
insert into EstCoreTaxonMapping(id, taxon, name) values (1011, 1011, 'Chinese');
insert into EstCoreTaxonMapping(id, taxon, name) values (1012, 1012, 'Japanese');
insert into EstCoreTaxonMapping(id, taxon, name) values (1013, 1013, 'Other');
insert into EstCoreTaxonMapping(id, taxon, name) values (1014, 1014, 'Mathematics');
insert into EstCoreTaxonMapping(id, taxon, name) values (1015, 1015, 'Science');
insert into EstCoreTaxonMapping(id, taxon, name) values (1016, 1016, 'Biology');
insert into EstCoreTaxonMapping(id, taxon, name) values (1017, 1017, 'Geography');
insert into EstCoreTaxonMapping(id, taxon, name) values (1018, 1018, 'Physics');
insert into EstCoreTaxonMapping(id, taxon, name) values (1019, 1019, 'Chemistry');
insert into EstCoreTaxonMapping(id, taxon, name) values (1020, 1020, 'Human studies');
insert into EstCoreTaxonMapping(id, taxon, name) values (1021, 1021, 'History');
insert into EstCoreTaxonMapping(id, taxon, name) values (1022, 1022, 'Civics and citizenship');
insert into EstCoreTaxonMapping(id, taxon, name) values (1023, 1023, 'Art');
insert into EstCoreTaxonMapping(id, taxon, name) values (1024, 1024, 'Music');
insert into EstCoreTaxonMapping(id, taxon, name) values (1025, 1025, 'Technology');
insert into EstCoreTaxonMapping(id, taxon, name) values (1026, 1026, 'Handicraft');
insert into EstCoreTaxonMapping(id, taxon, name) values (1027, 1027, 'Home economics');
insert into EstCoreTaxonMapping(id, taxon, name) values (1028, 1028, 'Physical education');
insert into EstCoreTaxonMapping(id, taxon, name) values (1029, 1029, 'Religious studies');
insert into EstCoreTaxonMapping(id, taxon, name) values (1030, 1030, 'Informatics');
insert into EstCoreTaxonMapping(id, taxon, name) values (1031, 1031, 'Career studies');
insert into EstCoreTaxonMapping(id, taxon, name) values (1032, 1032, 'Economics and business studies');
insert into EstCoreTaxonMapping(id, taxon, name) values (1033, 1033, 'Other');
insert into EstCoreTaxonMapping(id, taxon, name) values (1034, 1034, 'Lifelong learning and career planning');
insert into EstCoreTaxonMapping(id, taxon, name) values (1035, 1035, 'Environment and sustainable development');
insert into EstCoreTaxonMapping(id, taxon, name) values (1036, 1036, 'Civic initiative and entrepreneurship');
insert into EstCoreTaxonMapping(id, taxon, name) values (1037, 1037, 'Cultural identity');
insert into EstCoreTaxonMapping(id, taxon, name) values (1038, 1038, 'Information environment');
insert into EstCoreTaxonMapping(id, taxon, name) values (1039, 1039, 'Technology and innovation');
insert into EstCoreTaxonMapping(id, taxon, name) values (1040, 1040, 'Health and safety');
insert into EstCoreTaxonMapping(id, taxon, name) values (1041, 1041, 'Values and moral');
insert into EstCoreTaxonMapping(id, taxon, name) values (1042, 1042, 'Cultural and value competence');
insert into EstCoreTaxonMapping(id, taxon, name) values (1043, 1043, 'Social and citizenship competence');
insert into EstCoreTaxonMapping(id, taxon, name) values (1044, 1044, 'Self management competence');
insert into EstCoreTaxonMapping(id, taxon, name) values (1045, 1045, 'Learning to learn competence');
insert into EstCoreTaxonMapping(id, taxon, name) values (1046, 1046, 'Communication competence');
insert into EstCoreTaxonMapping(id, taxon, name) values (1047, 1047, 'Mathematics natural sciences and technology competence');
insert into EstCoreTaxonMapping(id, taxon, name) values (1048, 1048, 'Entrepreneurship competence');
insert into EstCoreTaxonMapping(id, taxon, name) values (1049, 1049, 'Digital competence');
insert into EstCoreTaxonMapping(id, taxon, name) values (1050, 1050, 'Estonian');
insert into EstCoreTaxonMapping(id, taxon, name) values (1051, 1051, 'Russian');
insert into EstCoreTaxonMapping(id, taxon, name) values (1052, 1052, 'Literature');
insert into EstCoreTaxonMapping(id, taxon, name) values (1053, 1053, 'Estonian');
insert into EstCoreTaxonMapping(id, taxon, name) values (1054, 1054, 'English');
insert into EstCoreTaxonMapping(id, taxon, name) values (1055, 1055, 'French');
insert into EstCoreTaxonMapping(id, taxon, name) values (1056, 1056, 'German');
insert into EstCoreTaxonMapping(id, taxon, name) values (1057, 1057, 'Russian');
insert into EstCoreTaxonMapping(id, taxon, name) values (1058, 1058, 'Swedish');
insert into EstCoreTaxonMapping(id, taxon, name) values (1059, 1059, 'Finnish');
insert into EstCoreTaxonMapping(id, taxon, name) values (1060, 1060, 'Latin');
insert into EstCoreTaxonMapping(id, taxon, name) values (1061, 1061, 'Chinese');
insert into EstCoreTaxonMapping(id, taxon, name) values (1062, 1062, 'Japanese');
insert into EstCoreTaxonMapping(id, taxon, name) values (1063, 1063, 'Other');
insert into EstCoreTaxonMapping(id, taxon, name) values (1064, 1064, 'Mathematics');
insert into EstCoreTaxonMapping(id, taxon, name) values (1065, 1065, 'Science');
insert into EstCoreTaxonMapping(id, taxon, name) values (1066, 1066, 'Biology');
insert into EstCoreTaxonMapping(id, taxon, name) values (1067, 1067, 'Geography');
insert into EstCoreTaxonMapping(id, taxon, name) values (1068, 1068, 'Physics');
insert into EstCoreTaxonMapping(id, taxon, name) values (1069, 1069, 'Chemistry');
insert into EstCoreTaxonMapping(id, taxon, name) values (1070, 1070, 'Human studies');
insert into EstCoreTaxonMapping(id, taxon, name) values (1071, 1071, 'History');
insert into EstCoreTaxonMapping(id, taxon, name) values (1072, 1072, 'Civics and citizenship');
insert into EstCoreTaxonMapping(id, taxon, name) values (1073, 1073, 'Art');
insert into EstCoreTaxonMapping(id, taxon, name) values (1074, 1074, 'Music');
insert into EstCoreTaxonMapping(id, taxon, name) values (1078, 1078, 'Physical education');
insert into EstCoreTaxonMapping(id, taxon, name) values (1079, 1079, 'Religious studies');
insert into EstCoreTaxonMapping(id, taxon, name) values (1080, 1080, 'Informatics');
insert into EstCoreTaxonMapping(id, taxon, name) values (1081, 1081, 'Career studies');
insert into EstCoreTaxonMapping(id, taxon, name) values (1082, 1082, 'Economics and business studies');
insert into EstCoreTaxonMapping(id, taxon, name) values (1083, 1083, 'Media studies');
insert into EstCoreTaxonMapping(id, taxon, name) values (1084, 1084, 'National defence');
insert into EstCoreTaxonMapping(id, taxon, name) values (1085, 1085, 'Basics of inquiry');
insert into EstCoreTaxonMapping(id, taxon, name) values (1086, 1086, 'Philosophy');
insert into EstCoreTaxonMapping(id, taxon, name) values (1087, 1087, 'Other');
insert into EstCoreTaxonMapping(id, taxon, name) values (1088, 1088, 'Lifelong learning and career planning');
insert into EstCoreTaxonMapping(id, taxon, name) values (1089, 1089, 'Environment and sustainable development');
insert into EstCoreTaxonMapping(id, taxon, name) values (1090, 1090, 'Civic initiative and entrepreneurship');
insert into EstCoreTaxonMapping(id, taxon, name) values (1091, 1091, 'Cultural identity');
insert into EstCoreTaxonMapping(id, taxon, name) values (1092, 1092, 'Information environment');
insert into EstCoreTaxonMapping(id, taxon, name) values (1093, 1093, 'Technology and innovation');
insert into EstCoreTaxonMapping(id, taxon, name) values (1094, 1094, 'Health and safety');
insert into EstCoreTaxonMapping(id, taxon, name) values (1095, 1095, 'Values and moral');
insert into EstCoreTaxonMapping(id, taxon, name) values (1096, 1096, 'Cultural and value competence');
insert into EstCoreTaxonMapping(id, taxon, name) values (1097, 1097, 'Social and citizenship competence');
insert into EstCoreTaxonMapping(id, taxon, name) values (1098, 1098, 'Self management competence');
insert into EstCoreTaxonMapping(id, taxon, name) values (1099, 1099, 'Learning to learn competence');
insert into EstCoreTaxonMapping(id, taxon, name) values (1100, 1100, 'Communication competence');
insert into EstCoreTaxonMapping(id, taxon, name) values (1101, 1101, 'Mathematics natural sciences and technology competence');
insert into EstCoreTaxonMapping(id, taxon, name) values (1102, 1102, 'Entrepreneurship competence');
insert into EstCoreTaxonMapping(id, taxon, name) values (1103, 1103, 'Digital competence');
insert into EstCoreTaxonMapping(id, taxon, name) values (10000, 10000, 'Basic history');
insert into EstCoreTaxonMapping(id, taxon, name) values (10001, 10001, 'Estonian history');
insert into EstCoreTaxonMapping(id, taxon, name) values (10100, 10100, 'Preschool Topic1');
insert into EstCoreTaxonMapping(id, taxon, name) values (10101, 10101, 'Preschool Topic2');
insert into EstCoreTaxonMapping(id, taxon, name) values (10200, 10200, 'Secondary Education Topic1');
insert into EstCoreTaxonMapping(id, taxon, name) values (10201, 10201, 'Secondary Education Topic2');
insert into EstCoreTaxonMapping(id, taxon, name) values (15000, 15000, 'Ajaarvamine');
insert into EstCoreTaxonMapping(id, taxon, name) values (15001, 15001, 'Ajalooallikad');
insert into EstCoreTaxonMapping(id, taxon, name) values (15002, 15002, 'Eluolu');
insert into EstCoreTaxonMapping(id, taxon, name) values (15003, 15003, 'Ajaloosündmused ja ajaloolised isikud');
insert into EstCoreTaxonMapping(id, taxon, name) values (15010, 15010, 'Muinasaeg');
insert into EstCoreTaxonMapping(id, taxon, name) values (15011, 15011, 'Tähtsamad arheloogilised kultuurid');
insert into EstCoreTaxonMapping(id, taxon, name) values (15012, 15012, 'Eesti ühiskonf muinasaja lõpul');
insert into EstCoreTaxonMapping(id, taxon, name) values (15013, 15013, 'Keskaeg');


-- Waramu Taxon mapping - for testing purposes

insert into WaramuTaxonMapping(id, taxon, name) values (1, 1, 'PRESCHOOLEDUCATION');
insert into WaramuTaxonMapping(id, taxon, name) values (2, 2, 'COMPULSORYEDUCATION');
insert into WaramuTaxonMapping(id, taxon, name) values (3, 3, 'SECONDARYEDUCATION');
insert into WaramuTaxonMapping(id, taxon, name) values (4, 4, 'VOCATIONALEDUCATION');
insert into WaramuTaxonMapping(id, taxon, name) values (100, 100, 'Me_and_the_environment');
insert into WaramuTaxonMapping(id, taxon, name) values (101, 101, 'Language_and_speech');
insert into WaramuTaxonMapping(id, taxon, name) values (102, 102, 'Estonian');
insert into WaramuTaxonMapping(id, taxon, name) values (103, 103, 'Mathematics');
insert into WaramuTaxonMapping(id, taxon, name) values (104, 104, 'Arts');
insert into WaramuTaxonMapping(id, taxon, name) values (105, 105, 'Music');
insert into WaramuTaxonMapping(id, taxon, name) values (106, 106, 'Movement');
insert into WaramuTaxonMapping(id, taxon, name) values (107, 107, 'Language_and_literature');
insert into WaramuTaxonMapping(id, taxon, name) values (108, 108, 'Foreign_language');
insert into WaramuTaxonMapping(id, taxon, name) values (109, 109, 'Mathematics');
insert into WaramuTaxonMapping(id, taxon, name) values (110, 110, 'Natural_sciences');
insert into WaramuTaxonMapping(id, taxon, name) values (111, 111, 'Social_studies');
insert into WaramuTaxonMapping(id, taxon, name) values (112, 112, 'Arts');
insert into WaramuTaxonMapping(id, taxon, name) values (113, 113, 'Technology');
insert into WaramuTaxonMapping(id, taxon, name) values (114, 114, 'Physical_education');
insert into WaramuTaxonMapping(id, taxon, name) values (115, 115, 'Electives');
insert into WaramuTaxonMapping(id, taxon, name) values (116, 116, 'Cross-curricular_themes');
insert into WaramuTaxonMapping(id, taxon, name) values (117, 117, 'Key_competences');
insert into WaramuTaxonMapping(id, taxon, name) values (118, 118, 'Language_and_literature');
insert into WaramuTaxonMapping(id, taxon, name) values (119, 119, 'Foreign_language');
insert into WaramuTaxonMapping(id, taxon, name) values (120, 120, 'Mathematics');
insert into WaramuTaxonMapping(id, taxon, name) values (121, 121, 'Natural_sciences');
insert into WaramuTaxonMapping(id, taxon, name) values (122, 122, 'Social_studies');
insert into WaramuTaxonMapping(id, taxon, name) values (123, 123, 'Arts');
insert into WaramuTaxonMapping(id, taxon, name) values (124, 124, 'Physical_education');
insert into WaramuTaxonMapping(id, taxon, name) values (125, 125, 'Electives');
insert into WaramuTaxonMapping(id, taxon, name) values (126, 126, 'Cross-curricular_themes');
insert into WaramuTaxonMapping(id, taxon, name) values (127, 127, 'Key_competences');
insert into WaramuTaxonMapping(id, taxon, name) values (129, 129, 'Computer_science');
insert into WaramuTaxonMapping(id, taxon, name) values (1000, 1000, 'Estonian');
insert into WaramuTaxonMapping(id, taxon, name) values (1001, 1001, 'Russian');
insert into WaramuTaxonMapping(id, taxon, name) values (1002, 1002, 'Literature');
insert into WaramuTaxonMapping(id, taxon, name) values (1003, 1003, 'Estonian');
insert into WaramuTaxonMapping(id, taxon, name) values (1004, 1004, 'English');
insert into WaramuTaxonMapping(id, taxon, name) values (1005, 1005, 'French');
insert into WaramuTaxonMapping(id, taxon, name) values (1006, 1006, 'German');
insert into WaramuTaxonMapping(id, taxon, name) values (1007, 1007, 'Russian');
insert into WaramuTaxonMapping(id, taxon, name) values (1008, 1008, 'Swedish');
insert into WaramuTaxonMapping(id, taxon, name) values (1009, 1009, 'Finnish');
insert into WaramuTaxonMapping(id, taxon, name) values (1010, 1010, 'Latin');
insert into WaramuTaxonMapping(id, taxon, name) values (1011, 1011, 'Chinese');
insert into WaramuTaxonMapping(id, taxon, name) values (1012, 1012, 'Japanese');
insert into WaramuTaxonMapping(id, taxon, name) values (1013, 1013, 'Other');
insert into WaramuTaxonMapping(id, taxon, name) values (1014, 1014, 'Mathematics');
insert into WaramuTaxonMapping(id, taxon, name) values (1015, 1015, 'Science');
insert into WaramuTaxonMapping(id, taxon, name) values (1016, 1016, 'Biology');
insert into WaramuTaxonMapping(id, taxon, name) values (1017, 1017, 'Geography');
insert into WaramuTaxonMapping(id, taxon, name) values (1018, 1018, 'Physics');
insert into WaramuTaxonMapping(id, taxon, name) values (1019, 1019, 'Chemistry');
insert into WaramuTaxonMapping(id, taxon, name) values (1020, 1020, 'Human_studies');
insert into WaramuTaxonMapping(id, taxon, name) values (1021, 1021, 'History');
insert into WaramuTaxonMapping(id, taxon, name) values (1022, 1022, 'Civics_and_citizenship');
insert into WaramuTaxonMapping(id, taxon, name) values (1023, 1023, 'Art');
insert into WaramuTaxonMapping(id, taxon, name) values (1024, 1024, 'Music');
insert into WaramuTaxonMapping(id, taxon, name) values (1025, 1025, 'Technology');
insert into WaramuTaxonMapping(id, taxon, name) values (1026, 1026, 'Handicraft');
insert into WaramuTaxonMapping(id, taxon, name) values (1027, 1027, 'Home_economics');
insert into WaramuTaxonMapping(id, taxon, name) values (1028, 1028, 'Physical_education');
insert into WaramuTaxonMapping(id, taxon, name) values (1029, 1029, 'Religious_studies');
insert into WaramuTaxonMapping(id, taxon, name) values (1030, 1030, 'Informatics');
insert into WaramuTaxonMapping(id, taxon, name) values (1031, 1031, 'Career_studies');
insert into WaramuTaxonMapping(id, taxon, name) values (1032, 1032, 'Economics_and_business_studies');
insert into WaramuTaxonMapping(id, taxon, name) values (1033, 1033, 'Other');
insert into WaramuTaxonMapping(id, taxon, name) values (1034, 1034, 'Lifelong_learning_and_career_planning');
insert into WaramuTaxonMapping(id, taxon, name) values (1035, 1035, 'Environment_and_sustainable_development');
insert into WaramuTaxonMapping(id, taxon, name) values (1036, 1036, 'Civic_initiative_and_entrepreneurship');
insert into WaramuTaxonMapping(id, taxon, name) values (1037, 1037, 'Cultural_identity');
insert into WaramuTaxonMapping(id, taxon, name) values (1038, 1038, 'Information_environment');
insert into WaramuTaxonMapping(id, taxon, name) values (1039, 1039, 'Technology_and_innovation');
insert into WaramuTaxonMapping(id, taxon, name) values (1040, 1040, 'Health_and_safety');
insert into WaramuTaxonMapping(id, taxon, name) values (1041, 1041, 'Values_and_moral');
insert into WaramuTaxonMapping(id, taxon, name) values (1042, 1042, 'Cultural_and_value_competence');
insert into WaramuTaxonMapping(id, taxon, name) values (1043, 1043, 'Social_and_citizenship_competence');
insert into WaramuTaxonMapping(id, taxon, name) values (1044, 1044, 'Self_management_competence');
insert into WaramuTaxonMapping(id, taxon, name) values (1045, 1045, 'Learning_to_learn_competence');
insert into WaramuTaxonMapping(id, taxon, name) values (1046, 1046, 'Communication_competence');
insert into WaramuTaxonMapping(id, taxon, name) values (1047, 1047, 'Mathematics_natural_sciences_and_technology_competence');
insert into WaramuTaxonMapping(id, taxon, name) values (1048, 1048, 'Entrepreneurship_competence');
insert into WaramuTaxonMapping(id, taxon, name) values (1049, 1049, 'Digital_competence');
insert into WaramuTaxonMapping(id, taxon, name) values (1050, 1050, 'Estonian');
insert into WaramuTaxonMapping(id, taxon, name) values (1051, 1051, 'Russian');
insert into WaramuTaxonMapping(id, taxon, name) values (1052, 1052, 'Literature');
insert into WaramuTaxonMapping(id, taxon, name) values (1053, 1053, 'Estonian');
insert into WaramuTaxonMapping(id, taxon, name) values (1054, 1054, 'English');
insert into WaramuTaxonMapping(id, taxon, name) values (1055, 1055, 'French');
insert into WaramuTaxonMapping(id, taxon, name) values (1056, 1056, 'German');
insert into WaramuTaxonMapping(id, taxon, name) values (1057, 1057, 'Russian');
insert into WaramuTaxonMapping(id, taxon, name) values (1058, 1058, 'Swedish');
insert into WaramuTaxonMapping(id, taxon, name) values (1059, 1059, 'Finnish');
insert into WaramuTaxonMapping(id, taxon, name) values (1060, 1060, 'Latin');
insert into WaramuTaxonMapping(id, taxon, name) values (1061, 1061, 'Chinese');
insert into WaramuTaxonMapping(id, taxon, name) values (1062, 1062, 'Japanese');
insert into WaramuTaxonMapping(id, taxon, name) values (1063, 1063, 'Other');
insert into WaramuTaxonMapping(id, taxon, name) values (1064, 1064, 'Mathematics');
insert into WaramuTaxonMapping(id, taxon, name) values (1065, 1065, 'Science');
insert into WaramuTaxonMapping(id, taxon, name) values (1066, 1066, 'Biology');
insert into WaramuTaxonMapping(id, taxon, name) values (1067, 1067, 'Geography');
insert into WaramuTaxonMapping(id, taxon, name) values (1068, 1068, 'Physics');
insert into WaramuTaxonMapping(id, taxon, name) values (1069, 1069, 'Chemistry');
insert into WaramuTaxonMapping(id, taxon, name) values (1070, 1070, 'Human_studies');
insert into WaramuTaxonMapping(id, taxon, name) values (1071, 1071, 'History');
insert into WaramuTaxonMapping(id, taxon, name) values (1072, 1072, 'Civics_and_citizenship');
insert into WaramuTaxonMapping(id, taxon, name) values (1073, 1073, 'Art');
insert into WaramuTaxonMapping(id, taxon, name) values (1074, 1074, 'Music');
insert into WaramuTaxonMapping(id, taxon, name) values (1078, 1078, 'Physical_education');
insert into WaramuTaxonMapping(id, taxon, name) values (1079, 1079, 'Religious_studies');
insert into WaramuTaxonMapping(id, taxon, name) values (1080, 1080, 'Informatics');
insert into WaramuTaxonMapping(id, taxon, name) values (1081, 1081, 'Career_studies');
insert into WaramuTaxonMapping(id, taxon, name) values (1082, 1082, 'Economics_and_business_studies');
insert into WaramuTaxonMapping(id, taxon, name) values (1083, 1083, 'Media_studies');
insert into WaramuTaxonMapping(id, taxon, name) values (1084, 1084, 'National_defence');
insert into WaramuTaxonMapping(id, taxon, name) values (1085, 1085, 'Basics_of_inquiry');
insert into WaramuTaxonMapping(id, taxon, name) values (1086, 1086, 'Philosophy');
insert into WaramuTaxonMapping(id, taxon, name) values (1087, 1087, 'Other');
insert into WaramuTaxonMapping(id, taxon, name) values (1088, 1088, 'Lifelong_learning_and_career_planning');
insert into WaramuTaxonMapping(id, taxon, name) values (1089, 1089, 'Environment_and_sustainable_development');
insert into WaramuTaxonMapping(id, taxon, name) values (1090, 1090, 'Civic_initiative_and_entrepreneurship');
insert into WaramuTaxonMapping(id, taxon, name) values (1091, 1091, 'Cultural_identity');
insert into WaramuTaxonMapping(id, taxon, name) values (1092, 1092, 'Information_environment');
insert into WaramuTaxonMapping(id, taxon, name) values (1093, 1093, 'Technology_and_innovation');
insert into WaramuTaxonMapping(id, taxon, name) values (1094, 1094, 'Health_and_safety');
insert into WaramuTaxonMapping(id, taxon, name) values (1095, 1095, 'Values_and_moral');
insert into WaramuTaxonMapping(id, taxon, name) values (1096, 1096, 'Cultural_and_value_competence');
insert into WaramuTaxonMapping(id, taxon, name) values (1097, 1097, 'Social_and_citizenship_competence');
insert into WaramuTaxonMapping(id, taxon, name) values (1098, 1098, 'Self_management_competence');
insert into WaramuTaxonMapping(id, taxon, name) values (1099, 1099, 'Learning_to_learn_competence');
insert into WaramuTaxonMapping(id, taxon, name) values (1100, 1100, 'Communication_competence');
insert into WaramuTaxonMapping(id, taxon, name) values (1101, 1101, 'Mathematics_natural_sciences_and_technology_competence');
insert into WaramuTaxonMapping(id, taxon, name) values (1102, 1102, 'Entrepreneurship_competence');
insert into WaramuTaxonMapping(id, taxon, name) values (1103, 1103, 'Digital_competence');
insert into WaramuTaxonMapping(id, taxon, name) values (10000, 10000, 'Basic_history');
insert into WaramuTaxonMapping(id, taxon, name) values (10001, 10001, 'Estonian_history');
insert into WaramuTaxonMapping(id, taxon, name) values (10100, 10100, 'Preschool_Topic1');
insert into WaramuTaxonMapping(id, taxon, name) values (10101, 10101, 'Preschool_Topic2');
insert into WaramuTaxonMapping(id, taxon, name) values (10200, 10200, 'Secondary_Education_Topic1');
insert into WaramuTaxonMapping(id, taxon, name) values (10201, 10201, 'Secondary_Education_Topic2');
insert into WaramuTaxonMapping(id, taxon, name) values (15000, 15000, 'Ajaarvamine');
insert into WaramuTaxonMapping(id, taxon, name) values (15001, 15001, 'Ajalooallikad');
insert into WaramuTaxonMapping(id, taxon, name) values (15002, 15002, 'Eluolu');
insert into WaramuTaxonMapping(id, taxon, name) values (15003, 15003, 'Ajaloosündmused_ja_ajaloolised_isikud');
insert into WaramuTaxonMapping(id, taxon, name) values (15010, 15010, 'Muinasaeg');
insert into WaramuTaxonMapping(id, taxon, name) values (15011, 15011, 'Tähtsamad_arheloogilised_kultuurid');
insert into WaramuTaxonMapping(id, taxon, name) values (15012, 15012, 'Eesti_ühiskonf_muinasaja_lõpul');
insert into WaramuTaxonMapping(id, taxon, name) values (15013, 15013, 'Keskaeg');

