-- IssueDate

insert into IssueDate(id, day, month, year) values(1, 2, 2, 1983);
insert into IssueDate(id, day, month, year) values(2, 27, 1, -983);
insert into IssueDate(id, year) values(3, -1500);
insert into IssueDate(id, day, month, year) values(4, 31, 3, 1923);
insert into IssueDate(id, day, month, year) values(5, 9, 12, 1978);
insert into IssueDate(id, day, month, year) values(6, 27, 1, 1986);
insert into IssueDate(id, month, year) values(7, 3, 1991);

-- LanguageTable

insert into LanguageTable(id, name, code) values (1, 'Estonian', 'est');
insert into LanguageTable(id, name, code) values (2, 'Russian', 'rus');
insert into LanguageTable(id, name, code) values (3, 'English', 'eng');
insert into LanguageTable(id, name, code) values (4, 'Arabic', 'ara');
insert into LanguageTable(id, name, code) values (5, 'Portuguese', 'por');
insert into LanguageTable(id, name, code) values (6, 'French', 'fre');

-- License Type

insert into LicenseType(id, name) values (1, 'CCBY');
insert into LicenseType(id, name) values (2, 'CCBYSA');
insert into LicenseType(id, name) values (3, 'CCBYND');

-- Repository. Do not use real URLs here

insert into Repository(id, baseURL, lastSynchronization, schemaName, isEstonianPublisher) values (1, 'http://repo1.ee', null, 'waramu', false);
insert into Repository(id, baseURL, lastSynchronization, schemaName, isEstonianPublisher) values (2, 'http://estonianPublisher.ee/OAIHandler', null, 'estCore', true);

-- User

insert into User(id, userName, name, surName, idCode, role) values (1, 'mati.maasikas', 'Mati', 'Maasikas', '39011220011', 'USER');
insert into User(id, userName, name, surName, idCode, role) values (2, 'peeter.paan', 'Peeter', 'Paan', '38011550077', 'USER');
insert into User(id, userName, name, surName, idCode, role) values (3, 'voldemar.vapustav', 'Voldemar', 'Vapustav', '37066990099', 'USER');
insert into User(id, userName, name, surName, idCode, role) values (4, 'voldemar.vapustav2', 'Voldemar', 'Vapustav', '15066990099', 'USER');
insert into User(id, userName, name, surName, idCode, role) values (5, 'mati.maasikas2', 'Mäti', 'Maasikas', '39011220012', 'USER');
insert into User(id, userName, name, surName, idCode, role) values (6, 'mati.maasikas-vaarikas', 'Mati', 'Maasikas-Vaarikas', '39011220013', 'USER');

-- AuthenticatedUser

insert into AuthenticatedUser(id, user_id, token, firstLogin, homeOrganization, mails, affiliations, scopedAffiliations) values (1, 1, 'token', false, null, null, null, null);

-- AuthenticationState

insert into AuthenticationState(id, token) values (1, 'testTOKEN');
insert into AuthenticationState(id, token) values (2, 'taatAuthenticateTestToken');

-- EducationalContext

insert into Taxon(id, name, level) values (1, 'PRESCHOOLEDUCATION', 'EDUCATIONAL_CONTEXT');
insert into EducationalContext(id) values (1);
insert into Taxon(id, name, level) values (2, 'BASICEDUCATION', 'EDUCATIONAL_CONTEXT');
insert into EducationalContext(id) values (2);
insert into Taxon(id, name, level) values (3, 'SECONDARYEDUCATION', 'EDUCATIONAL_CONTEXT');
insert into EducationalContext(id) values (3);
insert into Taxon(id, name, level) values (4, 'HIGHEREDUCATION', 'EDUCATIONAL_CONTEXT');
insert into EducationalContext(id) values (4);
insert into Taxon(id, name, level) values (5, 'VOCATIONALEDUCATION', 'EDUCATIONAL_CONTEXT');
insert into EducationalContext(id) values (5);
insert into Taxon(id, name, level) values (6, 'CONTINUINGEDUCATION', 'EDUCATIONAL_CONTEXT');
insert into EducationalContext(id) values (6);
insert into Taxon(id, name, level) values (7, 'TEACHEREDUCATION', 'EDUCATIONAL_CONTEXT');
insert into EducationalContext(id) values (7);
insert into Taxon(id, name, level) values (8, 'SPECIALEDUCATION', 'EDUCATIONAL_CONTEXT');
insert into EducationalContext(id) values (8);
insert into Taxon(id, name, level) values (9, 'OTHER', 'EDUCATIONAL_CONTEXT');
insert into EducationalContext(id) values (9);

