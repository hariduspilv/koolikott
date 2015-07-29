-- Those should be in separate files with complete data

-- EducationalContext

insert into EducationalContext(id, educationalContext) values (1001, 'PRESCHOOL');
insert into EducationalContext(id, educationalContext) values (1002, 'COMPULSORYEDUCATION');
insert into EducationalContext(id, educationalContext) values (1003, 'VOCATIONALEDUCATION');
insert into EducationalContext(id, educationalContext) values (1004, 'HIGHEREDUCATION');
insert into EducationalContext(id, educationalContext) values (1005, 'CONTINUINGEDUCATION');
insert into EducationalContext(id, educationalContext) values (1006, 'PROFESSIONALDEVELOPMENT');
insert into EducationalContext(id, educationalContext) values (1007, 'SPECIALEDUCATION');
insert into EducationalContext(id, educationalContext) values (1008, 'OTHER');

-- Classifications

insert into Classification(id, classificationName, parent) values (1, 'Biology', null);
insert into Classification(id, classificationName, parent) values(2, 'Plants', 1);
insert into Classification(id, classificationName, parent) values(3, 'Trees', 2);
insert into Classification(id, classificationName, parent) values (4, 'Mathematics', null);
insert into Classification(id, classificationName, parent) values(5, 'Algebra', 4);
insert into Classification(id, classificationName, parent) values(6, 'Linear', 5);
insert into Classification(id, classificationName, parent) values(7, 'Quadratic', 5);

-- Translation for Classifications. It must be in the translation files when we have the final Classification tree

insert into Translation(translationGroup, translationKey, translation) values (1, 'CLASSIFICATION_BIOLOGY', 'Bioloogia');
insert into Translation(translationGroup, translationKey, translation) values (1, 'CLASSIFICATION_PLANTS', 'Taimed');
insert into Translation(translationGroup, translationKey, translation) values (1, 'CLASSIFICATION_TREES', 'Puud');
insert into Translation(translationGroup, translationKey, translation) values (1, 'CLASSIFICATION_MATHEMATICS', 'Matemaatika');
insert into Translation(translationGroup, translationKey, translation) values (1, 'CLASSIFICATION_ALGEBRA', 'Algebra');
insert into Translation(translationGroup, translationKey, translation) values (1, 'CLASSIFICATION_LINEAR', 'Sirgjooneline');
insert into Translation(translationGroup, translationKey, translation) values (1, 'CLASSIFICATION_QUADRATIC', 'Quadratic');

insert into Translation(translationGroup, translationKey, translation) values (2, 'CLASSIFICATION_BIOLOGY', 'биология');
insert into Translation(translationGroup, translationKey, translation) values (2, 'CLASSIFICATION_PLANTS', 'растения');
insert into Translation(translationGroup, translationKey, translation) values (2, 'CLASSIFICATION_TREES', 'деревья');
insert into Translation(translationGroup, translationKey, translation) values (2, 'CLASSIFICATION_MATHEMATICS', 'математический');
insert into Translation(translationGroup, translationKey, translation) values (2, 'CLASSIFICATION_ALGEBRA', 'алгебра');
insert into Translation(translationGroup, translationKey, translation) values (2, 'CLASSIFICATION_LINEAR', 'линейный');
insert into Translation(translationGroup, translationKey, translation) values (2, 'CLASSIFICATION_QUADRATIC', 'квадратный');

insert into Translation(translationGroup, translationKey, translation) values (3, 'CLASSIFICATION_BIOLOGY', 'Biology');
insert into Translation(translationGroup, translationKey, translation) values (3, 'CLASSIFICATION_PLANTS', 'Plants');
insert into Translation(translationGroup, translationKey, translation) values (3, 'CLASSIFICATION_TREES', 'Trees');
insert into Translation(translationGroup, translationKey, translation) values (3, 'CLASSIFICATION_MATHEMATICS', 'Mathematics');
insert into Translation(translationGroup, translationKey, translation) values (3, 'CLASSIFICATION_ALGEBRA', 'Algebra');
insert into Translation(translationGroup, translationKey, translation) values (3, 'CLASSIFICATION_LINEAR', 'Linear');
insert into Translation(translationGroup, translationKey, translation) values (3, 'CLASSIFICATION_QUADRATIC', 'Quadratic');



