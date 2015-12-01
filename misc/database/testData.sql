-- IssueDate

insert into IssueDate(id, day, month, year) values(1, 2, 2, 1983);
insert into IssueDate(id, day, month, year) values(2, 27, 1, -983);
insert into IssueDate(id, year) values(3, -1500);
insert into IssueDate(id, day, month, year) values(4, 31, 3, 1923);
insert into IssueDate(id, day, month, year) values(5, 9, 12, 1978);
insert into IssueDate(id, day, month, year) values(6, 27, 1, 1986);
insert into IssueDate(id, month, year) values(7, 3, 1991);


-- Repositories

insert into Repository(id, baseURL, lastSynchronization, schemaName, isEstonianPublisher) values (1, 'http://koolitaja.eenet.ee:57219/Waramu3Web/OAIHandler', null, 'waramu', false);
insert into Repository(id, baseURL, lastSynchronization, schemaName, isEstonianPublisher) values (2, 'http://oxygen.netgroupdigital.com:8081/rest/OAIHandler', null, 'estCore', true);


-- User

insert into User(id, userName, name, surName, idCode, role) values (1, 'mati.maasikas', 'Mati', 'Maasikas', '39011220011', 'USER');
insert into User(id, userName, name, surName, idCode, role) values (2, 'peeter.paan', 'Peeter', 'Paan', '38011550077', 'USER');
insert into User(id, userName, name, surName, idCode, role) values (3, 'voldemar.vapustav', 'Voldemar', 'Vapustav', '37066990099', 'USER');
insert into User(id, userName, name, surName, idCode, role) values (4, 'voldemar.vapustav2', 'Voldemar', 'Vapustav', '38103070239', 'USER');
insert into User(id, userName, name, surName, idCode, role) values (5, 'õäöü.õäöü', 'Õäöü', 'Õäöü', '38406304916', 'USER');
insert into User(id, userName, name, surName, idCode, role) values (6, 'mart.dev', 'dev', 'dev', '39210140032', 'USER');
insert into User(id, userName, name, surName, idCode, role) values (7, 'ester.tester', 'Ester', 'Tester', '38202020234', 'USER');

-- Materials

insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, creator, deleted, paid) values(1, 1, 1, 1, 'https://www.youtube.com/watch?v=gSWbx3CvVUk', '1999-02-02 06:00:01', '2000-03-01 07:00:01', null, 1, false, true);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, creator, deleted, paid, repository) values(2, 2, 2, 2, 'https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes', '1992-02-03 06:00:01', '1995-07-12 09:00:01', null, 1, false, true, 2);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, creator, deleted, paid) values(3, 4, 3, 3,  'http://eloquentjavascript.net/Eloquent_JavaScript.pdf', '2009-02-17 08:00:01', '2011-01-10 19:00:01', null, 1, false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, creator, deleted, paid) values(4, 3, 4, 1,  'https://en.wikipedia.org/wiki/Power_Architecture', '2012-02-02 09:00:01', '2012-08-28 22:40:01', null, 1, false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, creator, deleted, paid) values(5, 3, 5, 2,  'https://en.wikipedia.org/wiki/Power_Architecture', '2011-09-15 08:00:01', '2012-11-04 09:30:01', null, 1, false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, creator, deleted, paid) values(6, null, null, null, 'http://www.planalto.gov.br/ccivil_03/Constituicao/Constituicao.htm', '1971-09-22 08:00:01', null, null, 1, false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, creator, deleted, paid) values(7, 4, 6, 3, 'https://president.ee/en/republic-of-estonia/the-constitution/index.html', '2001-07-16 06:00:01', null, null, 1, false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, creator, deleted, paid) values(8, 5, 7, 1, 'http://www.palmeiras.com.br/historia/titulos', '2014-06-01 09:00:01', null, null, 1, false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, views, picture, deleted, paid) values(9, null, null, null, 'http://EmptyFileds.test.ee', '2015-06-08 08:00:01', null, '98765432', null, false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, deleted, paid) values(10, 1, 2, 2, 'http://automated.test.ee', '2015-06-09 08:00:01', null, null, false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, deleted, paid) values(11, 1, 2, 3, 'http://performance.test.ee', '2015-06-09 08:00:01', null, null, false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, creator, deleted, paid) values(12, 1, 1, 1, 'https://en.wikipedia.org/wiki/Main_Page', '1999-02-02 06:00:01', '2000-03-01 07:00:01', null, 1, false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, creator, deleted, paid) values(13, 2, 2, 2, 'https://en.wikipedia.org/wiki/New_Zealand_flag_debate', '1992-02-03 06:00:01', '1995-07-12 09:00:01', null, 1, false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, creator, deleted, paid) values(14, 4, 3, 3,  'https://en.wikipedia.org/wiki/2015_Indian_heat_wave', '2009-02-17 08:00:01', '2011-01-10 19:00:01', null, 1, false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, creator, deleted, paid) values(15, 3, 4, 1,  'https://en.wikipedia.org/wiki/Second_Libyan_Civil_War', '2002-02-02 09:00:01', '2012-08-28 22:40:01', null, 1, false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, creator, deleted, paid) values(16, 3, 5, 2,  'https://en.wikipedia.org/wiki/United_States', '2001-09-15 08:00:01', '2012-11-04 09:30:01', null, 1, false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, creator, deleted, paid) values(17, null, null, null, 'https://en.wikipedia.org/wiki/2015_Cajon_Pass_wildfire', '1971-09-22 08:00:01', null, null, 1, false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, creator, deleted, paid) values(18, 4, 6, 3, 'https://en.wikipedia.org/wiki/India', '2001-07-16 06:00:01', null, null, 1, false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, creator, deleted, paid) values(19, 5, 7, 1, 'https://en.wikipedia.org/wiki/Japan', '2002-06-01 09:00:01', null, null, 1, false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, creator, deleted, paid) values(20, 1, 1, 1, 'https://en.wikipedia.org/wiki/Mexican_Drug_War', '1999-02-02 06:00:01', '2000-03-01 07:00:01', null, 1, false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, creator, deleted, paid) values(21, 2, 2, 2, 'https://en.wikipedia.org/wiki/War_in_Darfur', '1992-02-03 06:00:01', '1995-07-12 09:00:01', null, 1, false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, creator, deleted, paid) values(22, 4, 3, 3,  'https://en.wikipedia.org/wiki/Somalia', '2009-02-17 08:00:01', '2011-01-10 19:00:01', null, 1, false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, creator, deleted, paid) values(23, 3, 4, 1,  'https://en.wikipedia.org/wiki/Libya', '2002-02-02 09:00:01', '2012-08-28 22:40:01', null, 1, false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, creator, deleted, paid) values(24, 3, 5, 2,  'https://en.wikipedia.org/wiki/Democratic_Republic_of_Congo', '2001-09-15 08:00:01', '2012-11-04 09:30:01', null, 1, false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, creator, deleted, paid) values(25, null, null, null, 'https://en.wikipedia.org/wiki/Turkey-PKK_conflict', '1971-09-22 08:00:01', null, null, 2, false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, creator, deleted, paid) values(26, 4, 6, 3, 'https://en.wikipedia.org/wiki/Houthi_insurgency_in_Yemen', '2001-07-16 06:00:01', null, null, 2, false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, creator, deleted, paid) values(27, 5, 7, 1, 'https://en.wikipedia.org/wiki/Estonia', '2002-06-01 09:00:01', null, null, 2, false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, creator, deleted, paid) values(28, 1, 1, 1, 'https://en.wikipedia.org/wiki/Latvia', '1999-02-02 06:00:01', '2000-03-01 07:00:01', null, 2, false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, creator, deleted, paid) values(29, 2, 2, 2, 'https://en.wikipedia.org/wiki/Sweded', '1992-02-03 06:00:01', '1995-07-12 09:00:01', null, 2, false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, creator, deleted, paid) values(30, 4, 3, 3,  'https://en.wikipedia.org/wiki/Germany', '2009-02-17 08:00:01', '2011-01-10 19:00:01', null, 2, false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, creator, deleted, paid) values(31, 3, 4, 1,  'https://en.wikipedia.org/wiki/Russia', '2002-02-02 09:00:01', '2012-08-28 22:40:01', null, 2, false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, creator, deleted, paid) values(32, 3, 5, 2,  'https://en.wikipedia.org/wiki/Lithuania', '2001-09-15 08:00:01', '2012-11-04 09:30:01', null, 2, false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, creator, deleted, paid) values(33, null, null, null, 'https://en.wikipedia.org/wiki/Poland', '1971-09-22 08:00:01', null, null, 2, false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, creator, deleted, paid) values(34, 4, 6, 3, 'https://en.wikipedia.org/wiki/France', '2001-07-16 06:00:01', null, null, 2, false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, creator, deleted, paid) values(35, 5, 7, 1, 'https://en.wikipedia.org/wiki/Austria', '2002-06-01 09:00:01', null, null, 2, false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, deleted, paid) values(36, 1, 2, 3, 'http://pilditu.au/dio.ee', '2001-07-16 06:00:01', false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, deleted, paid) values(37, 1, 2, 3, '<script>alert(1)</script>', '2001-07-16 06:00:01', false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, deleted, paid) values(3919, 1, 2, 3, 'http://pilditu.audio.ee', '2001-07-16 06:00:01', false, false);
insert into Material(id, lang, issueDate, licenseType, source, added, updated, picture, creator, deleted, paid) values(3920, 2, 2, 2, 'https://oxygen.netgroupdigital.com/rest/repoMaterialSource', '1992-02-03 06:00:01', '1995-07-12 09:00:01', null, 1, false, true);

-- Authors

insert into Author(id, name, surname) values(1, 'Isaac', 'John Newton');
insert into Author(id, name, surname) values(2, 'Karl Simon Ben', 'Tom Oliver Marx');
insert into Author(id, name, surname) values(3, 'Leonardo', 'Fibonacci');
insert into Author(id, name, surname) values(4, 'Automated', 'Automated');
insert into Author(id, name, surname) values(5, '<script>alert(1)</script>', '<script>alert(1)</script>');

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
insert into Material_Author(material, author) values(36, 4);
insert into Material_Author(material, author) values(37, 5);
insert into Material_Author(material, author) values(3919, 4);

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
insert into LanguageString(id, lang, textValue) values (50, 1, 'Missing picture replacement test: Au/dio. DO NOT TOUCH!!!!');
insert into LanguageString(id, lang, textValue) values (51, 1, '<script>alert(1)</script>');
insert into LanguageString(id, lang, textValue) values (10833, 1, 'Missing picture replacement test: Video');

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

insert into Material_Description(description, material) values(50, 36);
insert into Material_Description(description, material) values(51, 37);
insert into Material_Description(description, material) values(10833, 3919);

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

insert into LanguageString(id, lang, textValue) values (48, 1, 'Missing picture replacement test: Au/dio');
insert into LanguageString(id, lang, textValue) values (49, 1, '<script>alert(1)</script>');
insert into LanguageString(id, lang, textValue) values (10834, 1, 'Missing picture replacement test: Video');

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

insert into Material_Title(title, material) values(48, 36);
insert into Material_Title(title, material) values(49, 37);
insert into Material_Title(title, material) values(10834, 3919);


-- Material_Taxon

