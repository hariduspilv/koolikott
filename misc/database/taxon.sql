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