-- Start of test data

-- IssueDate

insert into IssueDate(id, day, month, year) values(1, 2, 2, 1983);
insert into IssueDate(id, day, month, year) values(2, 27, 1, -983);
insert into IssueDate(id, year) values(3, -1500);
insert into IssueDate(id, day, month, year) values(4, 31, 3, 1923);
insert into IssueDate(id, day, month, year) values(5, 9, 12, 1978);
insert into IssueDate(id, day, month, year) values(6, 27, 1, 1986);
insert into IssueDate(id, month, year) values(7, 3, 1991);

-- Materials

insert into Material(id, lang, issueDate, licenseType, source, added, updated) values(1, 1, 1, 1, 'https://www.youtube.com/watch?v=gSWbx3CvVUk', '1999-02-02 06:00:01', '2000-03-01 07:00:01');
insert into Material(id, lang, issueDate, licenseType, source, added, updated) values(2, 2, 2, 2, 'https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes', '1992-02-03 06:00:01', '1995-07-12 09:00:01');
insert into Material(id, lang, issueDate, licenseType, source, added, updated) values(3, 4, 3, 3,  'http://eloquentjavascript.net/Eloquent_JavaScript.pdf', '2009-02-17 08:00:01', '2011-01-10 19:00:01');
insert into Material(id, lang, issueDate, licenseType, source, added, updated) values(4, 3, 4, 1,  'https://en.wikipedia.org/wiki/Power_Architecture', '2012-02-02 09:00:01', '2012-08-28 22:40:01');
insert into Material(id, lang, issueDate, licenseType, source, added, updated) values(5, 3, 5, 2,  'https://en.wikipedia.org/wiki/Power_Architecture', '2011-09-15 08:00:01', '2012-11-04 09:30:01');
insert into Material(id, lang, issueDate, licenseType, source, added, updated) values(6, null, null, null, 'http://www.planalto.gov.br/ccivil_03/Constituicao/Constituicao.htm', '1971-09-22 08:00:01', null);
insert into Material(id, lang, issueDate, licenseType, source, added, updated) values(7, 4, 6, 3, 'https://president.ee/en/republic-of-estonia/the-constitution/index.html', '2001-07-16 06:00:01', null);
insert into Material(id, lang, issueDate, licenseType, source, added, updated) values(8, 5, 7, 1, 'http://www.palmeiras.com.br/historia/titulos', '2014-06-01 09:00:01', null);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, views) values(9, null, null, null, 'http://EmptyFileds.test.ee', '2015-06-08 08:00:01', null, '98765432');
insert into Material(id, lang, issueDate, licenseType, source, added, updated) values(10, 1, 2, 3, 'http://automated.test.ee', '2015-06-09 08:00:01', null);
insert into Material(id, lang, issueDate, licenseType, source, added, updated) values(11, 1, 2, 3, 'http://performance.test.ee', '2015-06-09 08:00:01', null);