insert into Material_Taxon(material, taxon) values(1, 1); -- PRESCHOOLEDUCATION
insert into Material_Taxon(material, taxon) values(1, 1016); -- BASICEDUCATION - Natural sciences - Biology
insert into Material_Taxon(material, taxon) values(2, 1066); -- SECONDARYEDUCATION - Natural sciences - Biology
insert into Material_Taxon(material, taxon) values(3, 4); -- VOCATIONALEDUCATION
insert into Material_Taxon(material, taxon) values(4, 1066); -- SECONDARYEDUCATION - Natural sciences - Biology
insert into Material_Taxon(material, taxon) values(5, 1064); -- SECONDARYEDUCATION - Mathematics - Mathematics
insert into Material_Taxon(material, taxon) values(6, 1014); -- BASICEDUCATION - Mathematics - Mathematics
insert into Material_Taxon(material, taxon) values(6, 1066); -- SECONDARYEDUCATION - Natural sciences - Biology
insert into Material_Taxon(material, taxon) values(10, 103); -- PRESCHOOLEDUCATION - Mathematics
insert into Material_Taxon(material, taxon) values(11, 103); -- PRESCHOOLEDUCATION - Mathematics
insert into Material_Taxon(material, taxon) values(30, 10001); -- BASICEDUCATION - Social_studies - History - Estonian_history (topic)
insert into Material_Taxon(material, taxon) values(31, 20000); -- VOCATIONALEDUCATION - Computer_science - Computers_and_Networks (specialization)
insert into Material_Taxon(material, taxon) values(36, 1); -- PRESCHOOLEDUCATION
insert into Material_Taxon(material, taxon) values(36, 1018); -- BASICEDUCATION - Natural sciences - Physics
insert into Material_Taxon(material, taxon) values(37, 103); -- PRESCHOOLEDUCATION - Mathematics
insert into Material_Taxon(material, taxon) values(3919, 1053); -- SECONDARYEDUCATION - Foreign language - Estonian


-- Material_ResourceType

insert into Material_ResourceType(material, resourceType) values (1, 1);
insert into Material_ResourceType(material, resourceType) values (1, 2);
insert into Material_ResourceType(material, resourceType) values (2, 3);
insert into Material_ResourceType(material, resourceType) values (3, 4);
insert into Material_ResourceType(material, resourceType) values (4, 1);
insert into Material_ResourceType(material, resourceType) values (5, 3);
insert into Material_ResourceType(material, resourceType) values (6, 2);
insert into Material_ResourceType(material, resourceType) values (7, 5);
insert into Material_ResourceType(material, resourceType) values (10, 1);
insert into Material_ResourceType(material, resourceType) values (11, 1);
insert into Material_ResourceType(material, resourceType) values (36, 1);
insert into Material_ResourceType(material, resourceType) values (37, 2);
insert into Material_ResourceType(material, resourceType) values (3919, 5);


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
insert into Material_Publisher(material, publisher) values (36, 2);
insert into Material_Publisher(material, publisher) values (37, 3);
insert into Material_Publisher(material, publisher) values (3919, 3);

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
insert into Tag(id, name) values (12, '<script>alert(1)</script>');
-- insert into Tag(id, name) values (13, 'abiogenesis');

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
insert into Material_Tag(tag, material) values(12, 37);
insert into Material_Tag(tag, material) values(4, 3919);
insert into Material_Tag(tag, material) values(5, 3919);

-- insert into Material_Tag(tag, material) values(13, 7);

-- Material TargetGroups
insert into Material_TargetGroup(material, targetGroup) values (1, 'ZERO_FIVE');
insert into Material_TargetGroup(material, targetGroup) values (1, 'SIX_SEVEN');
insert into Material_TargetGroup(material, targetGroup) values (12, 'GRADE1');
insert into Material_TargetGroup(material, targetGroup) values (13, 'GRADE6');
insert into Material_TargetGroup(material, targetGroup) values (14, 'GRADE9');
insert into Material_TargetGroup(material, targetGroup) values (36, 'ZERO_FIVE');
insert into Material_TargetGroup(material, targetGroup) values (37, 'SIX_SEVEN');

-- Portfolio

insert into Portfolio(id, title, taxon, creator, summary, views, created, updated, picture) VALUES (1, 'The new stock market', 1000, 1, 'Some new stufff coming soon.', 325698, '1999-05-02 06:00:01', '2002-05-02 06:00:01', null);
insert into Portfolio(id, title, taxon, creator, summary, views, created, updated, picture) VALUES (2, 'Math for my students', 1001, 5, "You have to read all the textbooks in this portfolio. Don't forgert to make ALL exercices.", 7951, '2000-12-29 16:00:01', '2003-05-02 06:00:01', null);
insert into Portfolio(id, title, taxon, creator, summary, views, created, updated, picture) VALUES (3, 'The new World', null, 3, null, 0, '2014-12-29 10:00:01', null, null);
insert into Portfolio(id, title, taxon, creator, summary, views, created, updated, picture) VALUES (4, 'Biology for my students', 1016, 6, 'A marvellous summary.', 2, '2000-12-29 16:00:01', '2003-05-02 06:00:01', null);
insert into Portfolio(id, title, taxon, creator, summary, views, created, updated, picture) VALUES (5, 'Sports for my students', 1028, 6, 'A brilliant summary.', 3, '2003-12-29 16:00:01', '2003-05-02 06:00:01', null);
insert into Portfolio(id, title, taxon, creator, summary, views, created, updated, picture) VALUES (6, 'English for my students', 1054, 6, 'An intriguing summary.', 4, '2007-12-29 16:00:01', '2003-05-02 06:00:01', null);
insert into Portfolio(id, title, taxon, creator, summary, views, created, updated, picture) VALUES (7, 'Automated test resource title. DO NOT TOUCH!!! Title in estonian', 1002, 6, 'An intriguing summary.', 4, '2007-12-29 16:00:01', '2003-05-02 06:00:01', null);
insert into Portfolio(id, title, taxon, creator, summary, views, created, updated, picture) VALUES (8, 'Test portfolio number 8', 10100, 6, 'Bla bla bla.', 1, '2001-12-29 11:00:01', '2001-05-02 06:01:01', null);
insert into Portfolio(id, title, taxon, creator, summary, views, created, updated, picture) VALUES (9, 'Test portfolio number 9', 20000, 6, 'Blabla2.', 1, '2001-12-29 11:00:01', '2001-05-02 06:01:01', null);

-- Pictures