-- Domain

insert into Taxon(id, name, level) values (10, 'Mathematics', 'DOMAIN');
insert into Domain(id, educationalContext) values (10, 1);
insert into Taxon(id, name, level) values (11, 'ForeignLanguage', 'DOMAIN');
insert into Domain(id, educationalContext) values (11, 1);
insert into Taxon(id, name, level) values (12, 'DomainWithTopics', 'DOMAIN');
insert into Domain(id, educationalContext) values (12, 6);

-- Subject

insert into Taxon(id, name, level) values (20, 'Biology', 'SUBJECT');
insert into Subject(id, domain) values (20, 10);
insert into Taxon(id, name, level) values (21, 'Mathematics', 'SUBJECT');
insert into Subject(id, domain) values (21, 10);

-- Topics from Subjects

insert into Taxon(id, name, level) values (30, 'Algebra', 'TOPIC');
insert into Topic(id, subject) values (30, 21);
insert into Taxon(id, name, level) values (31, 'Trigonometria', 'TOPIC');
insert into Topic(id, subject) values (31, 21);

-- Topics from Domain

insert into Taxon(id, name, level) values (32, 'EstoniaAndTheWould', 'TOPIC');
insert into Topic(id, domain) values (32, 12);
insert into Taxon(id, name, level) values (33, 'VogaisTonicas', 'TOPIC');
insert into Topic(id, subject) values (33, 12);

-- EstCore taxon mapping

insert into EstCoreTaxonMapping(id, taxon, name) values (1, 1, 'preschoolEducation');
insert into EstCoreTaxonMapping(id, taxon, name) values (2, 2, 'basicEducation');
insert into EstCoreTaxonMapping(id, taxon, name) values (3, 3, 'secondaryEducation');
insert into EstCoreTaxonMapping(id, taxon, name) values (4, 4, 'higherEducation');
insert into EstCoreTaxonMapping(id, taxon, name) values (5, 5, 'vocationalEducation');
insert into EstCoreTaxonMapping(id, taxon, name) values (6, 6, 'continuingEducation');
insert into EstCoreTaxonMapping(id, taxon, name) values (7, 7, 'teacherEducation');
insert into EstCoreTaxonMapping(id, taxon, name) values (8, 8, 'specialEducation');
insert into EstCoreTaxonMapping(id, taxon, name) values (9, 9, 'other');
insert into EstCoreTaxonMapping(id, taxon, name) values (10, 10, 'Mathematics');
insert into EstCoreTaxonMapping(id, taxon, name) values (11, 11, 'Foreign language');
insert into EstCoreTaxonMapping(id, taxon, name) values (12, 12, 'DomainWithTopics');
insert into EstCoreTaxonMapping(id, taxon, name) values (20, 20, 'Biology');
insert into EstCoreTaxonMapping(id, taxon, name) values (21, 21, 'Mathematics');
insert into EstCoreTaxonMapping(id, taxon, name) values (30, 30, 'Algebra');
insert into EstCoreTaxonMapping(id, taxon, name) values (31, 31, 'Trigonometria');
insert into EstCoreTaxonMapping(id, taxon, name) values (32, 32, 'EstoniaAndTheWould');
insert into EstCoreTaxonMapping(id, taxon, name) values (33, 33, 'VogaisTonicas');

-- Waramu taxon mapping

insert into WaramuTaxonMapping(id, taxon, name) values (1, 1, 'PRESCHOOLEDUCATION');
insert into WaramuTaxonMapping(id, taxon, name) values (2, 2, 'COMPULSORYEDUCATION');
insert into WaramuTaxonMapping(id, taxon, name) values (3, 3, 'SECONDARYEDUCATION');
insert into WaramuTaxonMapping(id, taxon, name) values (4, 4, 'HIGHEREDUCATION');
insert into WaramuTaxonMapping(id, taxon, name) values (5, 5, 'VOCATIONALEDUCATION');
insert into WaramuTaxonMapping(id, taxon, name) values (6, 6, 'CONTINUINGEDUCATION');
insert into WaramuTaxonMapping(id, taxon, name) values (7, 7, 'TEACHEREDUCATION');
insert into WaramuTaxonMapping(id, taxon, name) values (8, 8, 'SPECIALEDUCATION');
insert into WaramuTaxonMapping(id, taxon, name) values (9, 9, 'OTHER');
insert into WaramuTaxonMapping(id, taxon, name) values (10, 10, 'Mathematics');
insert into WaramuTaxonMapping(id, taxon, name) values (11, 11, 'Foreign language');
insert into WaramuTaxonMapping(id, taxon, name) values (12, 12, 'DomainWithTopics');
insert into WaramuTaxonMapping(id, taxon, name) values (20, 20, 'Biology');
insert into WaramuTaxonMapping(id, taxon, name) values (21, 21, 'Mathematics');
insert into WaramuTaxonMapping(id, taxon, name) values (30, 30, 'Algebra');
insert into WaramuTaxonMapping(id, taxon, name) values (31, 31, 'Trigonometria');
insert into WaramuTaxonMapping(id, taxon, name) values (32, 32, 'EstoniaAndTheWould');
insert into WaramuTaxonMapping(id, taxon, name) values (33, 33, 'VogaisTonicas');