insert into Material(id, lang, issueDate, licenseType, source, added, updated) values(12, 1, 1, 1, 'https://en.wikipedia.org/wiki/Main_Page', '1999-02-02 06:00:01', '2000-03-01 07:00:01');
insert into Material(id, lang, issueDate, licenseType, source, added, updated) values(13, 2, 2, 2, 'https://en.wikipedia.org/wiki/New_Zealand_flag_debate', '1992-02-03 06:00:01', '1995-07-12 09:00:01');
insert into Material(id, lang, issueDate, licenseType, source, added, updated) values(14, 4, 3, 3,  'https://en.wikipedia.org/wiki/2015_Indian_heat_wave', '2009-02-17 08:00:01', '2011-01-10 19:00:01');
insert into Material(id, lang, issueDate, licenseType, source, added, updated) values(15, 3, 4, 1,  'https://en.wikipedia.org/wiki/Second_Libyan_Civil_War', '2002-02-02 09:00:01', '2012-08-28 22:40:01');
insert into Material(id, lang, issueDate, licenseType, source, added, updated) values(16, 3, 5, 2,  'https://en.wikipedia.org/wiki/United_States', '2001-09-15 08:00:01', '2012-11-04 09:30:01');
insert into Material(id, lang, issueDate, licenseType, source, added, updated) values(17, null, null, null, 'https://en.wikipedia.org/wiki/2015_Cajon_Pass_wildfire', '1971-09-22 08:00:01', null);
insert into Material(id, lang, issueDate, licenseType, source, added, updated) values(18, 4, 6, 3, 'https://en.wikipedia.org/wiki/India', '2001-07-16 06:00:01', null);
insert into Material(id, lang, issueDate, licenseType, source, added, updated) values(19, 5, 7, 1, 'https://en.wikipedia.org/wiki/Japan', '2002-06-01 09:00:01', null);

insert into Material(id, lang, issueDate, licenseType, source, added, updated) values(20, 1, 1, 1, 'https://en.wikipedia.org/wiki/Mexican_Drug_War', '1999-02-02 06:00:01', '2000-03-01 07:00:01');
insert into Material(id, lang, issueDate, licenseType, source, added, updated) values(21, 2, 2, 2, 'https://en.wikipedia.org/wiki/War_in_Darfur', '1992-02-03 06:00:01', '1995-07-12 09:00:01');
insert into Material(id, lang, issueDate, licenseType, source, added, updated) values(22, 4, 3, 3,  'https://en.wikipedia.org/wiki/Somalia', '2009-02-17 08:00:01', '2011-01-10 19:00:01');
insert into Material(id, lang, issueDate, licenseType, source, added, updated) values(23, 3, 4, 1,  'https://en.wikipedia.org/wiki/Libya', '2002-02-02 09:00:01', '2012-08-28 22:40:01');
insert into Material(id, lang, issueDate, licenseType, source, added, updated) values(24, 3, 5, 2,  'https://en.wikipedia.org/wiki/Democratic_Republic_of_Congo', '2001-09-15 08:00:01', '2012-11-04 09:30:01');
insert into Material(id, lang, issueDate, licenseType, source, added, updated) values(25, null, null, null, 'https://en.wikipedia.org/wiki/Turkey-PKK_conflict', '1971-09-22 08:00:01', null);
insert into Material(id, lang, issueDate, licenseType, source, added, updated) values(26, 4, 6, 3, 'https://en.wikipedia.org/wiki/Houthi_insurgency_in_Yemen', '2001-07-16 06:00:01', null);
insert into Material(id, lang, issueDate, licenseType, source, added, updated) values(27, 5, 7, 1, 'https://en.wikipedia.org/wiki/Estonia', '2002-06-01 09:00:01', null);