update Material set picture = (unhex('FFD8FFE1001845786966000049492A00080000000000000000000000FFEC00114475636B7900010004000000500000FFE1032B687474703A2F2F6E732E61646F62652E636F6D2F7861702F312E302F003C3F787061636B657420626567696E3D22EFBBBF222069643D2257354D304D7043656869487A7265537A4E54637A6B633964223F3E203C783A786D706D65746120786D6C6E733A783D2261646F62653A6E733A6D6574612F2220783A786D70746B3D2241646F626520584D5020436F726520352E332D633031312036362E3134353636312C20323031322F30322F30362D31343A35363A32372020202020202020223E203C7264663A52444620786D6C6E733A7264663D22687474703A2F2F7777772E77332E6F72672F313939392F30322F32322D7264662D73796E7461782D6E7323223E203C7264663A4465736372697074696F6E207264663A61626F75743D222220786D6C6E733A786D703D22687474703A2F2F6E732E61646F62652E636F6D2F7861702F312E302F2220786D6C6E733A786D704D4D3D22687474703A2F2F6E732E61646F62652E636F6D2F7861702F312E302F6D6D2F2220786D6C6E733A73745265663D22687474703A2F2F6E732E61646F62652E636F6D2F7861702F312E302F73547970652F5265736F75726365526566232220786D703A43726561746F72546F6F6C3D2241646F62652050686F746F73686F7020435336202857696E646F7773292220786D704D4D3A496E7374616E636549443D22786D702E6969643A43383635353242344646383831314534394546384645443039353333324444432220786D704D4D3A446F63756D656E7449443D22786D702E6469643A4338363535324235464638383131453439454638464544303935333332444443223E203C786D704D4D3A4465726976656446726F6D2073745265663A696E7374616E636549443D22786D702E6969643A4338363535324232464638383131453439454638464544303935333332444443222073745265663A646F63756D656E7449443D22786D702E6469643A4338363535324233464638383131453439454638464544303935333332444443222F3E203C2F7264663A4465736372697074696F6E3E203C2F7264663A5244463E203C2F783A786D706D6574613E203C3F787061636B657420656E643D2272223F3EFFEE002641646F62650064C0000000010300150403060A0D00000D400000139700001BCB00002901FFDB0084000202020202020202020203020202030403020203040504040404040506050505050505060607070807070609090A0A09090C0C0C0C0C0C0C0C0C0C0C0C0C0C0C01030303050405090606090D0B090B0D0F0E0E0E0E0F0F0C0C0C0C0C0F0F0C0C0C0C0C0C0F0C0C0C0C0C0C0C0C0C0C0C0C0C0C0C0C0C0C0C0C0C0C0C0C0C0C0C0CFFC2001108008C00D203011100021101031101FFC400DB00000202030101000000000000000000000203060701040508000101010003010100000000000000000000000102030405061000010402010206020301010000000000010002030411051020123040213113062214321516412511000102040304060608050500000000000102030011120421311341516122719132231405304081A1425210B1C1D16282330672A243341520B2C2832412000104030101000000000000000000001140600121001020701213010002020103030305010100000000000100112131411051617181912030A140F0B1C1D1F1E1FFDA000C03010002110311000001D2D5CCF47C8F87A36470C82423E306DEBDF1B749E7A3AD7986DC1816A0A9156A0D7B516A6B6262F8723647A361C32090C296C6F3FD7B0F1DB424EDAABA36487679F2CD9E469ECD21400AAC4DA95D7A432553D8BE4748C1A8D8623A0C24D2E5F6265C5EADB2E5E1CD91D6716DA83EDD7DFEEF9A1A05016A9545A8A42AAD7CC3621A8E46C310838243231CDF457A79DD539DBCC0B15D7BABBDD697EA5ABD3F280B8156A855A9542EBDABA6CC5E8F91D0E43330410495FE9FA2BD3CEEC954D7948F5CEA9E8B0EE8D1676FF985AAD574B558BB52A95551B174351D23023327D4506B48E3F41D1C7A633B356F6BC9DB24BB9BA6AFE9E3BF2791D9BC60A02ED58AB52AA1569B17C861A1C1993299191586BFA1B238FD1D5BAE79AB18767BF879C84F4E9B7F6FCA0A002A14A54D2956A16831748F19218411932998E461E9F7BCFF00A3DE6B8F6CBB38A19BB393F47CCF5F3F38541574002AE94AA55D023E470E90CC983E3210245397EBA45AB3D2CF7EF6BE789EEC67BD3F222985F81A5AAC5D2D556AC062E1B0C430E3E08C183372E34F53ADC5EB4FF97671376ACF6F9F9BE54733D393E51040A001576AC459B12306C861993000269E7BEEEC7AFCC3D79696ADD2BBA3D3BC560996BADF3E573509F1F02A00502E17532C5F23A1D219F180566D8EDDB76D159EED5ECD3EA7F3736EC7983B30B3F8F6C3E6CDFB7D69A792AED9CF4E6CE7C1F027CA0A35AB7160D91B0C4F8C16F69EDBA31DDE4FDDD160E1AA62C6C0C5B794E59AB8654FECCA9BCB7FB3B472A4A9B2D34C6FE43300D0281AF635181C8411F2FA2B8FD439978FBB76FB738F4F42CDDB8F4F66289518D844CFC73D3BFDD5CBCDB994E561682DFC908DBA7E30052CD7B1B06867C1961E9E8B6397D088E8EAAA3A32F4DB8BA196B3CA75F6418D79633AB7F9BB5F6DED971CD36F3D0EE9AEB77168EDE32302D4455868C824C83531E7EF90795EF6B4D912E84DFA7CEB9DC9DDB86F6CC465E763956DA7AA0BC5E9BAE1ABDFC26C797BFCD816EE6C985002D4B1706861139D3D1C6E6F7E49C5D1C7D98D7FD98FA5B5F9B2D6BC24DB7EBE563971F5E54BDEB87F37A3DFD4DCECF3EF4BC47963E4CEAE3E46787CA008AB1887066539BA7DBABA75F425B9B936E3A797D05A3965B75A0EBE78F3265CEC6D53974F8DFB2F1A677B71F55EBA74E96FD103E9F363DBB9720027FFDA000801010001050208208703AC413152DE8A17457AACC7C028A3D010E47435A5EED4C357BA7AF13E2DAEA2B8749AD95A75C2CF7C9198FA8A3C1E0703C03999FADD74D028DAFC58A523DD2EAFE451D075595F7CBEFF0049E0A3C0E078103FFF0046A1F4611873C2B5306075B799AC63F7BACA3C04381D7F211B1A6252D12B9A9D2F72B0220AF5BAF1CF27E5B1E0F49F0F3C36CFF69B8B3B77C517F71BC82A4BB6FB1DEB514DB9A96E78A6BB5F5BB0B72C1AE92C4B4FA4F07C4B153FAEDA8BBA92D96266CD7D835DF2C62FC721DADA9A7AB435EDCE001E1043C0BCCF92BEB767057AAE7DDBB5457DFC8EB36CEBA5DA5B75A92AD71133C03C0410F00FA8A4C6C16AF69652E7EA5EE8F5DA4A75D597C5F31F45959EB3C0E8CF4E537F23B16C6D8359B5648856A78BF276C3A083512ECB6D58D5B8B2B2B2B3D410EACACF0FF8DCE9755F5B9F5B69F06BA7D6B6DDFB5A7A31497E9EA296BED7DEB61535AC64AD9273969CACF58410E8CAF5275FA2BD764FB4415751429D49B65BAA32D9B1661FAFE8EED593E8FA6743BBD25BA773512ECE09BEEB42F0D55393E1FB54F5EA5C65EFAB611C83959EB1C678CAFAFD40D83BFB97DA2EB5FBDFA0EAFF005B431FD6758D9DA18C6F692CBF563BB5E0A51C2BEFF0FCDF53A52776ECC9EAE1F86FB562559E73C65656565656786873DEE83F5F5B5366D8DB69CEDB4C2DC3106BE67ACA6387648725CEED1BD805ED3E9ED00E89D1B94DE89C3B96E75ECA6FCACF195959E32B2B2BB97D76AFCD61D6A9BD5B115BABAFD34B1EE2898840E910782A3C764BEDD80C77E786B57ABF5B8AB2D3CBDEACBBF3DDFD95D50CF766B7C656567C08AADCABAFA2E7FEA4F761846C27ED66837975D043B163D1646E4C68ED914CFED837AE2EA11D99256C7FB70C9637F62EC14B471966D74A69C7959E7281595959595A8A352566F771FBF2086C4D5C5CDE57330A4F76A6AC10D471B3188AE76A6B8765AB0230F9FF000DC6E58E8A9EC3F620FDAD731682B54B8D8AB76A9E28DF1DFA86959CACACACA1C7AAF5E2DFEF66EFEDFC743FB7CCBFEA3E5D663F768FC09B9527C09F9ECBF8EE93F8EF3F53E1FB0FB6BFBFB349FB1F057FF42B71FE995EFDBC747FFFDA0008010200010502F10BC2EE59F2E4A2FCA2839772EF4D767CAC851E31CB7D3CABFA421E55DD2D087953EC188B020D6845A31EC8808795F559C26B96134227CA909CDF5F65F8A0329A30B1E59C83977273937CBF6E53D98594D6928B0E1BE542C958EE458D09C7D3B934793CA0B384E0BD420537053F098BFE2CF8EEE0272EE3D2CF7FF9C03E31595ECB3D594EE0F0D3E2391385F2232820741E0A33269CA280CA68F11C53BDF0981630B3C9E0F19E329AEF0DC79ED09B9E73C13C008B70B078C700E7C2282385F823EDD07808229DEE826F4FFFDA0008010300010502F1046E2BF59C9D139BE5C0CA1176A081E248014F616F95ACD44F00ACACA91996F9483F89E8213BDBCA41FC79CF127F1F291B308E1ABBB20673DFEBDAA2CB958003FC9C2EC858E482B3D8DCE7CA5790357BF1943896727CB31FDC02C229E703CA81950FE087AAC22404E7E53C7AF946700E17C8EE4B728B08F241A9AC08F1959E07AF05108B3C78C70D47A823CB9BE30F458E31D43A5EDF123083495F12EC4E041E9637B8F627B71C06A94F890858E3384F1958E98FDD1195D9845C9EDF0E36772637B78C84538F18E070C6F3272463C2AFECE4DCA194728F40E1AA44D43DBF05F8A97A7FFFDA0008010202063F02F2C0EF2D5963DADA45FFDA0008010302063F02601570AE158D18CBC03579490AB13D7CA63E581A35D7FFDA0008010101063F02F493D15F5452A4AA63A2294B9251D8AC3D5C2466728C0871FDAB94E5FC31A7338E72821B666ADF5082293D509B6573CFB13CC7AB2EDD0BA084CD67EC84B897E67E59CA245B49DE6A82A01091D31224278C38E9E7D31CA61293D970D0A1EAB713CAB09F7427E9328A762A60886E5812EA65D7EAB703F1823AA12A093D312544A71DEDC36DFF001AC27EB84E8DCB4A54F0A569316F3C94B4AA5EAB7A2B76D99B34A5250D1A54B56D99CE14DDA8BB9349A9443CE92065324AA17E616F76FE9A2653A8AD66D52E9845BF98A556614C6ADB5AB15212EEF24CE6605B37E5C2B22A2AA36113CE50B1E636094028DA8116B74F77AFB09525B391294E00C32E5CFEB2F157AA7F90651362EC245DA46242C64A96E3147896D970F69058528F54A1AB169A74D8EA2177B76E35A282941A836DA4804D47DD1E5F796E948BDB0555680E4B1F1367828473796799B6BF8DB448A7AC2A1C6EDD02CE69299BAA9AC4FF0008FBE1992795B4A5A4744A00180197AA2E427213CF7477690990D90A798783770B9D21626024C36976EA80C7F56582BAE1A530F4CAB95E6C9ED711030E2610AAD4669068E27D548391C0C693DD99E23ED8F1165E66F30D9ED5B93347B36C4957D5090C54F38AC76F2E10AB950F13707FAEBD94EEDD1CF86BB8100244C9E00089652D9EAA13504CF6984B85D4B6FA15CA553911D309B7BAE4A8481D87A0ED89E1D70D8B74C98C839903D10BB6EF5FF340D2941E5A6969291298684E7ED30B4FC0BE647AAB4953B473856189E5C603EFBEBD07D3DDACAB981DC1236830FDBDB3755AABE073256E5F0308B2F1CED0E0548296001213C49869179E62B21D5D0BDC93C3DB163716ABAD6DD6871C56652B49C3D862C16E82B76E1EA1084672233302D51CD70A04A1897310924190F6452A052A198387A80004C9C84778DAAD591DA716247D822D6C6C8537BE6CEE92AE8F33886522A7943761845D0B5452C791DAA8848ECEB286093F57B2021F75CF0E553B94A30206E1BA12E5B361F4AF9415E69DF39ED873C2B458765DCF312906052C2D971B489BAD72CCFBA716CFF98ADFBA7FC3975861692273C045BF9ADEBFA8EBD7ADEA3647626954824EC1C23F6E3B9D6AB94E133DA04ECE9895D5A850DE46220BB60F8A653D073EA0A820E046041F4C2FE905D5BD4364EC48C0C70DF178F2BF43C8AD12DFFD8EF78BF70023C5DC8FFD3E72B372F93F2ABB03AB1853C5B9A544A8B590513B550942121284E0948C008E534C7857F992BCCED1C44384AB55D71210A748972A4480117987359B8D3A7F2AA5F6C7ED0787C4F9FE661114E7BE129DD329855E5BA7BD02A7123E21F7FA54A1226A59012389845B366450902637EF84A6E8D21382D7F6C5B5B4E4E7EE5F3053EFF060AA7FEC0984DBDB365696C04A129C801801135274E31380DD09812FA3CCADA5FDC5B388971961EF8FDA572E09866E6973D88523FE30247988C38C3637411D51ACD2B91D548B5F29F48BB954A8B6CA7F32A0A12F6AADB32284E7172CA24C97DB5B6974E34D4253872E5D0022D99433E5EA9E147C47A76402D4A9C94AE3F463023082A2626E2E9D434238930DBBE2C9D1BC5DD32C539054F93F9A186D5DB68CA5F8607BE0B565CCB064B737424B8B2BDB8FA472FE7A6DB83B0709A77FDD01684D4EAF196D3011796A594AB35AC484FA6356D6ED45B40AA818A93BCCF6880D5BF949BA6D2665EAA44C017168E59A8FCE30EB898897D121893B20992438C90E209C84B3F744ACD1AB3EDB8701D70B754A4B4F2716E8338F0CC8521C51938F6D96E1153C8ED6F8F12C62C8FD44FCBC7A3D1BD7B7CEA52C5BA800D2B00B3C61B6D2B09B641A5A40C061B61A5585DA12EB60776BC8FB4469DEF953AFA5C9CF4C6A225C48C217E1C2AC9C71DD27ECF6539AB0F8611A2A98DB289B6AD76F6B6ACE24534FE1849DE213BD539409FC2270B60B479F0E68ADF791E5B6CDF2A1A12D4207D422A45CA9E2A1895AF28D56DF0B792A554D7E631CC89C2D0B482958A549E061C6675373EE97C3D133DAF0D41F0FF255F165B6113CAA34CA051E2A8C2746E81E0EAFF1F851E225AD3FCB843FFE6F4BC7567537CFD907C2CE8F8B77D1CF9C33F272CA2DE7F8B3F646128EF655F08B49766A5D74F6A52C2728C2BCB6CE19F0F56B5474E9CE04B5EAE397BE11A9FDA486B6967C672E6EA867C44A8E6D297BFF00D3FFDA0008010103013F211F403A0CCD7D21212B2E7A8E396B4FE5803858F37B178654A95122448C482082540810741981D04A950E1B6A82814785761C7AC64E915A7E523C2BB0737B5ACA701D9B7E6580AAC9E0ED71CA5BEFE3C3163D5E81120944302081021060C1AE98F62E637A86FBC567E513503B33FC8C502B82CFCC238499D995AD925DDE62A956079D3F31227463047A9441A841020409508747AF0C941A1750DBCE6314143C1BF28957302DF93728F31944631890412BAC7410EA74749FE3046A0108A94A94ABB6A6A2F93F92134DB86D1F14C3B4A679DDAC7A9E8C7A109508420CB972E0F45E2FB45268D624B463E0526A6311FD0A603DBC11A835B79826ED366460EB51C05F663DAEBC45B9150A62ECD6DD464B2D645363584F12B65B8365C996DDBCD4A0DA1A3D71D58C7A17A8842107E8C4B876539EB5AA9945A6B93CCC5CCE78FBF00C0FA9EA00882D516CDC71325C8C29A64E470D30927F784F3CE26A1E0422AC2A5F5838EF49C8416FA7F3009D3A1E08CB8B18C624AE82CB7404A950C4B98971876EE5528CBDF50E73B5695F3190EF0845277E63D8356FE371A402242CB631EB400698B7C4A022E5540665C6318C63162E8BA154BEB72FA6A033217D06572E2EF9C0F5875B393A1CE943E61F270F23E0DBD66675186F071D0C4E7E112BCF74625DCB63E15C756E5C58C63D18E831060F45CBE97518AA9C168BED31284C0C35531110A2E48A1C9607A452D7B55622DE3AB725DD9D8735707A8A72C0B62BBB4ADAE3513C5653F9FCCBFA4162C58B311450874B97D458C49476082B017E26A34459B9033C08641B9F608B6AF0CA6C66A8B1496B1DE2A77350F086B470C4B621095C60C68AF798C6CA28CBBAB167BC4911A12ADF252B52FF004C158F67AD718B1E97154517983D2FA0508CA22D5780832E6CDAF87CBFC41D8E3793AF96AAEF3C790FC5DF43217802C7369971502E91668899B2298C7B4B3CAAED598B1B69811DEA09BBC68EF52FA4D815DAF019CB44508C86D07149883A042D544AC0C9967E205E6EE4A8D311737D97F7F3119AFD3226C7A97D165C21D06213718A8C279441EAF31A0CDFE48F9D851D64F9FDEE266BBDDE43F666284DA8A2F06EB5C7983086841760214B7AEC652DD31DE73B74CB522EE58CC6003F398F4FF0B56FC4825C667A9858BD49477CF97A81D2510D5B7D1F9858EFD2E30BD074083A173CBB9D928986D07B5CBEE66601D1A0397E65C3A2B907D810699637BFC0085D11E72C7BE38394621A4C450E1BB60D9BA8A1B3E616BF1A4679469B2D3E0846936BC5DC8E452E273E2C0241FCBDB8F0FE27ABA2E3D0207E961A5C69B02AC3EC434DA275B7BD152E1858B24C255D5EA54A6E8AD6CCE2DA76F5899A1E423772DD18825613D8AF5EB0566B96E461B833105878440705388539B3DD996955936DE0FB6A15BA06FD12B80F164CE615FC1DAD97D0C317060C13AACB87229317F9BFB6616B1AB602E017B4FE6120A8E2FDE6E4A29444A0706331EAE1018EF785F79A829EEFD164201BBA4A8334C671EF2B47117B45A4646DFF008F2F933CF9E4C3E7FD45B6F6D9D72A54CC15FD79078B799448BEA2DFACA34A2B7E7A1074AE2CB740E913EB8C99CD686F20C876372FF3B947459EB83C440176A6AD66AF89487159D4DE421E3F885C6311EE0CE558310A6902EA91F69401FF002E60D1E795C4CE5804F785026B1F15039191884A545A28A7BFAC6152E4580B0D763BCC9C2A925B9A008E2C47358C097B2A60360D37822DC5AF4852426204F73B1F269843D7DA6611FF0050B9F148EE7F56EF3ED39CFADAAE7FDFE5E7F13CC6FE62E0B9EE767EC62A62EDB0FF00D2607DE9FB03C4E1B5AF2F44BFBCD769E84E394EF5F141E9AEEA60EDFDA2E7C839E7C5667ED8BD7499B176B617FEB47E6CDABBEECEAA66663D3FFFDA0008010203013F21FB80E60585BF4F416C752910846040FD3457018A84BB836FD29E847A0C41FA63D418B5921FD2F24B996A920188AB2C134610A269FA473145FF00D96C9DFCCAB0E99EA210F79635FA5C97122859974D7570377FA62C3E495ED1583F4F65162F1C82B4D3F4BBC0998AD84E10342672C7F46C6799AEE054C9B8B184AB0476CE707F405D0E3D664C28AE9589692A287697D2BC7DE5708EEEDD36F43517A146FA075598FB8F886330A3AE90625CB970E8AC4744B7886BAE53F709C47705F52D6925269D03A171322256E147103BC5CB30FDBAE65960C5BA611B8E7A103D2B964B9DE78A7887454FB5594B9DFE8F63F43D77BFA2B3C4F395E3E9FFDA0008010303013F21FB9C6C3B84DE1FA7454431E7A2EE9963136BFA530BC06302631BC063F4A3A46040E83A7E90FD2A654A94228C74BD21FA3AB975BB895ADE85BA914910CF40884FD23F057D7BCC26A7654AB85F6FE2549557B114ADFD25C734A8C0CA743DA06F0FD2EA1568C5CCB778C1C454ADC4473FA5B151F288D65A6F63A9B9FA539BE89A4B31733371CCE0BF44EF499F4BB19618B2E88312C9D9FBE357D348B316FA9166D35EB767EE85C3A46789E728EA74DA259F4519FB992FA60BBCBCAC32BA30E8F41D2D04E7615D3EE3DCC3103B314213B58F6F521319AEF0854A2E95193EE031E65CB311D18664988F4095286E1D123E22F4BDF6DB5747D19F34F2E99EA25E5A897A94DA3DF5096FF4FF00FFDA000C0301000211031100001046E0EA6D204394C1E766D575637559566C6CF51401FA0BD763251A208CAB9D20085D9C6FCF36089B0E1E3124B4CEB9FA2396B95BD9EF56C9532F70C26E197A96BB91D887D157F1BCFE085866399F50E3BB5E337682213A0D2215C3F2897CF65895964257AAC384DA697012E8A2BD9F50EF5FCA10C4DF3220EB033A368FBB83D889597B3E71F106927FCA0042022EEEB0D4AEC8D041E696EC0B6C52D02AA09D6036BCF09062766FA6A1545905A7B73E999602C25E440AFF00FFDA0008010103013F10A2B8CC154FE26BA80C57BCE3C9C455B3507B2014BF88076818F4D4466A8DAB8F37C4264950C7DCE311965288D5DF4CC2460A24E4C8F46287C98629B0299871B8147CC1AA3F33578ED0AF118D6A527F72EBE789571ED3D135C3C240AAD4C12A0299AF30C7F89E1861BCB9660506A283CCC03C85A3CAAE00E6263C47A3990D793CF68E1B4DB3A77901EF16467946154D837E08EDCC25C076B20FC44E2E9755326F90E2F51F99161D2C690EF889A4D732D57598DBC44FF481CE3307A5CBB1A25F6D54DFCF6E8595C4A83F101E35B997055C1683077850CCFCD02A870C0F22149E60AD5A7A320782A965963CB8E9781CFAC3E3932CF968167D62C004B95F1907C4AD5EA42C4E6ED94525238B316F155154BB65B4BEA8BE228F9E27E1E2278F9819F29B35E266B47A4C31B82EC7D27F2540AF0DC238E60EDD9A898BCF9947105F30A792584A8A0FBCBE885835655AF4C4AE9A062EA0EA3E732AE80B960541A959ECA68969E531E65A53E3C27F089BD8E582F1C6A346F337951E9A6EF3E23DAE2265E904DFB1DE5FFC9A578C3162937CCA1B966330479C4DBD20EB3EF0996813B24BF7B8BC20A0A6BBCCA06B4C4242E832E7C4B3C2AD1BDC8804B04426283DBE929C4512D3A8C629DCC57D711AD8CB3C55CD72C4662F749E5ED0390CCBD9F695EDCC20E36CAB8A852B33340B8594ADD42FAACCA33BED0EEDCC57016FB433FE65FABE06BB85B98DE2874A44A2C0A82DCD691966D80816C952EB08B052AB4DEABCC1FAB89605518382DDEE26F0D59D00C3BC284604ECB67C2C6D2BDBBA0B0028500AA7B188D7BC6A0ACB2CFAC3BB890E9657F8653DE13980758F3177C7888F79E0CC1B65358E399E7BC39730B15F8964CD7871EE42ACD129491DB04BA82B472ADE205989FC6C09A6BB22097D710D795B690035D0D243DFC767F829B3255F620315C65F17811C525E5BD43C4E63118662A8BE5418A01CC18026D71CA077F665423D9A276C448E0DF3995FE4F4973456A59AB89003981834E1801A8ABFC813FA8A2763140D4E35A6EE32279E2399585AAF7605F312048D8ECBABC4437448993802C3786EF9A80A0D9B1605E2FB0DC2B0955E93CA15B0368CAAC05ABC0EA37C711116EB30F6CC4BE088F8E6537B8D99812B5CCB9DCF98B51143BA0B5065B39C4F080BA656CFC40C2947811FC315DE36D82EF581F865BA8586D644D2B14BD25F3FF4B0372B3985352B701C4305C28B603872DAAC096D5F53384A25E8C198CDF99787461C54AEAE03444B7E23E536458B8867AF5012FBA661A89A57131D5CB4ABF460CDCBAFF30CBF896263E21DDA9CE37E205CD71A880A60396A8819D262E234514C2E061B83E94EB4F11B623516F0B3ECC4422860C1832CBB0F96233490F5E04C54185440D0941C66C03EA0FCC0CA778F97CC6CF68E105F3DE03CF199F9F50ABB74796E779C47E6775C29F5FEE04C2E7994FF00D8D351B6D83816ADA3CDC010BABDE5E2EEC2F10887BA8E013602ACBDB519BD1A28B756667375A53A12D901D29C95ED6B1E639BD1B05873A54154E662E11A09180058070DA664F1ABC198080672BAD3005DBAB0B0A6FB2A32E1891888BABB2024CB9DF10F28D8CB16F3EF01975C7316F1BAE657B4F557896377ED28ABC21BA6C99AB1C4B9EB1C6172B36C3005AAF01002999486C717CA03BF1017D58434E630212AF590980D4DAF4AB8017ACD5EF2C456300164505AA1A2CE29616860AA4B8843C96DAE60D95C14D81E88E7366C8A5D423515401AB07E22E56F4DB20D4B3E0BAB71294B745428A42C966911BB7A887AD8F2DC97073195B1C8EC838C5D9BEF02C6178B02D2377540610BF4257501C8894C3CA2DC6E29CC5A3364C17CF7E877E6A26C659BB6E2BCEE6A5777B825E70C1E06E45616BA5DBEC259EC26AF1C1ED099EF121107753E98E6A87B364B19096FAF56D6815AD1544D1730C8843A4C0801E8400F75714DE4B335086BF3000C353BDFCE1A9E7D8D851DB9A369E51830081C7A74B3082027EEBCB1974A0F93C4414AB098B2CAFDB53B67399F51C25DF0C6C26A36C92FBEE2755EF2C5B9B567A3CCAAA8AF10B718E6205CB32929DF10B9835C450C42EC00F96023621CB813EAB2913015096276C5BB7BCE1D6E7755F017A30DED938858380131132CB67F78AB6540055BBAF04B61D03BDB29D005DAC18BC471983477651B21EF95F50440E6D32F447D43E931C44AEC72C6F484C2ACE43BD4A4E9BAEE4DC716180142D66EA68FE15071679F48F9DCAAFF0032BE7BC4F99FF585609D63C411E9DE1C3BCA1F29908DBBAD7D52572EBF55BBC3D45D4AFC513AEF44AC5BDE00B605829ACED1050BD4609102938C9BBCC4B28E1C4384D906FF00B98C11A21C9DB442D8ED3154D6EAA2427752B157DC95E701B282857708A6A0C80B0AC80940CF1095681DDBFD842D2B5ED00A81C5590368BE5F1F31D3C0A796C5AA857D5861B806062B7FCCF5CB0C3EB36D7B919598F09752DACF1A949E3BC703B2E4EB12E934E4BE045F76C5A31502C7766E685636CB6CA79A32C9171F7B0564B0165DAA114BFCBA6D25D1FE262B69160EC984B2EB0A12F0F9940BDC765302232CE6EFB42D629346732DCCAED384E75711932A0B67BF029DAF51C29D4E9814CA26129B8CDBA583021065B3B35011F1C1BFB5B65BE65113076ACA46F269BD7A689ACDB1BEB98FFDCC38733F65C476E3B4039B616B489CB8944C4260B1ADB441057326B5B6B5980ABB2536EC181EB06BD017B502BBAB4422300CA163363436C50D7640A345EC10D086809586E2541DD45F4461941F9FD120007C01AF12276476F02F7EF2A582D01E66BDE05D3629D9DE0A9792A2009129C0C43FC0C202168E36A519AA83C2AA3079351EA3C4BA01C0A53241A6103403B1A94645ED5543C8C66EC0BDFB357FF27307BC3E1CC70CCFEF3BBED0EDF79D89E5A8CDE27E0E3FF68F05A8F98A1B9773C7DDEBC7AEFAB8FEB3DEFC66AF35E67CE54D07A3B5799F2A0E55C4D55F0D799E9A3854DD71C155554F7AED9ED3C7DF72F5BE67B6A995466FB1D94A7BDD7FF7647F13C0C5E8D67927F2847BDFCABFCCC7CE1CB09BF70D63D75E0AF79FE3B0F9737778A9F8CFFA96999FFFDA0008010203013F107A3D5FAF06FC2682E07D63E83E87ABF48368250B4768294462AF1018E56E098DF6FD2D5471D338828D234544A767D57F7ECB62665422959E8AE2BEC9D1EAFD62C6546118AD0B2CACF89707D47DFC450B63F17C1FE40815BDB11439E72B9A9A65F58C79BBCD60573F643EE3A2F8C9E9DBDA171FD3FD8DD630C176F55F111BA0CFFBED167F9BFE422ABE831F3FF91BC86056BEEDCBFA9503889C98560B22362D728367A4AD73CA7EC9D58CAFB06310451B03719FA131FA3B115AEC4112CFD0575DC080BA8D205952ED0E8EA509D8ABFDDC5A76FA2A57D0744FB4E9638657432408AE7F89DA44127447317878F319EBB44A2F89BFB2C7E9AE846B32C95C71E78940B939F484B9BE22E62B9BEB64CF5E74C17D217C7897B8E23FC1077A854CF4A95F5BD2BA546B022DCC13BBFF00231CE0C4064C1A8E609C1A8D0A731EE9503DA63ECFF336A898A972DA7EAAEB52A574502E265DE704B03D8AF7FF00B1ECCC13C4BEF191745A951D8C147A3FB95661512E6FB64AEB5F5D4A8A732ECA52A603DE110BCB69E6561192D2B534956D0EF8D73440DFE600A8B3156704C97EE2076732CEF51F65C2D8A38967A434254A1855D1542C4C15C21137982F28AD47EF3EA4EB52A2E26E523E25881A89ACCF5C33B868B1FDEE352B2D0EF288144A23845CE63015F88AEE9ED15A1763A0E324CA73D6A575C75CDE79FDF6991531E970CF3BF1A94A6519B846A5A89C23389296CA4A46FB4F9FF7ED31BCBE9FFFDA0008010303013F10FA197F5E60A1DDC7F3165FF27F902BB0EE665FD37F41D2FA3F64D95ACB925F77F912371C6598489AD6F1AF8885643A7ADF421F4B2E2F47EA40595A3D39F9FEA11552B7B7E208ACF48884E6255FC41FA4EA3D57EC3002F1FDCC216E34BE185CB5765FC7D6187D0FD860F82562185184805593DDEFA4743A1D17EC960778A530EBB7AFB5EA3A3AFCC035C570E61C57580D79BF4ED0C4DFCD42A90A604522305F3DEFBC01002AC35759EA7D0757ECB10B75D3BA6A9E4ABF339093D1FF00221434370B9AD8DCC7A7CFFA97580BE6EFE2B1F99E4513DB51835AB6FABF59F43F654B058AED7AA7FD95045D1C7410E13135A4A40014B3683821D6BA9D0FB7505426CCC12BB3E1E484055E7FD85D9C7C07F372A9AF776C5ACE98F58E0D873D6BEA3EE3D4897B6DF69965D05B2CF6E676712A7DD9FA8EA7DC5D58ADC558FDAA943241115DA120D8CDE7C332937D2BEB63F505C5F38211C1AEF06906A04A751DC21F1124519C1DE358304B91070732DFEAC4AC75AFA1FAEE1997A0AB7484AEB3D17319772D00C0471B3F7DE54E619AECFCFDBA951101B61D3804BB240A07882738443CF477040B8212A36C6CDC332EA68F4FD752BE8B53E3F98B60A3BBA84DE5E90BDD768CB6C2126DD1799D8C65F485798CAA6187102B86566932FD152BAD4AEB9579E3FB80AF089B8306B3311EB1CD865C5CF40B63AFB38F99532AA5999185D3C71380836F7A574A81F5545574BE2195DBF75286C8B2827E6636C88E50B6F0F45332D867282D28E42FD7511E68F48F9ACB996CDE18F571C4AFB35B6AFC6FDFCCC79EACC99FE7FF0026FBAF46A5E18764E597A66D32789A5BAB75E931373D0FCCC5FB3F133ED7E65ABDBF77F4FF00FFD9')) where id between 2 and 30;
update Material set picture = (unhex('89504E470D0A1A0A0000000D494844520000004800000048080600000055EDB3470000000467414D410000B18F0BFC6105000000206348524D00007A26000080840000FA00000080E8000075300000EA6000003A98000017709CBA513C00000006624B474400FF00FF00FFA0BDA7930000000774494D4507DF071D091500078B8A93000007D44944415478DAEDDC7B8C156719C7F10FBB4B91AA50C45229ADB5D40045E9C5DA6891AA0D349AD426DE6A624CDB788B1AD46AAA58AF355AC56A138D0D9652ACC17AC3188D62D4C40B049BA2514B2A84D655B108A80BD92D97C2020BBBFEF1CCE99C3D3BE7B667CE9E3D4B7FC924EC9C77E67DE63BCFFBBCB767E0693DAD4634A9D5068CD0AAEEC2BFBAF01C9C8BF3700E66604AF2FB000E601FF6602F7A71022C9F378100A550A6E2622CC12BB010B3F16C9C51E6EA9378123D780C5BF07B6CC3E14661B516500AE61CBC1637E06598D9A06D07F1307E8C0DF837864603AA35805230331328EFC465A259E5A94174631DBE239A625D1E35F68002CE645C8B15A229E50D260BD423B80B3F417FAD90C60E50EA3567E356BC07678D59FDA1A3F83EEEC0E3A8EA4D63032885B3105F11F1A6A3862B07F184E8A1F662BF08C84378A668A2739263A6DA3DF10FE2253D540D52F301A5705E8E557849952B8644ACD88CDF626BF2F761D1850F15D93E3901752E16E19AE4988BCE2AF5ECC42DF8792548CD0594C2B90A6B85075502F3281E1071E29FA20BAF2DA8465D1D381FD7E1665C5105D45E2CC74FCBD5D33C40299C45F8B6E8A5CA695F02700D76D50CA572BDB370233E800B2A5CB10B6FC7C6AC7A9B0D687602675985925BF049D1A44EE535024EEA9F84CBF17995E3DE2378ABF0E061906A0994A335EE0CDC5601CE20D627866DCC154EFA904362C078B3887F27CA94BE348138BDF487FC3D2875F11B703F9E5506CE3A7C54CC9F729B3B55B0E94C7C021F91CEE78A75528CCBBE5A6C4F733C28BADD1565E0C00FC70C4E7AFFA3F822BE815319A5BA44BCBAA4F864BE8052EFB951F420597A4834BDB1813312D21DF859995217E27DE82A3C4B333CE805A2CD6735DF7DF8B4467BAAC6D487CF88395A96DEA868AC961FA0D47B5E8FF9654A7D139B5A0627AD73BB88350319A566898E639255DDB97BD074F106B2BCE751DC87C116794E29A4F578B04CA9EBF07CF26F6297892E334BDFC5BF5A4766849EC0B7647BD15CB1689713A0B479BD0AD3324AEC11D38756C59DE14A6DF835766494E81473BA8E3C3DE81962CE95A52DF87BABB964E87F0252965E8A5979029A8572EEB151B62B8F076DC2B18CF3E7E1A23C019D2F16C34A55581F1E1FCDABA0D4961DC2934A350D0BF25CEA9C2386F3A51AC4F5586C55F7F8D84519AEA9B2974426E3A23C01CD2A53D10C315B6F47CDCEB3894D6BFC16E34E33F204D4AC896F2BD535111F2A4F0DE609E848AB9FA6093A946790EE152B78A53DD511B1C4D1AFD55BDDF56B739E80F68A01D7D492F347F131B1EE5B6D2B66BC69304F407BC404B014D00C31C2DE6AF9BCC1563F71BDCA3306FD57613B77B8BA70358A27B56DA33C011D5298528CD4ABC5EE67DB29EF6E7E93EC49E93C2C45DB79513E80D289DF1FF18F8C129371938C7DA7F1AEBC3D680F7E59E6B757E20D682B2F6AC6487ABD485329D519F8B058CE6C1BE507286D665B95DF77BA44CCECCF6C172F6A86070DE01E3170CCD2DBF07E74B603A466EDCD4FC2A7F059D92FE1A0D87ABE5FDE490BE56DEA106B56532A94ECC1B1627B9A33370A83CE165B3DD796297500B7E35E1C6F1AA414CE9BC5AE6E694F3A2406B30FE35DE829B6A599D9A5FB13835E28F6BC4B7516568AADEA3BADEAEE417EEBD669F39D26F6DB5788CCFD2CF58A4DCD9ED21FC622C3EC2D222695336E50EC707E410C344F340C29EAEE140914B7E175622C96A57E7C1C77CBD8F56D0C501832050BC454E2B0D825E82B2AD521527EEF149F1494D3019119BF56B8FB71D4E751614F97C885BC497408CFAB70C5B1C4AE95CA34F3D1014ABDE302912971BD68DB274437FF39FCA6E88AC978B7483D9951E5EE7DC293368875A4DDE22D8F8495DA3145BCA02B85B72C13E97F95D48F2F2780FAF3CB724D8D9A8BD5B283F06E913DBAA1E85CA7686E5F92240654D129B15FF598487CD829D2678E8AC03A15CF1531ECE2E498A37C532A56AF48B95BAD4A07511FA0DAE05482447C7AB0522407D4FB8206938368BAA319C7FD550C417EA1862146ED2B7C299CF9A26B5E56E58AE9621DE871E105C5E07E253C6481ECCDC6729A5404A65EB88745C6ED2D22D3BEA6AF7F6AAB64389C3562E259AB768AA4AA6D4F199406D3ABF041BC46E500DE88FAC5F7635F1771B1AE315775408DC129E87611B895402262C912D1EB2C15BD4E1EC38FDE04CC03099843C3EAAF5195078AE943BC488C65AE1EA5B17393871E7AEA4C0AAA5FA4A06C122F61A968BE8B1258536AAC63400C4E77E077C93DB72B646E8C726C551ED0F04F09EE135F028E56FB87C129566AF88055DDDB93875A2DBAEDF9224E5D28BAEDE9D24D816322AEF488CCB5BF25C76ED1D3E5322ACF76E57CE1F4E14DD8342A83535B3AC50B2D74E32785D79CCA0B4696467A506AD0A5A2B76A04CE11D1A53F38EA3BA40F7E2A398E3785444D8052385708CFB9BC817B3F2982F3DD3839AE92A7EA50DAC486C359ABF2E74BF5C0196857381446A2299C2B73807348CCCFDA1E0E493679A2C522E6BCB881FB1D145FD4ACD1C6CDAA588518B458C49C850DDCAB00E75E63B18C3A46EA1081F81E8DC13920169D26141CC2836E55F28D549DEA130BF0EB4C303804A0250D5CDF27BEE05BA7D51FA934491DCA4D01AAABD704874300DA3C8AEBF6897595090D87007417FE52C735FBF0217CCF04874300DA86F78A1CC26A2AC0F98151FE7F3CEDA6C29AEE9FC5AE432548FF116BCCA70D1CE8287AD03FE11D62BDB654BB12383F3A9DE0903D599D2FF278968AB597ADF89A08E6A7151CF83FE2B901522650A2E80000002574455874646174653A63726561746500323031352D30372D32395430393A32313A30302B30323A3030E8BBADC10000002574455874646174653A6D6F6469667900323031352D30372D32395430393A32313A30302B30323A303099E6157D0000001974455874536F6674776172650041646F626520496D616765526561647971C9653C0000000049454E44AE426082')) where id = 1;
update Material set picture = null where id = 7;
update Portfolio set picture = (unhex('FFD8FFE1001845786966000049492A00080000000000000000000000FFEC00114475636B7900010004000000500000FFE1032B687474703A2F2F6E732E61646F62652E636F6D2F7861702F312E302F003C3F787061636B657420626567696E3D22EFBBBF222069643D2257354D304D7043656869487A7265537A4E54637A6B633964223F3E203C783A786D706D65746120786D6C6E733A783D2261646F62653A6E733A6D6574612F2220783A786D70746B3D2241646F626520584D5020436F726520352E332D633031312036362E3134353636312C20323031322F30322F30362D31343A35363A32372020202020202020223E203C7264663A52444620786D6C6E733A7264663D22687474703A2F2F7777772E77332E6F72672F313939392F30322F32322D7264662D73796E7461782D6E7323223E203C7264663A4465736372697074696F6E207264663A61626F75743D222220786D6C6E733A786D703D22687474703A2F2F6E732E61646F62652E636F6D2F7861702F312E302F2220786D6C6E733A786D704D4D3D22687474703A2F2F6E732E61646F62652E636F6D2F7861702F312E302F6D6D2F2220786D6C6E733A73745265663D22687474703A2F2F6E732E61646F62652E636F6D2F7861702F312E302F73547970652F5265736F75726365526566232220786D703A43726561746F72546F6F6C3D2241646F62652050686F746F73686F7020435336202857696E646F7773292220786D704D4D3A496E7374616E636549443D22786D702E6969643A43383635353242344646383831314534394546384645443039353333324444432220786D704D4D3A446F63756D656E7449443D22786D702E6469643A4338363535324235464638383131453439454638464544303935333332444443223E203C786D704D4D3A4465726976656446726F6D2073745265663A696E7374616E636549443D22786D702E6969643A4338363535324232464638383131453439454638464544303935333332444443222073745265663A646F63756D656E7449443D22786D702E6469643A4338363535324233464638383131453439454638464544303935333332444443222F3E203C2F7264663A4465736372697074696F6E3E203C2F7264663A5244463E203C2F783A786D706D6574613E203C3F787061636B657420656E643D2272223F3EFFEE002641646F62650064C0000000010300150403060A0D00000D400000139700001BCB00002901FFDB0084000202020202020202020203020202030403020203040504040404040506050505050505060607070807070609090A0A09090C0C0C0C0C0C0C0C0C0C0C0C0C0C0C01030303050405090606090D0B090B0D0F0E0E0E0E0F0F0C0C0C0C0C0F0F0C0C0C0C0C0C0F0C0C0C0C0C0C0C0C0C0C0C0C0C0C0C0C0C0C0C0C0C0C0C0C0C0C0C0CFFC2001108008C00D203011100021101031101FFC400DB00000202030101000000000000000000000203060701040508000101010003010100000000000000000000000102030405061000010402010206020301010000000000010002030411051020123040213113062214321516412511000102040304060608050500000000000102030011120421311341516122719132231405304081A1425210B1C1D16282330672A243341520B2C2832412000104030101000000000000000000001140600121001020701213010002020103030305010100000000000100112131411051617181912030A140F0B1C1D1F1E1FFDA000C03010002110311000001D2D5CCF47C8F87A36470C82423E306DEBDF1B749E7A3AD7986DC1816A0A9156A0D7B516A6B6262F8723647A361C32090C296C6F3FD7B0F1DB424EDAABA36487679F2CD9E469ECD21400AAC4DA95D7A432553D8BE4748C1A8D8623A0C24D2E5F6265C5EADB2E5E1CD91D6716DA83EDD7DFEEF9A1A05016A9545A8A42AAD7CC3621A8E46C310838243231CDF457A79DD539DBCC0B15D7BABBDD697EA5ABD3F280B8156A855A9542EBDABA6CC5E8F91D0E43330410495FE9FA2BD3CEEC954D7948F5CEA9E8B0EE8D1676FF985AAD574B558BB52A95551B174351D23023327D4506B48E3F41D1C7A633B356F6BC9DB24BB9BA6AFE9E3BF2791D9BC60A02ED58AB52AA1569B17C861A1C1993299191586BFA1B238FD1D5BAE79AB18767BF879C84F4E9B7F6FCA0A002A14A54D2956A16831748F19218411932998E461E9F7BCFF00A3DE6B8F6CBB38A19BB393F47CCF5F3F38541574002AE94AA55D023E470E90CC983E3210245397EBA45AB3D2CF7EF6BE789EEC67BD3F222985F81A5AAC5D2D556AC062E1B0C430E3E08C183372E34F53ADC5EB4FF97671376ACF6F9F9BE54733D393E51040A001576AC459B12306C861993000269E7BEEEC7AFCC3D79696ADD2BBA3D3BC560996BADF3E573509F1F02A00502E17532C5F23A1D219F180566D8EDDB76D159EED5ECD3EA7F3736EC7983B30B3F8F6C3E6CDFB7D69A792AED9CF4E6CE7C1F027CA0A35AB7160D91B0C4F8C16F69EDBA31DDE4FDDD160E1AA62C6C0C5B794E59AB8654FECCA9BCB7FB3B472A4A9B2D34C6FE43300D0281AF635181C8411F2FA2B8FD439978FBB76FB738F4F42CDDB8F4F66289518D844CFC73D3BFDD5CBCDB994E561682DFC908DBA7E30052CD7B1B06867C1961E9E8B6397D088E8EAAA3A32F4DB8BA196B3CA75F6418D79633AB7F9BB5F6DED971CD36F3D0EE9AEB77168EDE32302D4455868C824C83531E7EF90795EF6B4D912E84DFA7CEB9DC9DDB86F6CC465E763956DA7AA0BC5E9BAE1ABDFC26C797BFCD816EE6C985002D4B1706861139D3D1C6E6F7E49C5D1C7D98D7FD98FA5B5F9B2D6BC24DB7EBE563971F5E54BDEB87F37A3DFD4DCECF3EF4BC47963E4CEAE3E46787CA008AB1887066539BA7DBABA75F425B9B936E3A797D05A3965B75A0EBE78F3265CEC6D53974F8DFB2F1A677B71F55EBA74E96FD103E9F363DBB9720027FFDA000801010001050208208703AC413152DE8A17457AACC7C028A3D010E47435A5EED4C357BA7AF13E2DAEA2B8749AD95A75C2CF7C9198FA8A3C1E0703C03999FADD74D028DAFC58A523DD2EAFE451D075595F7CBEFF0049E0A3C0E078103FFF0046A1F4611873C2B5306075B799AC63F7BACA3C04381D7F211B1A6252D12B9A9D2F72B0220AF5BAF1CF27E5B1E0F49F0F3C36CFF69B8B3B77C517F71BC82A4BB6FB1DEB514DB9A96E78A6BB5F5BB0B72C1AE92C4B4FA4F07C4B153FAEDA8BBA92D96266CD7D835DF2C62FC721DADA9A7AB435EDCE001E1043C0BCCF92BEB767057AAE7DDBB5457DFC8EB36CEBA5DA5B75A92AD71133C03C0410F00FA8A4C6C16AF69652E7EA5EE8F5DA4A75D597C5F31F45959EB3C0E8CF4E537F23B16C6D8359B5648856A78BF276C3A083512ECB6D58D5B8B2B2B2B3D410EACACF0FF8DCE9755F5B9F5B69F06BA7D6B6DDFB5A7A31497E9EA296BED7DEB61535AC64AD9273969CACF58410E8CAF5275FA2BD764FB4415751429D49B65BAA32D9B1661FAFE8EED593E8FA6743BBD25BA773512ECE09BEEB42F0D55393E1FB54F5EA5C65EFAB611C83959EB1C678CAFAFD40D83BFB97DA2EB5FBDFA0EAFF005B431FD6758D9DA18C6F692CBF563BB5E0A51C2BEFF0FCDF53A52776ECC9EAE1F86FB562559E73C65656565656786873DEE83F5F5B5366D8DB69CEDB4C2DC3106BE67ACA6387648725CEED1BD805ED3E9ED00E89D1B94DE89C3B96E75ECA6FCACF195959E32B2B2BB97D76AFCD61D6A9BD5B115BABAFD34B1EE2898840E910782A3C764BEDD80C77E786B57ABF5B8AB2D3CBDEACBBF3DDFD95D50CF766B7C656567C08AADCABAFA2E7FEA4F761846C27ED66837975D043B163D1646E4C68ED914CFED837AE2EA11D99256C7FB70C9637F62EC14B471966D74A69C7959E7281595959595A8A352566F771FBF2086C4D5C5CDE57330A4F76A6AC10D471B3188AE76A6B8765AB0230F9FF000DC6E58E8A9EC3F620FDAD731682B54B8D8AB76A9E28DF1DFA86959CACACACA1C7AAF5E2DFEF66EFEDFC743FB7CCBFEA3E5D663F768FC09B9527C09F9ECBF8EE93F8EF3F53E1FB0FB6BFBFB349FB1F057FF42B71FE995EFDBC747FFFDA0008010200010502F10BC2EE59F2E4A2FCA2839772EF4D767CAC851E31CB7D3CABFA421E55DD2D087953EC188B020D6845A31EC8808795F559C26B96134227CA909CDF5F65F8A0329A30B1E59C83977273937CBF6E53D98594D6928B0E1BE542C958EE458D09C7D3B934793CA0B384E0BD420537053F098BFE2CF8EEE0272EE3D2CF7FF9C03E31595ECB3D594EE0F0D3E2391385F2232820741E0A33269CA280CA68F11C53BDF0981630B3C9E0F19E329AEF0DC79ED09B9E73C13C008B70B078C700E7C2282385F823EDD07808229DEE826F4FFFDA0008010300010502F1046E2BF59C9D139BE5C0CA1176A081E248014F616F95ACD44F00ACACA91996F9483F89E8213BDBCA41FC79CF127F1F291B308E1ABBB20673DFEBDAA2CB958003FC9C2EC858E482B3D8DCE7CA5790357BF1943896727CB31FDC02C229E703CA81950FE087AAC22404E7E53C7AF946700E17C8EE4B728B08F241A9AC08F1959E07AF05108B3C78C70D47A823CB9BE30F458E31D43A5EDF123083495F12EC4E041E9637B8F627B71C06A94F890858E3384F1958E98FDD1195D9845C9EDF0E36772637B78C84538F18E070C6F3272463C2AFECE4DCA194728F40E1AA44D43DBF05F8A97A7FFFDA0008010202063F02F2C0EF2D5963DADA45FFDA0008010302063F02601570AE158D18CBC03579490AB13D7CA63E581A35D7FFDA0008010101063F02F493D15F5452A4AA63A2294B9251D8AC3D5C2466728C0871FDAB94E5FC31A7338E72821B666ADF5082293D509B6573CFB13CC7AB2EDD0BA084CD67EC84B897E67E59CA245B49DE6A82A01091D31224278C38E9E7D31CA61293D970D0A1EAB713CAB09F7427E9328A762A60886E5812EA65D7EAB703F1823AA12A093D312544A71DEDC36DFF001AC27EB84E8DCB4A54F0A569316F3C94B4AA5EAB7A2B76D99B34A5250D1A54B56D99CE14DDA8BB9349A9443CE92065324AA17E616F76FE9A2653A8AD66D52E9845BF98A556614C6ADB5AB15212EEF24CE6605B37E5C2B22A2AA36113CE50B1E636094028DA8116B74F77AFB09525B391294E00C32E5CFEB2F157AA7F90651362EC245DA46242C64A96E3147896D970F69058528F54A1AB169A74D8EA2177B76E35A282941A836DA4804D47DD1E5F796E948BDB0555680E4B1F1367828473796799B6BF8DB448A7AC2A1C6EDD02CE69299BAA9AC4FF0008FBE1992795B4A5A4744A00180197AA2E427213CF7477690990D90A798783770B9D21626024C36976EA80C7F56582BAE1A530F4CAB95E6C9ED711030E2610AAD4669068E27D548391C0C693DD99E23ED8F1165E66F30D9ED5B93347B36C4957D5090C54F38AC76F2E10AB950F13707FAEBD94EEDD1CF86BB8100244C9E00089652D9EAA13504CF6984B85D4B6FA15CA553911D309B7BAE4A8481D87A0ED89E1D70D8B74C98C839903D10BB6EF5FF340D2941E5A6969291298684E7ED30B4FC0BE647AAB4953B473856189E5C603EFBEBD07D3DDACAB981DC1236830FDBDB3755AABE073256E5F0308B2F1CED0E0548296001213C49869179E62B21D5D0BDC93C3DB163716ABAD6DD6871C56652B49C3D862C16E82B76E1EA1084672233302D51CD70A04A1897310924190F6452A052A198387A80004C9C84778DAAD591DA716247D822D6C6C8537BE6CEE92AE8F33886522A7943761845D0B5452C791DAA8848ECEB286093F57B2021F75CF0E553B94A30206E1BA12E5B361F4AF9415E69DF39ED873C2B458765DCF312906052C2D971B489BAD72CCFBA716CFF98ADFBA7FC3975861692273C045BF9ADEBFA8EBD7ADEA3647626954824EC1C23F6E3B9D6AB94E133DA04ECE9895D5A850DE46220BB60F8A653D073EA0A820E046041F4C2FE905D5BD4364EC48C0C70DF178F2BF43C8AD12DFFD8EF78BF70023C5DC8FFD3E72B372F93F2ABB03AB1853C5B9A544A8B590513B550942121284E0948C008E534C7857F992BCCED1C44384AB55D71210A748972A4480117987359B8D3A7F2AA5F6C7ED0787C4F9FE661114E7BE129DD329855E5BA7BD02A7123E21F7FA54A1226A59012389845B366450902637EF84A6E8D21382D7F6C5B5B4E4E7EE5F3053EFF060AA7FEC0984DBDB365696C04A129C801801135274E31380DD09812FA3CCADA5FDC5B388971961EF8FDA572E09866E6973D88523FE30247988C38C3637411D51ACD2B91D548B5F29F48BB954A8B6CA7F32A0A12F6AADB32284E7172CA24C97DB5B6974E34D4253872E5D0022D99433E5EA9E147C47A76402D4A9C94AE3F463023082A2626E2E9D434238930DBBE2C9D1BC5DD32C539054F93F9A186D5DB68CA5F8607BE0B565CCB064B737424B8B2BDB8FA472FE7A6DB83B0709A77FDD01684D4EAF196D3011796A594AB35AC484FA6356D6ED45B40AA818A93BCCF6880D5BF949BA6D2665EAA44C017168E59A8FCE30EB898897D121893B20992438C90E209C84B3F744ACD1AB3EDB8701D70B754A4B4F2716E8338F0CC8521C51938F6D96E1153C8ED6F8F12C62C8FD44FCBC7A3D1BD7B7CEA52C5BA800D2B00B3C61B6D2B09B641A5A40C061B61A5585DA12EB60776BC8FB4469DEF953AFA5C9CF4C6A225C48C217E1C2AC9C71DD27ECF6539AB0F8611A2A98DB289B6AD76F6B6ACE24534FE1849DE213BD539409FC2270B60B479F0E68ADF791E5B6CDF2A1A12D4207D422A45CA9E2A1895AF28D56DF0B792A554D7E631CC89C2D0B482958A549E061C6675373EE97C3D133DAF0D41F0FF255F165B6113CAA34CA051E2A8C2746E81E0EAFF1F851E225AD3FCB843FFE6F4BC7567537CFD907C2CE8F8B77D1CF9C33F272CA2DE7F8B3F646128EF655F08B49766A5D74F6A52C2728C2BCB6CE19F0F56B5474E9CE04B5EAE397BE11A9FDA486B6967C672E6EA867C44A8E6D297BFF00D3FFDA0008010103013F211F403A0CCD7D21212B2E7A8E396B4FE5803858F37B178654A95122448C482082540810741981D04A950E1B6A82814785761C7AC64E915A7E523C2BB0737B5ACA701D9B7E6580AAC9E0ED71CA5BEFE3C3163D5E81120944302081021060C1AE98F62E637A86FBC567E513503B33FC8C502B82CFCC238499D995AD925DDE62A956079D3F31227463047A9441A841020409508747AF0C941A1750DBCE6314143C1BF28957302DF93728F31944631890412BAC7410EA74749FE3046A0108A94A94ABB6A6A2F93F92134DB86D1F14C3B4A679DDAC7A9E8C7A109508420CB972E0F45E2FB45268D624B463E0526A6311FD0A603DBC11A835B79826ED366460EB51C05F663DAEBC45B9150A62ECD6DD464B2D645363584F12B65B8365C996DDBCD4A0DA1A3D71D58C7A17A8842107E8C4B876539EB5AA9945A6B93CCC5CCE78FBF00C0FA9EA00882D516CDC71325C8C29A64E470D30927F784F3CE26A1E0422AC2A5F5838EF49C8416FA7F3009D3A1E08CB8B18C624AE82CB7404A950C4B98971876EE5528CBDF50E73B5695F3190EF0845277E63D8356FE371A402242CB631EB400698B7C4A022E5540665C6318C63162E8BA154BEB72FA6A033217D06572E2EF9C0F5875B393A1CE943E61F270F23E0DBD66675186F071D0C4E7E112BCF74625DCB63E15C756E5C58C63D18E831060F45CBE97518AA9C168BED31284C0C35531110A2E48A1C9607A452D7B55622DE3AB725DD9D8735707A8A72C0B62BBB4ADAE3513C5653F9FCCBFA4162C58B311450874B97D458C49476082B017E26A34459B9033C08641B9F608B6AF0CA6C66A8B1496B1DE2A77350F086B470C4B621095C60C68AF798C6CA28CBBAB167BC4911A12ADF252B52FF004C158F67AD718B1E97154517983D2FA0508CA22D5780832E6CDAF87CBFC41D8E3793AF96AAEF3C790FC5DF43217802C7369971502E91668899B2298C7B4B3CAAED598B1B69811DEA09BBC68EF52FA4D815DAF019CB44508C86D07149883A042D544AC0C9967E205E6EE4A8D311737D97F7F3119AFD3226C7A97D165C21D06213718A8C279441EAF31A0CDFE48F9D851D64F9FDEE266BBDDE43F666284DA8A2F06EB5C7983086841760214B7AEC652DD31DE73B74CB522EE58CC6003F398F4FF0B56FC4825C667A9858BD49477CF97A81D2510D5B7D1F9858EFD2E30BD074083A173CBB9D928986D07B5CBEE66601D1A0397E65C3A2B907D810699637BFC0085D11E72C7BE38394621A4C450E1BB60D9BA8A1B3E616BF1A4679469B2D3E0846936BC5DC8E452E273E2C0241FCBDB8F0FE27ABA2E3D0207E961A5C69B02AC3EC434DA275B7BD152E1858B24C255D5EA54A6E8AD6CCE2DA76F5899A1E423772DD18825613D8AF5EB0566B96E461B833105878440705388539B3DD996955936DE0FB6A15BA06FD12B80F164CE615FC1DAD97D0C317060C13AACB87229317F9BFB6616B1AB602E017B4FE6120A8E2FDE6E4A29444A0706331EAE1018EF785F79A829EEFD164201BBA4A8334C671EF2B47117B45A4646DFF008F2F933CF9E4C3E7FD45B6F6D9D72A54CC15FD79078B799448BEA2DFACA34A2B7E7A1074AE2CB740E913EB8C99CD686F20C876372FF3B947459EB83C440176A6AD66AF89487159D4DE421E3F885C6311EE0CE558310A6902EA91F69401FF002E60D1E795C4CE5804F785026B1F15039191884A545A28A7BFAC6152E4580B0D763BCC9C2A925B9A008E2C47358C097B2A60360D37822DC5AF4852426204F73B1F269843D7DA6611FF0050B9F148EE7F56EF3ED39CFADAAE7FDFE5E7F13CC6FE62E0B9EE767EC62A62EDB0FF00D2607DE9FB03C4E1B5AF2F44BFBCD769E84E394EF5F141E9AEEA60EDFDA2E7C839E7C5667ED8BD7499B176B617FEB47E6CDABBEECEAA66663D3FFFDA0008010203013F21FB80E60585BF4F416C752910846040FD3457018A84BB836FD29E847A0C41FA63D418B5921FD2F24B996A920188AB2C134610A269FA473145FF00D96C9DFCCAB0E99EA210F79635FA5C97122859974D7570377FA62C3E495ED1583F4F65162F1C82B4D3F4BBC0998AD84E10342672C7F46C6799AEE054C9B8B184AB0476CE707F405D0E3D664C28AE9589692A287697D2BC7DE5708EEEDD36F43517A146FA075598FB8F886330A3AE90625CB970E8AC4744B7886BAE53F709C47705F52D6925269D03A171322256E147103BC5CB30FDBAE65960C5BA611B8E7A103D2B964B9DE78A7887454FB5594B9DFE8F63F43D77BFA2B3C4F395E3E9FFDA0008010303013F21FB9C6C3B84DE1FA7454431E7A2EE9963136BFA530BC06302631BC063F4A3A46040E83A7E90FD2A654A94228C74BD21FA3AB975BB895ADE85BA914910CF40884FD23F057D7BCC26A7654AB85F6FE2549557B114ADFD25C734A8C0CA743DA06F0FD2EA1568C5CCB778C1C454ADC4473FA5B151F288D65A6F63A9B9FA539BE89A4B31733371CCE0BF44EF499F4BB19618B2E88312C9D9FBE357D348B316FA9166D35EB767EE85C3A46789E728EA74DA259F4519FB992FA60BBCBCAC32BA30E8F41D2D04E7615D3EE3DCC3103B314213B58F6F521319AEF0854A2E95193EE031E65CB311D18664988F4095286E1D123E22F4BDF6DB5747D19F34F2E99EA25E5A897A94DA3DF5096FF4FF00FFDA000C0301000211031100001046E0EA6D204394C1E766D575637559566C6CF51401FA0BD763251A208CAB9D20085D9C6FCF36089B0E1E3124B4CEB9FA2396B95BD9EF56C9532F70C26E197A96BB91D887D157F1BCFE085866399F50E3BB5E337682213A0D2215C3F2897CF65895964257AAC384DA697012E8A2BD9F50EF5FCA10C4DF3220EB033A368FBB83D889597B3E71F106927FCA0042022EEEB0D4AEC8D041E696EC0B6C52D02AA09D6036BCF09062766FA6A1545905A7B73E999602C25E440AFF00FFDA0008010103013F10A2B8CC154FE26BA80C57BCE3C9C455B3507B2014BF88076818F4D4466A8DAB8F37C4264950C7DCE311965288D5DF4CC2460A24E4C8F46287C98629B0299871B8147CC1AA3F33578ED0AF118D6A527F72EBE789571ED3D135C3C240AAD4C12A0299AF30C7F89E1861BCB9660506A283CCC03C85A3CAAE00E6263C47A3990D793CF68E1B4DB3A77901EF16467946154D837E08EDCC25C076B20FC44E2E9755326F90E2F51F99161D2C690EF889A4D732D57598DBC44FF481CE3307A5CBB1A25F6D54DFCF6E8595C4A83F101E35B997055C1683077850CCFCD02A870C0F22149E60AD5A7A320782A965963CB8E9781CFAC3E3932CF968167D62C004B95F1907C4AD5EA42C4E6ED94525238B316F155154BB65B4BEA8BE228F9E27E1E2278F9819F29B35E266B47A4C31B82EC7D27F2540AF0DC238E60EDD9A898BCF9947105F30A792584A8A0FBCBE885835655AF4C4AE9A062EA0EA3E732AE80B960541A959ECA68969E531E65A53E3C27F089BD8E582F1C6A346F337951E9A6EF3E23DAE2265E904DFB1DE5FFC9A578C3162937CCA1B966330479C4DBD20EB3EF0996813B24BF7B8BC20A0A6BBCCA06B4C4242E832E7C4B3C2AD1BDC8804B04426283DBE929C4512D3A8C629DCC57D711AD8CB3C55CD72C4662F749E5ED0390CCBD9F695EDCC20E36CAB8A852B33340B8594ADD42FAACCA33BED0EEDCC57016FB433FE65FABE06BB85B98DE2874A44A2C0A82DCD691966D80816C952EB08B052AB4DEABCC1FAB89605518382DDEE26F0D59D00C3BC284604ECB67C2C6D2BDBBA0B0028500AA7B188D7BC6A0ACB2CFAC3BB890E9657F8653DE13980758F3177C7888F79E0CC1B65358E399E7BC39730B15F8964CD7871EE42ACD129491DB04BA82B472ADE205989FC6C09A6BB22097D710D795B690035D0D243DFC767F829B3255F620315C65F17811C525E5BD43C4E63118662A8BE5418A01CC18026D71CA077F665423D9A276C448E0DF3995FE4F4973456A59AB89003981834E1801A8ABFC813FA8A2763140D4E35A6EE32279E2399585AAF7605F312048D8ECBABC4437448993802C3786EF9A80A0D9B1605E2FB0DC2B0955E93CA15B0368CAAC05ABC0EA37C711116EB30F6CC4BE088F8E6537B8D99812B5CCB9DCF98B51143BA0B5065B39C4F080BA656CFC40C2947811FC315DE36D82EF581F865BA8586D644D2B14BD25F3FF4B0372B3985352B701C4305C28B603872DAAC096D5F53384A25E8C198CDF99787461C54AEAE03444B7E23E536458B8867AF5012FBA661A89A57131D5CB4ABF460CDCBAFF30CBF896263E21DDA9CE37E205CD71A880A60396A8819D262E234514C2E061B83E94EB4F11B623516F0B3ECC4422860C1832CBB0F96233490F5E04C54185440D0941C66C03EA0FCC0CA778F97CC6CF68E105F3DE03CF199F9F50ABB74796E779C47E6775C29F5FEE04C2E7994FF00D8D351B6D83816ADA3CDC010BABDE5E2EEC2F10887BA8E013602ACBDB519BD1A28B756667375A53A12D901D29C95ED6B1E639BD1B05873A54154E662E11A09180058070DA664F1ABC198080672BAD3005DBAB0B0A6FB2A32E1891888BABB2024CB9DF10F28D8CB16F3EF01975C7316F1BAE657B4F557896377ED28ABC21BA6C99AB1C4B9EB1C6172B36C3005AAF01002999486C717CA03BF1017D58434E630212AF590980D4DAF4AB8017ACD5EF2C456300164505AA1A2CE29616860AA4B8843C96DAE60D95C14D81E88E7366C8A5D423515401AB07E22E56F4DB20D4B3E0BAB71294B745428A42C966911BB7A887AD8F2DC97073195B1C8EC838C5D9BEF02C6178B02D2377540610BF4257501C8894C3CA2DC6E29CC5A3364C17CF7E877E6A26C659BB6E2BCEE6A5777B825E70C1E06E45616BA5DBEC259EC26AF1C1ED099EF121107753E98E6A87B364B19096FAF56D6815AD1544D1730C8843A4C0801E8400F75714DE4B335086BF3000C353BDFCE1A9E7D8D851DB9A369E51830081C7A74B3082027EEBCB1974A0F93C4414AB098B2CAFDB53B67399F51C25DF0C6C26A36C92FBEE2755EF2C5B9B567A3CCAAA8AF10B718E6205CB32929DF10B9835C450C42EC00F96023621CB813EAB2913015096276C5BB7BCE1D6E7755F017A30DED938858380131132CB67F78AB6540055BBAF04B61D03BDB29D005DAC18BC471983477651B21EF95F50440E6D32F447D43E931C44AEC72C6F484C2ACE43BD4A4E9BAEE4DC716180142D66EA68FE15071679F48F9DCAAFF0032BE7BC4F99FF585609D63C411E9DE1C3BCA1F29908DBBAD7D52572EBF55BBC3D45D4AFC513AEF44AC5BDE00B605829ACED1050BD4609102938C9BBCC4B28E1C4384D906FF00B98C11A21C9DB442D8ED3154D6EAA2427752B157DC95E701B282857708A6A0C80B0AC80940CF1095681DDBFD842D2B5ED00A81C5590368BE5F1F31D3C0A796C5AA857D5861B806062B7FCCF5CB0C3EB36D7B919598F09752DACF1A949E3BC703B2E4EB12E934E4BE045F76C5A31502C7766E685636CB6CA79A32C9171F7B0564B0165DAA114BFCBA6D25D1FE262B69160EC984B2EB0A12F0F9940BDC765302232CE6EFB42D629346732DCCAED384E75711932A0B67BF029DAF51C29D4E9814CA26129B8CDBA583021065B3B35011F1C1BFB5B65BE65113076ACA46F269BD7A689ACDB1BEB98FFDCC38733F65C476E3B4039B616B489CB8944C4260B1ADB441057326B5B6B5980ABB2536EC181EB06BD017B502BBAB4422300CA163363436C50D7640A345EC10D086809586E2541DD45F4461941F9FD120007C01AF12276476F02F7EF2A582D01E66BDE05D3629D9DE0A9792A2009129C0C43FC0C202168E36A519AA83C2AA3079351EA3C4BA01C0A53241A6103403B1A94645ED5543C8C66EC0BDFB357FF27307BC3E1CC70CCFEF3BBED0EDF79D89E5A8CDE27E0E3FF68F05A8F98A1B9773C7DDEBC7AEFAB8FEB3DEFC66AF35E67CE54D07A3B5799F2A0E55C4D55F0D799E9A3854DD71C155554F7AED9ED3C7DF72F5BE67B6A995466FB1D94A7BDD7FF7647F13C0C5E8D67927F2847BDFCABFCCC7CE1CB09BF70D63D75E0AF79FE3B0F9737778A9F8CFFA96999FFFDA0008010203013F107A3D5FAF06FC2682E07D63E83E87ABF48368250B4768294462AF1018E56E098DF6FD2D5471D338828D234544A767D57F7ECB62665422959E8AE2BEC9D1EAFD62C6546118AD0B2CACF89707D47DFC450B63F17C1FE40815BDB11439E72B9A9A65F58C79BBCD60573F643EE3A2F8C9E9DBDA171FD3FD8DD630C176F55F111BA0CFFBED167F9BFE422ABE831F3FF91BC86056BEEDCBFA9503889C98560B22362D728367A4AD73CA7EC9D58CAFB06310451B03719FA131FA3B115AEC4112CFD0575DC080BA8D205952ED0E8EA509D8ABFDDC5A76FA2A57D0744FB4E9638657432408AE7F89DA44127447317878F319EBB44A2F89BFB2C7E9AE846B32C95C71E78940B939F484B9BE22E62B9BEB64CF5E74C17D217C7897B8E23FC1077A854CF4A95F5BD2BA546B022DCC13BBFF00231CE0C4064C1A8E609C1A8D0A731EE9503DA63ECFF336A898A972DA7EAAEB52A574502E265DE704B03D8AF7FF00B1ECCC13C4BEF191745A951D8C147A3FB95661512E6FB64AEB5F5D4A8A732ECA52A603DE110BCB69E6561192D2B534956D0EF8D73440DFE600A8B3156704C97EE2076732CEF51F65C2D8A38967A434254A1855D1542C4C15C21137982F28AD47EF3EA4EB52A2E26E523E25881A89ACCF5C33B868B1FDEE352B2D0EF288144A23845CE63015F88AEE9ED15A1763A0E324CA73D6A575C75CDE79FDF6991531E970CF3BF1A94A6519B846A5A89C23389296CA4A46FB4F9FF7ED31BCBE9FFFDA0008010303013F10FA197F5E60A1DDC7F3165FF27F902BB0EE665FD37F41D2FA3F64D95ACB925F77F912371C6598489AD6F1AF8885643A7ADF421F4B2E2F47EA40595A3D39F9FEA11552B7B7E208ACF48884E6255FC41FA4EA3D57EC3002F1FDCC216E34BE185CB5765FC7D6187D0FD860F82562185184805593DDEFA4743A1D17EC960778A530EBB7AFB5EA3A3AFCC035C570E61C57580D79BF4ED0C4DFCD42A90A604522305F3DEFBC01002AC35759EA7D0757ECB10B75D3BA6A9E4ABF339093D1FF00221434370B9AD8DCC7A7CFFA97580BE6EFE2B1F99E4513DB51835AB6FABF59F43F654B058AED7AA7FD95045D1C7410E13135A4A40014B3683821D6BA9D0FB7505426CCC12BB3E1E484055E7FD85D9C7C07F372A9AF776C5ACE98F58E0D873D6BEA3EE3D4897B6DF69965D05B2CF6E676712A7DD9FA8EA7DC5D58ADC558FDAA943241115DA120D8CDE7C332937D2BEB63F505C5F38211C1AEF06906A04A751DC21F1124519C1DE358304B91070732DFEAC4AC75AFA1FAEE1997A0AB7484AEB3D17319772D00C0471B3F7DE54E619AECFCFDBA951101B61D3804BB240A07882738443CF477040B8212A36C6CDC332EA68F4FD752BE8B53E3F98B60A3BBA84DE5E90BDD768CB6C2126DD1799D8C65F485798CAA6187102B86566932FD152BAD4AEB9579E3FB80AF089B8306B3311EB1CD865C5CF40B63AFB38F99532AA5999185D3C71380836F7A574A81F5545574BE2195DBF75286C8B2827E6636C88E50B6F0F45332D867282D28E42FD7511E68F48F9ACB996CDE18F571C4AFB35B6AFC6FDFCCC79EACC99FE7FF0026FBAF46A5E18764E597A66D32789A5BAB75E931373D0FCCC5FB3F133ED7E65ABDBF77F4FF00FFD9')) where id between 1 and 5;