-- Materials

insert into Material(id, lang, issueDate, licenseType, source, repository, repositoryIdentifier, added, updated, views, picture, creator, deleted, paid, isSpecialEducation) values(1, 1, 1, 1, 'https://www.youtube.com/watch?v=gSWbx3CvVUk', 1, 'isssiiaawej', '1999-01-01 00:00:01', '2000-03-01 07:00:01', 100, '656b6f6f6c696b6f7474', 1, false, true, true);
insert into Material(id, lang, issueDate, licenseType, source, repository, repositoryIdentifier, added, updated, views, picture, creator, deleted, paid, isSpecialEducation) values(2, 2, 2, 2, 'https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes', 1, 'isssiidosa00dsa', '1970-01-01 00:00:01', '1995-07-12 09:00:01', 200, null, 2, false, true, false);
insert into Material(id, lang, issueDate, licenseType, source, repository, repositoryIdentifier, added, updated, views, picture, creator, deleted, paid, isSpecialEducation) values(3, 4, 3, 3,  'http://eloquentjavascript.net/Eloquent_JavaScript.pdf', null, null, '2009-01-01 00:00:01', '2011-01-10 19:00:01', 300, null, null, false, false, false);
insert into Material(id, lang, issueDate, licenseType, source, repository, repositoryIdentifier, added, updated, views, picture, creator, deleted, paid, isSpecialEducation) values(4, 3, 4, 1,  'https://en.wikipedia.org/wiki/Power_Architecture', null, null, '2012-01-01 00:00:01', '2012-08-28 22:40:01', 400, null, 1, false, false, false);
insert into Material(id, lang, issueDate, licenseType, source, repository, repositoryIdentifier, added, updated, views, picture, creator, deleted, paid, isSpecialEducation) values(5, 3, 5, 2,  'https://en.wikipedia.org/wiki/Power_Architecture', null, null, '2011-09-01 00:00:01', '2012-11-04 09:30:01', 500, null, 2, false, false, false);
insert into Material(id, lang, issueDate, licenseType, source, repository, repositoryIdentifier, added, updated, views, picture, creator, deleted, paid, isSpecialEducation) values(6, null, null, null, 'http://www.planalto.gov.br/ccivil_03/Constituicao/Constituicao.htm', null, null, '1911-09-01 00:00:01', null, 600, null, null, false, false, false);
insert into Material(id, lang, issueDate, licenseType, source, repository, repositoryIdentifier, added, updated, views, picture, creator, deleted, paid, isSpecialEducation) values(7, 4, 6, 3, 'https://president.ee/en/republic-of-estonia/the-constitution/index.html', null, null, '2001-07-01 00:00:01', null, 700, null, null, false, false, false);
insert into Material(id, lang, issueDate, licenseType, source, repository, repositoryIdentifier, added, updated, views, picture, creator, deleted, paid, isSpecialEducation) values(8, 5, 7, 1, 'http://www.palmeiras.com.br/historia/titulos', null, null, '2014-06-01 00:00:01', null, 800, null, 1, false, false, false);
insert into Material(id, lang, issueDate, licenseType, source, repository, repositoryIdentifier, added, updated, views, picture, deleted, paid) values(9, null, null, null, 'http://www.chaging.it.com', null, null, '1911-09-01 00:00:01', null, 0, null, false, false);
insert into Material(id, lang, issueDate, licenseType, source, repository, repositoryIdentifier, added, updated, views, picture, deleted, paid) values(10, null, null, null, 'http://www.boo.com', null, null, '1911-09-01 00:00:01', null, 0, null, false, false); -- Do not use this material, it is deleted by tests
insert into Material(id, lang, issueDate, licenseType, source, repository, repositoryIdentifier, added, updated, views, picture, creator, deleted, paid) values(11, 1, null, 1, 'https://www.deleted.com/', 1, 'isssiiaawejdsada4564', '2015-09-02 00:00:01', '2015-09-03 07:00:01', 100, '656b6f6f6c696b6f7474', 1, true, false); -- This material should be amoung the 8 latest materials
insert into Material(id, lang, issueDate, licenseType, source, repository, repositoryIdentifier, added, updated, views, picture, deleted, paid) values(12, null, null, null, 'http://estRepo.com', 2, null, '1911-09-01 00:00:01', null, 0, null, false, false);