insert into Material(id, lang, issueDate, licenseType, source, added, updated) values(28, 1, 1, 1, 'https://en.wikipedia.org/wiki/Latvia', '1999-02-02 06:00:01', '2000-03-01 07:00:01');
insert into Material(id, lang, issueDate, licenseType, source, added, updated) values(29, 2, 2, 2, 'https://en.wikipedia.org/wiki/Sweded', '1992-02-03 06:00:01', '1995-07-12 09:00:01');
insert into Material(id, lang, issueDate, licenseType, source, added, updated) values(30, 4, 3, 3,  'https://en.wikipedia.org/wiki/Germany', '2009-02-17 08:00:01', '2011-01-10 19:00:01');
insert into Material(id, lang, issueDate, licenseType, source, added, updated) values(31, 3, 4, 1,  'https://en.wikipedia.org/wiki/Russia', '2002-02-02 09:00:01', '2012-08-28 22:40:01');
insert into Material(id, lang, issueDate, licenseType, source, added, updated) values(32, 3, 5, 2,  'https://en.wikipedia.org/wiki/Lithuania', '2001-09-15 08:00:01', '2012-11-04 09:30:01');
insert into Material(id, lang, issueDate, licenseType, source, added, updated) values(33, null, null, null, 'https://en.wikipedia.org/wiki/Poland', '1971-09-22 08:00:01', null);
insert into Material(id, lang, issueDate, licenseType, source, added, updated) values(34, 4, 6, 3, 'https://en.wikipedia.org/wiki/France', '2001-07-16 06:00:01', null);
insert into Material(id, lang, issueDate, licenseType, source, added, updated) values(35, 5, 7, 1, 'https://en.wikipedia.org/wiki/Austria', '2002-06-01 09:00:01', null);
-- Authors

insert into Author(id, name, surname) values(1, 'Isaac', 'John Newton');
insert into Author(id, name, surname) values(2, 'Karl Simon Ben', 'Tom Oliver Marx');
insert into Author(id, name, surname) values(3, 'Leonardo', 'Fibonacci');
insert into Author(id, name, surname) values(4, 'Automated', 'Automated');

-- Material_Authors

insert into Material_Author(material, author) values(1, 1);
insert into Material_Author(material, author) values(2, 1);
insert into Material_Author(material, author) values(2, 3);
insert into Material_Author(material, author) values(3, 1);
insert into Material_Author(material, author) values(4, 1);
insert into Material_Author(material, author) values(5, 3);
insert into Material_Author(material, author) values(6, 3);
insert into Material_Author(material, author) values(7, 3);
insert into Material_Author(material, author) values(8, 2);
insert into Material_Author(material, author) values(10, 4);
insert into Material_Author(material, author) values(11, 4);

-- Material Descriptions

insert into LanguageString(id, lang, textValue) values (1, 1, 'Test description in estonian. (Russian available)');
insert into LanguageString(id, lang, textValue) values (2, 2, 'Test description in russian, which is the only language available.');
insert into LanguageString(id, lang, textValue) values (3, 2, 'Test description in russian. (Estonian available)');
insert into LanguageString(id, lang, textValue) values (4, 5, 'Test description in portugese, as the material language (english) not available.');
insert into LanguageString(id, lang, textValue) values (5, 4, 'Test description in arabic (material language). No estonian or russian available.');
insert into LanguageString(id, lang, textValue) values (6, 3, 'Test description in english, which is the material language.');
insert into LanguageString(id, lang, textValue) values (7, 3, 'Test description in english, which is not the material language. Others are also available, but arent estonian or russian.');
insert into LanguageString(id, lang, textValue) values (8, 5, 'Test description in portugese, english also available.');
insert into LanguageString(id, lang, textValue) values (19, 1, 'Automated test resource description. DO NOT TOUCH!!!! material language Estonian, Description Estonian');
insert into LanguageString(id, lang, textValue) values (21, 1, 'Performance test resource description. DO NOT TOUCH!!!!');
insert into LanguageString(id, lang, textValue) values (39, 3, 'Test description');

insert into Material_Description(description, material) values(1, 1);
insert into Material_Description(description, material) values(2, 2);
insert into Material_Description(description, material) values(3, 1);
insert into Material_Description(description, material) values(4, 4);
insert into Material_Description(description, material) values(5, 3);
insert into Material_Description(description, material) values(6, 5);
insert into Material_Description(description, material) values(7, 7);
insert into Material_Description(description, material) values(8, 7);
insert into Material_Description(description, material) values(19, 10);
insert into Material_Description(description, material) values(21, 11);