-- Chapter

insert into Chapter(id, title, portfolio, textValue, parentChapter, chapterOrder) values (1, 'The crisis', 1, 'line 1\nline2', null, 0);
insert into Chapter(id, title, portfolio, textValue, parentChapter, chapterOrder) values (2, 'The EU response', 1, 'This is some text that explains what is this Chapter about.\nIt can have many lines\n\n\nAnd can also have    spaces   betwenn    the words on it', null, 1);
insert into Chapter(id, title, portfolio, textValue, parentChapter, chapterOrder) values (3, 'Subprime', null, 'The crises of subprime bla bla bla', 1, 0);
insert into Chapter(id, title, portfolio, textValue, parentChapter, chapterOrder) values (4, 'The big crash', null, 'Bla bla bla Bla bla bla Bla bla bla\nBla bla bla Bla bla blaBla bla blaBla bla blaBla bla bla\nBla bla blaBla bla bla', 1, 0);

-- Chapter-Material

insert into Chapter_Material(chapter, material, materialOrder) values(1, 1, 0);
insert into Chapter_Material(chapter, material, materialOrder) values(1, 2, 1);
insert into Chapter_Material(chapter, material, materialOrder) values(1, 3, 2);
insert into Chapter_Material(chapter, material, materialOrder) values(1, 4, 3);
insert into Chapter_Material(chapter, material, materialOrder) values(1, 5, 4);
insert into Chapter_Material(chapter, material, materialOrder) values(1, 6, 5);
insert into Chapter_Material(chapter, material, materialOrder) values(1, 7, 6);
insert into Chapter_Material(chapter, material, materialOrder) values(1, 8, 7);
insert into Chapter_Material(chapter, material, materialOrder) values(1, 9, 8);
insert into Chapter_Material(chapter, material, materialOrder) values(1, 10, 9);
insert into Chapter_Material(chapter, material, materialOrder) values(1, 11, 10);
insert into Chapter_Material(chapter, material, materialOrder) values(1, 12, 11);
insert into Chapter_Material(chapter, material, materialOrder) values(4, 5, 0);
insert into Chapter_Material(chapter, material, materialOrder) values(4, 1, 1);
insert into Chapter_Material(chapter, material, materialOrder) values(4, 8, 2);

-- Portfolio-Tags

insert into Portfolio_Tag(tag, portfolio) values(1, 1);
insert into Portfolio_Tag(tag, portfolio) values(1, 2);
insert into Portfolio_Tag(tag, portfolio) values(2, 1);
insert into Portfolio_Tag(tag, portfolio) values(3, 1);
insert into Portfolio_Tag(tag, portfolio) values(4, 1);
insert into Portfolio_Tag(tag, portfolio) values(4, 2);
insert into Portfolio_Tag(tag, portfolio) values(5, 1);
insert into Portfolio_Tag(tag, portfolio) values(2, 7);
insert into Portfolio_Tag(tag, portfolio) values(12, 7);

-- Portfolio TargetGroups
insert into Portfolio_TargetGroup(portfolio, targetGroup) values (1, 'ZERO_FIVE');
insert into Portfolio_TargetGroup(portfolio, targetGroup) values (1, 'GRADE1');
insert into Portfolio_TargetGroup(portfolio, targetGroup) values (8, 'ZERO_FIVE');
insert into Portfolio_TargetGroup(portfolio, targetGroup) values (8, 'SIX_SEVEN');