-- Authors

insert into Author(id, name, surname) values(1, 'Isaac', 'John Newton');
insert into Author(id, name, surname) values(2, 'Karl Simon Ben', 'Tom Oliver Marx');
insert into Author(id, name, surname) values(3, 'Leonardo', 'Fibonacci');

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

-- LanguageKeyCodes

insert into LanguageKeyCodes(lang, code) values (1, 'et');
insert into LanguageKeyCodes(lang, code) values (2, 'ru');
insert into LanguageKeyCodes(lang, code) values (3, 'en');
insert into LanguageKeyCodes(lang, code) values (5, 'pt');
insert into LanguageKeyCodes(lang, code) values (5, 'pt-br');
insert into LanguageKeyCodes(lang, code) values (6, 'fr');

-- Material Descriptions

insert into LanguageString(id, lang, textValue) values (1, 1, 'Test description in estonian. (Russian available)');
insert into LanguageString(id, lang, textValue) values (2, 2, 'Test description in russian, which is the only language available.');
insert into LanguageString(id, lang, textValue) values (3, 2, 'Test description in russian. (Estonian available)');
insert into LanguageString(id, lang, textValue) values (4, 5, 'Test description in portugese, as the material language (english) not available.');
insert into LanguageString(id, lang, textValue) values (5, 4, 'Test description in arabic (material language). No estonian or russian available.');
insert into LanguageString(id, lang, textValue) values (6, 3, 'Test description in english, which is the material language.');
insert into LanguageString(id, lang, textValue) values (7, 3, 'Test description in english, which is not the material language. Others are also available, but arent estonian or russian.');
insert into LanguageString(id, lang, textValue) values (8, 5, 'Test description in portugese, english also available.');

insert into Material_Description(description, material) values(1, 1);
insert into Material_Description(description, material) values(2, 2);
insert into Material_Description(description, material) values(3, 1);
insert into Material_Description(description, material) values(4, 4);
insert into Material_Description(description, material) values(5, 3);
insert into Material_Description(description, material) values(6, 5);
insert into Material_Description(description, material) values(7, 7);
insert into Material_Description(description, material) values(8, 7);

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


-- Material_Taxon

insert into Material_Taxon(material, taxon) values(1,20); -- PRESCHOOLEDUCATION/Mathematics/Biology
insert into Material_Taxon(material, taxon) values(2,21); -- PRESCHOOLEDUCATION/Mathematics/Mathematics
insert into Material_Taxon(material, taxon) values(3,20); -- PRESCHOOLEDUCATION/Mathematics/Biology
insert into Material_Taxon(material, taxon) values(4,20); -- PRESCHOOLEDUCATION/Mathematics/Biology
insert into Material_Taxon(material, taxon) values(5,21); -- PRESCHOOLEDUCATION/Mathematics/Mathematics
insert into Material_Taxon(material, taxon) values(6,20); -- PRESCHOOLEDUCATION/Mathematics/Biology
insert into Material_Taxon(material, taxon) values(6,21); -- PRESCHOOLEDUCATION/Mathematics/Mathematics

insert into Material_Taxon(material, taxon) values(7,11); -- PRESCHOOLEDUCATION/ForeignLanguage

insert into Material_Taxon(material, taxon) values(1,2); -- BASICEDUCATION
insert into Material_Taxon(material, taxon) values(2,4); -- HIGHEREDUCATION
insert into Material_Taxon(material, taxon) values(3,5); -- VOCATIONALEDUCATION
insert into Material_Taxon(material, taxon) values(4,6); -- CONTINUINGEDUCATION
insert into Material_Taxon(material, taxon) values(5,4); -- HIGHEREDUCATION
insert into Material_Taxon(material, taxon) values(7,5); -- VOCATIONALEDUCATION