insert into Material_Description(description, material) values(39, 12);
insert into Material_Description(description, material) values(39, 13);
insert into Material_Description(description, material) values(39, 14);
insert into Material_Description(description, material) values(39, 15);
insert into Material_Description(description, material) values(39, 16);
insert into Material_Description(description, material) values(39, 17);
insert into Material_Description(description, material) values(39, 18);
insert into Material_Description(description, material) values(39, 19);

insert into Material_Description(description, material) values(39, 20);
insert into Material_Description(description, material) values(39, 21);
insert into Material_Description(description, material) values(39, 22);
insert into Material_Description(description, material) values(39, 23);
insert into Material_Description(description, material) values(39, 24);
insert into Material_Description(description, material) values(39, 25);
insert into Material_Description(description, material) values(39, 26);
insert into Material_Description(description, material) values(39, 27);

insert into Material_Description(description, material) values(39, 28);
insert into Material_Description(description, material) values(39, 29);
insert into Material_Description(description, material) values(39, 30);
insert into Material_Description(description, material) values(39, 31);
insert into Material_Description(description, material) values(39, 32);
insert into Material_Description(description, material) values(39, 33);
insert into Material_Description(description, material) values(39, 34);
insert into Material_Description(description, material) values(39, 35);

-- Material Titles

insert into LanguageString(id, lang, textValue) values (9, 1, 'Matemaatika õpik üheksandale klassile');
insert into LanguageString(id, lang, textValue) values (10, 2, 'Математика учебник для 9-го класса');
insert into LanguageString(id, lang, textValue) values (11, 2, 'Математика учебник для 8-го класса');
insert into LanguageString(id, lang, textValue) values (12, 5, 'Test title in portugese: manual de instruções, as the material language (english) not available.');
insert into LanguageString(id, lang, textValue) values (13, 4, 'الرياضيات الكتب المدرسية للصف 7');
insert into LanguageString(id, lang, textValue) values (14, 3, 'The Capital.');
insert into LanguageString(id, lang, textValue) values (15, 3, 'Test title in english, which is not the material language. Others are also available, but arent estonian or russian.');
insert into LanguageString(id, lang, textValue) values (16, 5, 'Test title in portugese, english also available.');
insert into LanguageString(id, lang, textValue) values (17, 1, 'Eesti keele õpik üheksandale klassile');
insert into LanguageString(id, lang, textValue) values (18, 1, 'Aabits 123');
insert into LanguageString(id, lang, textValue) values (20, 1, 'Automated test resource title. DO NOT TOUCH!!! Title in estonian');
insert into LanguageString(id, lang, textValue) values (22, 1, 'Performance test resource title. DO NOT TOUCH!!!');

insert into LanguageString(id, lang, textValue) values (23, 3, 'Main Page of Wikipedia');
insert into LanguageString(id, lang, textValue) values (24, 3, 'New Zealand flag debate');
insert into LanguageString(id, lang, textValue) values (25, 3, '2015 Indian heat wave');
insert into LanguageString(id, lang, textValue) values (26, 3, 'Second Libyan Civil War');
insert into LanguageString(id, lang, textValue) values (27, 3, 'United States');
insert into LanguageString(id, lang, textValue) values (28, 3, '2015 Cajon Pass wildfire');
insert into LanguageString(id, lang, textValue) values (29, 3, 'India');
insert into LanguageString(id, lang, textValue) values (30, 3, 'Japan');

insert into LanguageString(id, lang, textValue) values (31, 3, 'Mexican Drug War');
insert into LanguageString(id, lang, textValue) values (32, 3, 'War in Darfur');
insert into LanguageString(id, lang, textValue) values (33, 3, 'Somalia');
insert into LanguageString(id, lang, textValue) values (34, 3, 'Libya');
insert into LanguageString(id, lang, textValue) values (35, 3, 'Democratic Republic of Congo');
insert into LanguageString(id, lang, textValue) values (36, 3, 'Turkey-PKK conflict');
insert into LanguageString(id, lang, textValue) values (37, 3, 'Houthi insurgency in Yemen');
insert into LanguageString(id, lang, textValue) values (38, 3, 'Estonia');

insert into LanguageString(id, lang, textValue) values (40, 3, 'Latvia');
insert into LanguageString(id, lang, textValue) values (41, 3, 'Sweden');
insert into LanguageString(id, lang, textValue) values (42, 3, 'Germany');
insert into LanguageString(id, lang, textValue) values (43, 3, 'Russia');
insert into LanguageString(id, lang, textValue) values (44, 3, 'Lithuania');
insert into LanguageString(id, lang, textValue) values (45, 3, 'Poland');
insert into LanguageString(id, lang, textValue) values (46, 3, 'France');
insert into LanguageString(id, lang, textValue) values (47, 3, 'Austria');

insert into Material_Title(title, material) values(9, 1);
insert into Material_Title(title, material) values(10, 1);
insert into Material_Title(title, material) values(11, 2);
insert into Material_Title(title, material) values(12, 4);
insert into Material_Title(title, material) values(13, 3);
insert into Material_Title(title, material) values(14, 5);
insert into Material_Title(title, material) values(15, 7);
insert into Material_Title(title, material) values(16, 7);
insert into Material_Title(title, material) values(17, 6);
insert into Material_Title(title, material) values(18, 8);
insert into Material_Title(title, material) values(20, 10);
insert into Material_Title(title, material) values(22, 11);

insert into Material_Title(title, material) values(23, 12);
insert into Material_Title(title, material) values(24, 13);
insert into Material_Title(title, material) values(25, 14);
insert into Material_Title(title, material) values(26, 15);
insert into Material_Title(title, material) values(27, 16);
insert into Material_Title(title, material) values(28, 17);
insert into Material_Title(title, material) values(29, 18);
insert into Material_Title(title, material) values(30, 19);

insert into Material_Title(title, material) values(31, 20);
insert into Material_Title(title, material) values(32, 21);
insert into Material_Title(title, material) values(33, 22);
insert into Material_Title(title, material) values(34, 23);
insert into Material_Title(title, material) values(35, 24);
insert into Material_Title(title, material) values(36, 25);
insert into Material_Title(title, material) values(37, 26);
insert into Material_Title(title, material) values(38, 27);

insert into Material_Title(title, material) values(40, 28);
insert into Material_Title(title, material) values(41, 29);
insert into Material_Title(title, material) values(42, 30);
insert into Material_Title(title, material) values(43, 31);
insert into Material_Title(title, material) values(44, 32);
insert into Material_Title(title, material) values(45, 33);
insert into Material_Title(title, material) values(46, 34);
insert into Material_Title(title, material) values(47, 35);

-- Material classifications

insert into Material_Classification(classification, material) values(1,1);
insert into Material_Classification(classification, material) values(2,1);
insert into Material_Classification(classification, material) values(2,2);
insert into Material_Classification(classification, material) values(1,3);
insert into Material_Classification(classification, material) values(1,4);
insert into Material_Classification(classification, material) values(5,4);
insert into Material_Classification(classification, material) values(7,5);
insert into Material_Classification(classification, material) values(4,10);
insert into Material_Classification(classification, material) values(4,11);

-- Material_ResourceType

insert into Material_ResourceType(material, resourceType) values (1, 1);
insert into Material_ResourceType(material, resourceType) values (1, 2);
insert into Material_ResourceType(material, resourceType) values (2, 3);
insert into Material_ResourceType(material, resourceType) values (3, 4);
insert into Material_ResourceType(material, resourceType) values (4, 5);
insert into Material_ResourceType(material, resourceType) values (5, 3);
insert into Material_ResourceType(material, resourceType) values (6, 2);
insert into Material_ResourceType(material, resourceType) values (7, 4);
insert into Material_ResourceType(material, resourceType) values (10, 1);
insert into Material_ResourceType(material, resourceType) values (11, 1);