-- ResourceType

insert into ResourceType(id, name) values (1001, 'TEXTBOOK1');
insert into ResourceType(id, name) values (1002, 'EXPERIMENT1');
insert into ResourceType(id, name) values (1003, 'SIMULATION1');
insert into ResourceType(id, name) values (1004, 'GLOSSARY1');
insert into ResourceType(id, name) values (1005, 'ROLEPLAY1');
insert into ResourceType(id, name) values (1006, 'WEBSITE');
insert into ResourceType(id, name) values (1007, 'COURSE');

-- Material_ResourceType

insert into Material_ResourceType(material, resourceType) values (1, 1001);
insert into Material_ResourceType(material, resourceType) values (1, 1002);
insert into Material_ResourceType(material, resourceType) values (2, 1003);
insert into Material_ResourceType(material, resourceType) values (3, 1004);
insert into Material_ResourceType(material, resourceType) values (4, 1005);
insert into Material_ResourceType(material, resourceType) values (5, 1003);
insert into Material_ResourceType(material, resourceType) values (6, 1002);
insert into Material_ResourceType(material, resourceType) values (7, 1004);

-- Publishers

insert into Publisher(id, name, website) values (1, 'Koolibri', 'http://www.koolibri.ee');
insert into Publisher(id, name, website) values (2, 'Pegasus', 'http://www.pegasus.ee');
insert into Publisher(id, name, website) values (3, 'Varrak', 'http://www.varrak.ee');

-- Cross-curricular themes
insert into CrossCurricularTheme(id, name) values (1, 'Lifelong_learning_and_career_planning');
insert into CrossCurricularTheme(id, name) values (2, 'Environment_and_sustainable_development');

-- Material Cross-curricular themes
insert into Material_CrossCurricularTheme(material, crossCurricularTheme) values (1, 1);

-- MaterialPublisher

insert into Material_Publisher(material, publisher) values (1, 1);
insert into Material_Publisher(material, publisher) values (1, 2);
insert into Material_Publisher(material, publisher) values (2, 2);
insert into Material_Publisher(material, publisher) values (3, 3);

-- Material Tags

insert into Tag(id, name) values (1, 'matemaatika');
insert into Tag(id, name) values (2, 'põhikool');
insert into Tag(id, name) values (3, 'õpik');
insert into Tag(id, name) values (4, 'mathematics');
insert into Tag(id, name) values (5, 'book');
insert into Tag(id, name) values (6, 'Математика');
insert into Tag(id, name) values (7, 'учебник');

insert into Material_Tag(tag, material) values(1, 1);
insert into Material_Tag(tag, material) values(1, 2);
insert into Material_Tag(tag, material) values(2, 1);
insert into Material_Tag(tag, material) values(3, 1);
insert into Material_Tag(tag, material) values(4, 1);
insert into Material_Tag(tag, material) values(4, 2);
insert into Material_Tag(tag, material) values(5, 1);
insert into Material_Tag(tag, material) values(6, 2);
insert into Material_Tag(tag, material) values(7, 2);

-- Material TargetGroups
insert into Material_TargetGroup(material, targetGroup) values (1, 'ZERO_FIVE');
insert into Material_TargetGroup(material, targetGroup) values (1, 'SIX_SEVEN');

-- TranslationGroup

insert into TranslationGroup(id, lang) values (1, 1);
insert into TranslationGroup(id, lang) values (2, 2);
insert into TranslationGroup(id, lang) values (3, 3);

-- Translation

-- Estonian
insert into Translation(translationGroup, translationKey, translation) values (1, 'FOO', 'FOO sõnum');
insert into Translation(translationGroup, translationKey, translation) values (1, 'Estonian', 'Eesti keeles');
insert into Translation(translationGroup, translationKey, translation) values (1, 'Russian', 'Vene keel');

-- Russian
insert into Translation(translationGroup, translationKey, translation) values (2, 'FOO', 'FOO сообщение');
insert into Translation(translationGroup, translationKey, translation) values (2, 'Estonian', 'Эстонский язык');
insert into Translation(translationGroup, translationKey, translation) values (2, 'Russian', 'русский язык');

-- English
insert into Translation(translationGroup, translationKey, translation) values (3, 'FOO', 'FOO message');
insert into Translation(translationGroup, translationKey, translation) values (3, 'Estonian', 'Estonian');
insert into Translation(translationGroup, translationKey, translation) values (3, 'Russian', 'Russian');

-- Page

-- Estonian
insert into Page(id, name, content, language) VALUES (1, 'About', '<h1>Meist</h1><p>Tekst siin</p>', 1);
insert into Page(id, name, content, language) VALUES (2, 'Help', '<h1>Abi</h1><p>ekst siine</p>', 1);

-- Russian
insert into Page(id, name, content, language) VALUES (3, 'About', '<h1>О нас</h1><p>Текст здесь.</p>', 2);
insert into Page(id, name, content, language) VALUES (4, 'Help', '<h1>Помощь</h1><p>Текст здесь.</p>', 2);

-- English
insert into Page(id, name, content, language) VALUES (5, 'About', '<h1>About us</h1><p>Text here</p>', 3);
insert into Page(id, name, content, language) VALUES (6, 'Help', '<h1>Help</h1><p>Text here</p>', 3);

-- Portfolio

insert into Portfolio(id, title, taxon, creator, summary, views, created, updated, picture) VALUES (1, 'The new stock market', 21, 6, 'The changes after 2008.', 95455215, '2000-12-29 06:00:01', '2004-12-29 06:00:01', '656b6f6f6c696b6f7474');
insert into Portfolio(id, title, taxon, creator, summary, views, created, updated, picture) VALUES (2, 'New ways how to do it', null, 4, null, 14, '2012-12-29 06:00:01', null, null);
insert into Portfolio(id, title, taxon, creator, summary, views, created, updated, picture) VALUES (3, 'The newer stock market', 21, 6, 'A marvellous summary.', 14, '2002-12-29 06:00:01', '2006-12-29 06:00:01', '656b6f6f6c696b6f7474');
insert into Portfolio(id, title, taxon, creator, summary, views, created, updated, picture) VALUES (4, 'The even newer stock market', null, 1, 'Cool summary.', 100, '2003-10-10 07:00:11', null, null);

-- Chapter

insert into Chapter(id, title, portfolio, textValue, parentChapter, chapterOrder) values (1, 'The crisis', 1, null, null, 0);
insert into Chapter(id, title, portfolio, textValue, parentChapter, chapterOrder) values (2, 'Chapter 3', 1, 'This is some text that explains what is the Chapter 3 about.' || char(10) || 'It can have many lines' || char(10) || char(10) || char(10) || 'And can also have    spaces   betwenn    the words on it', null, 2);
insert into Chapter(id, title, portfolio, textValue, parentChapter, chapterOrder) values (3, 'Chapter 2', 1, 'Paragraph 1' || char(10) || char(10) || 'Paragraph 2' || char(10) || char(10) || 'Paragraph 3' || char(10) || char(10) || 'Paragraph 4', null, 1);
insert into Chapter(id, title, portfolio, textValue, parentChapter, chapterOrder) values (4, 'Subprime', null, null, 1, 0); -- Subchpater of #1
insert into Chapter(id, title, portfolio, textValue, parentChapter, chapterOrder) values (5, 'The big crash', null, 'Bla bla bla' || char(10) || 'Bla bla bla bla bla bla bla', 1, 1); -- Subchpater of #1

-- Chapter-Material

insert into Chapter_Material(chapter, material, materialOrder) values(1, 1, 0);
insert into Chapter_Material(chapter, material, materialOrder) values(4, 5, 0);
insert into Chapter_Material(chapter, material, materialOrder) values(4, 1, 1);
insert into Chapter_Material(chapter, material, materialOrder) values(4, 8, 2);
insert into Chapter_Material(chapter, material, materialOrder) values(5, 3, 0);

-- Portfolio-Tags

insert into Portfolio_Tag(tag, portfolio) values(1, 1);
insert into Portfolio_Tag(tag, portfolio) values(1, 3);
insert into Portfolio_Tag(tag, portfolio) values(2, 1);
insert into Portfolio_Tag(tag, portfolio) values(3, 1);
insert into Portfolio_Tag(tag, portfolio) values(4, 1);
insert into Portfolio_Tag(tag, portfolio) values(4, 3);
insert into Portfolio_Tag(tag, portfolio) values(5, 1);

-- Portfolio TargetGroups
insert into Portfolio_TargetGroup(portfolio, targetGroup) values (1, 'SIX_SEVEN');
insert into Portfolio_TargetGroup(portfolio, targetGroup) values (1, 'ZERO_FIVE');

-- Portfolio Cross-curricular themes
insert into Portfolio_CrossCurricularTheme(portfolio, crossCurricularTheme) values (1, 1);