-- Material_EducationalContext

insert into Material_EducationalContext(material, educationalContext) values (1, 1001);
insert into Material_EducationalContext(material, educationalContext) values (1, 1002);
insert into Material_EducationalContext(material, educationalContext) values (2, 1003);
insert into Material_EducationalContext(material, educationalContext) values (3, 1004);
insert into Material_EducationalContext(material, educationalContext) values (4, 1005);
insert into Material_EducationalContext(material, educationalContext) values (5, 1003);
insert into Material_EducationalContext(material, educationalContext) values (6, 1002);
insert into Material_EducationalContext(material, educationalContext) values (7, 1004);
insert into Material_EducationalContext(material, educationalContext) values (10, 1001);
insert into Material_EducationalContext(material, educationalContext) values (11, 1001);

-- Publishers

insert into Publisher(id, name, website) values (1, 'Koolibri', 'http://www.koolibri.ee');
insert into Publisher(id, name, website) values (2, 'Pegasus', 'http://www.pegasus.ee');
insert into Publisher(id, name, website) values (3, 'Varrak', 'http://www.varrak.ee');

-- MaterialPublisher

insert into Material_Publisher(material, publisher) values (1, 1);
insert into Material_Publisher(material, publisher) values (1, 2);
insert into Material_Publisher(material, publisher) values (2, 2);
insert into Material_Publisher(material, publisher) values (3, 3);
insert into Material_Publisher(material, publisher) values (10, 2);
insert into Material_Publisher(material, publisher) values (11, 2);

-- Material Tags

insert into Tag(id, name) values (1, 'matemaatika');
insert into Tag(id, name) values (2, 'põhikool');
insert into Tag(id, name) values (3, 'õpik');
insert into Tag(id, name) values (4, 'mathematics');
insert into Tag(id, name) values (5, 'book');
insert into Tag(id, name) values (6, 'математика');
insert into Tag(id, name) values (7, 'учебник');
insert into Tag(id, name) values (8, 'لرياضيات');
insert into Tag(id, name) values (9, 'لكتب');
insert into Tag(id, name) values (10, 'test');
insert into Tag(id, name) values (11, 'material');

insert into Material_Tag(tag, material) values(1, 1);
insert into Material_Tag(tag, material) values(1, 2);
insert into Material_Tag(tag, material) values(2, 1);
insert into Material_Tag(tag, material) values(3, 1);
insert into Material_Tag(tag, material) values(4, 1);
insert into Material_Tag(tag, material) values(4, 2);
insert into Material_Tag(tag, material) values(4, 5);
insert into Material_Tag(tag, material) values(5, 1);
insert into Material_Tag(tag, material) values(5, 5);
insert into Material_Tag(tag, material) values(6, 2);
insert into Material_Tag(tag, material) values(7, 2);
insert into Material_Tag(tag, material) values(8, 3);
insert into Material_Tag(tag, material) values(9, 3);
insert into Material_Tag(tag, material) values(10, 4);
insert into Material_Tag(tag, material) values(10, 5);
insert into Material_Tag(tag, material) values(10, 6);
insert into Material_Tag(tag, material) values(10, 7);
insert into Material_Tag(tag, material) values(10, 8);
insert into Material_Tag(tag, material) values(11, 4);
insert into Material_Tag(tag, material) values(11, 5);
insert into Material_Tag(tag, material) values(11, 6);
insert into Material_Tag(tag, material) values(11, 7);
insert into Material_Tag(tag, material) values(11, 8);

-- Repositories

insert into Repository(id, baseURL, lastSynchronization, schemaName) values (1, 'http://koolitaja.eenet.ee:57219/Waramu3Web/OAIHandler', '1970-01-12 13:46:39', 'waramu');
