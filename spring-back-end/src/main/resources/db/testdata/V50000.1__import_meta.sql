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

-- migration adds youtube
insert into LicenseType(id, name) values (1, 'CCBY');
insert into LicenseType(id, name) values (2, 'CCBYSA');
insert into LicenseType(id, name) values (3, 'CCBYND');

-- Repository. Do not use real URLs here

insert into Repository(id, baseURL, lastSynchronization, schemaName, contentIsEmbeddable, metadataPrefix, used) values (1, 'http://repo1.ee', null, 'waramu', false, 'oai_estcore', true);
insert into Repository(id, baseURL, lastSynchronization, schemaName, contentIsEmbeddable, metadataPrefix, used) values (2, 'http://estonianPublisher.ee/OAIHandler', null, 'estCore', true, 'oai_estcore', true);

-- Publishers

insert into Publisher(id, name, website) values (1, 'Koolibri', 'http://www.koolibri.ee');
insert into Publisher(id, name, website) values (2, 'Pegasus', 'http://www.pegasus.ee');
insert into Publisher(id, name, website) values (3, 'Varrak', 'http://www.varrak.ee');

-- User

insert into User(id, userName, name, surName, idCode, role, publisher) values (1, 'mati.maasikas', 'Mati', 'Maasikas', '39011220011', 'USER', null);
insert into User(id, userName, name, surName, idCode, role, publisher) values (2, 'peeter.paan', 'Peeter', 'Paan', '38011550077', 'USER', null);
insert into User(id, userName, name, surName, idCode, role, publisher) values (3, 'voldemar.vapustav', 'Voldemar', 'Vapustav', '37066990099', 'USER', null);
insert into User(id, userName, name, surName, idCode, role, publisher) values (4, 'voldemar.vapustav2', 'Voldemar', 'Vapustav', '15066990099', 'USER', null);
insert into User(id, userName, name, surName, idCode, role, publisher) values (5, 'mati.maasikas2', 'Mäti', 'Maasikas', '39011220012', 'USER', null);
insert into User(id, userName, name, surName, idCode, role, publisher) values (6, 'mati.maasikas-vaarikas', 'Mati', 'Maasikas-Vaarikas', '39011220013', 'USER', null);
insert into User(id, userName, name, surName, idCode, role, publisher) values (7, 'my.testuser', 'My', 'Testuser', '78912378912', 'USER', null);
insert into User(id, userName, name, surName, idCode, role, publisher) values (8, 'admin.admin', 'Admin', 'Admin', '89898989898', 'ADMIN', null);
insert into User(id, userName, name, surName, idCode, role, publisher) values (9, 'second.testuser', 'Second', 'Testuser', '89012378912', 'USER', null);
insert into User(id, userName, name, surName, idCode, role, publisher) values (10, 'super.publisher', 'Super', 'Publisher', '77007700770', 'USER', 1);
insert into User(id, userName, name, surName, idCode, role, publisher) values (11, 'restricted.user', 'Restricted', 'User', '89898989890', 'RESTRICTED', null);
insert into User(id, userName, name, surName, idCode, role, publisher) values (12, 'biffy.clyro', 'Biffy', 'Clyro', '38211120031', 'MODERATOR', null);
insert into User(id, userName, name, surName, idCode, role, publisher) values (13, 'user.to.be.banned1', 'Whiskey', 'Tango', '38256133107', 'USER', null);
insert into User(id, userName, name, surName, idCode, role, publisher) values (14, 'user.to.be.banned2', 'November', 'Juliet', '38256133108', 'USER', null);
insert into User(id, userName, name, surName, idCode, role, publisher) values (15, 'restricted.user2', 'Restricted', 'User', '89898989892', 'RESTRICTED', null);
insert into User(id, userName, name, surName, idCode, role, publisher) values (16, 'taxon.user', 'Taxon', 'User', '11111111111', 'MODERATOR', null);

-- UserTourData

INSERT INTO UserTourData(id, user, generalTour, editTour) VALUES (1, 1, 1, 0);

-- AuthenticatedUser

insert into AuthenticatedUser(id, user_id, token, firstLogin, person, deleted, loggedOut, declined, loginFrom, loginDate) values (1, 1, 'token', false, null, 0, 0, 0, 'DEV', {ts'2011-09-01 00:00:01'});

-- AuthenticationState

insert into AuthenticationState(id, token) values (1, 'testTOKEN');
insert into AuthenticationState(id, token) values (2, 'taatAuthenticateTestToken');

-- EducationalContext

insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (1, 'PRESCHOOLEDUCATION', 'preschooleducation', 'PRESCHOOLEDUCATION', 'EDUCATIONAL_CONTEXT', 1);
insert into EducationalContext(id, used) values (1, 1);
insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (2, 'BASICEDUCATION', 'basiceducation', 'BASICEDUCATION', 'EDUCATIONAL_CONTEXT', 1);
insert into EducationalContext(id, used) values (2, 1);
insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (3, 'SECONDARYEDUCATION', 'secondaryeducation', 'SECONDARYEDUCATION', 'EDUCATIONAL_CONTEXT', 1);
insert into EducationalContext(id, used) values (3, 1);
insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (4, 'HIGHEREDUCATION', 'highereducation', 'HIGHEREDUCATION', 'EDUCATIONAL_CONTEXT', 1);
insert into EducationalContext(id, used) values (4, 1);
insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (5, 'VOCATIONALEDUCATION', 'vocationaleducation', 'VOCATIONALEDUCATION', 'EDUCATIONAL_CONTEXT', 1);
insert into EducationalContext(id, used) values (5, 1);
insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (6, 'CONTINUINGEDUCATION', 'continuingeducation', 'CONTINUINGEDUCATION', 'EDUCATIONAL_CONTEXT', 1);
insert into EducationalContext(id, used) values (6, 1);
insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (7, 'TEACHEREDUCATION', 'teachereducation', 'TEACHEREDUCATION', 'EDUCATIONAL_CONTEXT', 1);
insert into EducationalContext(id, used) values (7, 1);
insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (8, 'SPECIALEDUCATION', 'specialeducation', 'SPECIALEDUCATION', 'EDUCATIONAL_CONTEXT', 1);
insert into EducationalContext(id, used) values (8, 1);
insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (9, 'OTHER', 'other', 'OTHER', 'EDUCATIONAL_CONTEXT', 1);
insert into EducationalContext(id, used) values (9, 1);

-- Domain

insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (10, 'Mathematics', 'mathematics', 'Mathematics', 'DOMAIN', 1);
insert into Domain(id, educationalContext, used) values (10, 1, 1);
insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (11, 'ForeignLanguage', 'foreignlanguage','ForeignLanguage', 'DOMAIN', 1);
insert into Domain(id, educationalContext, used) values (11, 1, 1);
insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (12, 'DomainWithTopics', 'domainwithtopics','DomainWithTopics', 'DOMAIN', 1);
insert into Domain(id, educationalContext, used) values (12, 6, 1);
insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (13, 'SecondaryDomain', 'secondarydomain','SecondaryDomain', 'DOMAIN', 1);
insert into Domain(id, educationalContext, used) values (13, 3, 1);
insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (14, 'Computer_science', 'computer_science', 'Computer_science', 'DOMAIN', 1);
insert into Domain(id, educationalContext, used) values (14, 5, 1);
insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (15, 'Unused_Taxon', 'unused_taxon', 'Unused_Taxon', 'DOMAIN', 0);
insert into Domain(id, educationalContext, used) values (15, 7, 0);
insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (16, 'Used_Taxon', 'used_taxon' ,'Used_Taxon', 'DOMAIN', 1);
insert into Domain(id, educationalContext, used) values (16, 7, 1);
insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (17, 'Arvutiteadused', 'arvutiteadused' ,'Arvutiteadused', 'DOMAIN', 1);
insert into Domain(id, educationalContext, used) values (17, 5, 1);
insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (18, 'Secondary_with_subtopic', 'Secondary_with_subtopic' ,'Secondary_with_subtopic', 'DOMAIN', 1);
insert into Domain(id, educationalContext, used) values (18, 3, 1);
insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (19, 'Specialization_module_topic_subtopic', 'specialization_module_topic_subtopic' ,'Specialization_module_topic_subtopic', 'DOMAIN', 1);
insert into Domain(id, educationalContext, used) values (19, 8, 1);


-- Subject

insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (20, 'Biology', 'biology', 'Biology', 'SUBJECT', 1);
insert into Subject(id, domain, used) values (20, 10, 1);
insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (21, 'Mathematics', 'mathematics', 'Mathematics', 'SUBJECT', 1);
insert into Subject(id, domain, used) values (21, 10, 1);
insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (22, 'SecondarySubject', 'secondarysubject', 'SecondarySubject', 'SUBJECT', 1);
insert into Subject(id, domain, used) values (22, 13, 1);
insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (23, 'SecondarySubject2', 'secondarysubject2', 'SecondarySubject2', 'SUBJECT', 1);
insert into Subject(id, domain, used) values (23, 18, 1);

-- Specialization

insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (40, 'Computers_and_Networks', 'computers_and_networks', 'Computers_and_Networks', 'SPECIALIZATION', 1);
insert into Specialization(id, domain, used) values (40, 14, 1);
insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (41, 'Special', 'special', 'Special', 'SPECIALIZATION', 1);
insert into Specialization(id, domain, used) values (41, 19, 1);

-- Module

insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (50, 'IT_õigus', 'it_õigus', 'IT_õigus', 'MODULE', 1);
insert into Module(id, specialization, used) values (50, 40, 1);
insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (51, 'Kommunikatsioon', 'kommunikatsioon', 'Kommunikatsioon', 'MODULE', 1);
insert into Module(id, specialization, used) values (51, 40, 1);
insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (52, 'Module_for_special', 'module_for_special', 'Module_for_special', 'MODULE', 1);
insert into Module(id, specialization, used) values (52, 41, 1);

-- Topics from Subjects

insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (30, 'Algebra', 'algebra', 'Algebra', 'TOPIC', 1);
insert into Topic(id, subject, used) values (30, 21, 1);
insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (31, 'Trigonometria', 'trigonometria', 'Trigonometria', 'TOPIC', 1);
insert into Topic(id, subject, used) values (31, 21, 1);
insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (37, 'Topic_from_subject', 'Topic_from_subject', 'Topic_from_subject', 'TOPIC', 1);
insert into Topic(id, subject, used) values (37, 23, 1);

-- Topics from Domain

insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (32, 'EstoniaAndTheWould', 'estoniaandthewould', 'EstoniaAndTheWould', 'TOPIC', 1);
insert into Topic(id, domain, used) values (32, 12, 1);
insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (33, 'VogaisTonicas', 'vogaistonicas', 'VogaisTonicas','TOPIC', 1);
insert into Topic(id, domain, used) values (33, 12, 1);
insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (36, 'Info-ja_kommunikatsioonitehnoloogia', 'info-ja_kommunikatsioonitehnoloogia', 'Info-ja_kommunikatsioonitehnoloogia', 'TOPIC', 1);
insert into Topic(id, domain, used) values (36, 17, 1);

-- Topics from Module

insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (34, 'Infoühiskonna_tehnoloogiad', 'infoühiskonna_tehnoloogiad', 'Infoühiskonna_tehnoloogiad', 'TOPIC', 1);
insert into Topic(id, module, used) values (34, 50, 1);
insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (35, 'Arvuti_töövahendina', 'arvuti_töövahendina', 'Arvuti_töövahendina', 'TOPIC', 1);
insert into Topic(id, module, used) values (35, 51, 1);
insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (38, 'Module_from_subject', 'module_from_subject', 'Module_from_subject', 'TOPIC', 1);
insert into Topic(id, module, used) values (38, 52, 1);

-- Subtopic

insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (60, 'arvsõna', 'arvsõna', 'arvsõna', 'SUBTOPIC', 1);
insert into Subtopic(id, topic, used) values (60, 30, 1);
insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (61, 'konkurents', 'konkurents', 'konkurents', 'SUBTOPIC', 1);
insert into Subtopic(id, topic, used) values (61, 32, 1);
insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (62, 'tehnoloogia_ja_ühiskond', 'tehnoloogia_ja_ühiskond', 'tehnoloogia_ja_ühiskond', 'SUBTOPIC', 1);
insert into Subtopic(id, topic, used) values (62, 34, 1);
insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (63, 'tehnoloogia_ja_ühiskond', 'tehnoloogia_ja_ühiskond', 'tehnoloogia_ja_ühiskond', 'SUBTOPIC', 1);
insert into Subtopic(id, topic, used) values (63, 37, 1);
insert into Taxon(id, name, nameLowerCase, translationKey, level, used) values (64, 'Subtopic_from_topic', 'subtopic_from_topic', 'Subtopic_from_topic', 'SUBTOPIC', 1);
insert into Subtopic(id, topic, used) values (64, 38, 1);


-- UserTaxon

INSERT INTO User_Taxon(user, taxon) VALUES (12, 1);
INSERT INTO User_Taxon(user, taxon) VALUES (12, 10);
INSERT INTO User_Taxon(user, taxon) VALUES (12, 21);
INSERT INTO User_Taxon(user, taxon) VALUES (12, 31);
INSERT INTO User_Taxon(user, taxon) VALUES (16, 21);
INSERT INTO User_Taxon(user, taxon) VALUES (16, 31);

-- EstCore taxon mapping

insert into EstCoreTaxonMapping(id, taxon, name, nameLowercase) values (1, 1, 'preschoolEducation', 'preschooleducation');
insert into EstCoreTaxonMapping(id, taxon, name, nameLowercase) values (2, 2, 'basicEducation', 'basiceducation');
insert into EstCoreTaxonMapping(id, taxon, name, nameLowercase) values (3, 3, 'secondaryEducation', 'secondaryeducation');
insert into EstCoreTaxonMapping(id, taxon, name, nameLowercase) values (4, 4, 'higherEducation', 'highereducation');
insert into EstCoreTaxonMapping(id, taxon, name, nameLowercase) values (5, 5, 'vocationalEducation', 'vocationaleducation');
insert into EstCoreTaxonMapping(id, taxon, name, nameLowercase) values (6, 6, 'continuingEducation', 'continuingeducation');
insert into EstCoreTaxonMapping(id, taxon, name, nameLowercase) values (7, 7, 'teacherEducation', 'teachereducation');
insert into EstCoreTaxonMapping(id, taxon, name, nameLowercase) values (8, 8, 'specialEducation', 'specialeducation');
insert into EstCoreTaxonMapping(id, taxon, name, nameLowercase) values (9, 9, 'other', 'other');
insert into EstCoreTaxonMapping(id, taxon, name, nameLowercase) values (10, 10, 'Mathematics', 'mathematics');
insert into EstCoreTaxonMapping(id, taxon, name, nameLowercase) values (11, 11, 'Foreign language', 'foreign language');
insert into EstCoreTaxonMapping(id, taxon, name, nameLowercase) values (12, 12, 'DomainWithTopics', 'domainwithtopics');
insert into EstCoreTaxonMapping(id, taxon, name, nameLowercase) values (17, 17, 'Arvutiteadused', 'arvutiteadused');
insert into EstCoreTaxonMapping(id, taxon, name, nameLowercase) values (18, 18, 'Secondary_with_subtopic', 'secondary_with_subtopic');
insert into EstCoreTaxonMapping(id, taxon, name, nameLowercase) values (19, 19, 'Specialization_module_topic_subtopic', 'specialization_module_topic_subtopic');
insert into EstCoreTaxonMapping(id, taxon, name, nameLowercase) values (20, 20, 'Biology', 'biology');
insert into EstCoreTaxonMapping(id, taxon, name, nameLowercase) values (21, 21, 'Mathematics', 'mathematics');
insert into EstCoreTaxonMapping(id, taxon, name, nameLowercase) values (22, 22, 'SecondarySubject', 'secondarysubject');
insert into EstCoreTaxonMapping(id, taxon, name, nameLowercase) values (23, 23, 'SecondarySubject2', 'secondarysubject2');
insert into EstCoreTaxonMapping(id, taxon, name, nameLowercase) values (30, 30, 'Algebra', 'algebra');
insert into EstCoreTaxonMapping(id, taxon, name, nameLowercase) values (31, 31, 'Trigonometria', 'trigonometria');
insert into EstCoreTaxonMapping(id, taxon, name, nameLowercase) values (32, 32, 'EstoniaAndTheWould', 'estoniaandthewould');
insert into EstCoreTaxonMapping(id, taxon, name, nameLowercase) values (33, 33, 'VogaisTonicas', 'vogaistonicas');
insert into EstCoreTaxonMapping(id, taxon, name, nameLowercase) values (36, 36, 'Info-ja_kommunikatsioonitehnoloogia', 'info-ja_kommunikatsioonitehnoloogia');
insert into EstCoreTaxonMapping(id, taxon, name, nameLowercase) values (37, 37, 'Topic_from_subject', 'topic_from_subject');
insert into EstCoreTaxonMapping(id, taxon, name, nameLowercase) values (41, 41, 'Special', 'special');
insert into EstCoreTaxonMapping(id, taxon, name, nameLowercase) values (52, 52, 'Module_for_special', 'module_for_special');


-- Translation
insert into TranslationGroup(id, lang) values (1, 1);
insert into TranslationGroup(id, lang) values (2, 2);
insert into TranslationGroup(id, lang) values (3, 3);


-- Estonian
insert into Translation(translationGroup, translationKey, translation) values (1, 'FOO', 'FOO sõnum');
insert into Translation(translationGroup, translationKey, translation) values (1, 'Estonian', 'Eesti keeles');
insert into Translation(translationGroup, translationKey, translation) values (1, 'Russian', 'Vene keel');
insert into Translation(translationGroup, translationKey, translation) values (1, 'TOPIC_MATHEMATICS', 'Matemaatika');
insert into Translation(translationGroup, translationKey, translation) values (1, 'FEED_ID', 'e-Koolikott:et');
insert into Translation(translationGroup, translationKey, translation) values (1, 'FEED_TITLE', 'e-Koolikott - Uudised');
insert into Translation(translationGroup, translationKey, translation) values (1, 'FEED_VERSION_TITLE', 'Uus versioon "%s"');
insert into Translation(translationGroup, translationKey, translation) values (1, 'FEED_PORTFOLIO_TITLE', 'Uus portfoolio "%s"');
insert into Translation(translationGroup, translationKey, translation) values (1, 'FEED_MATERIAL_TITLE', 'Uus material "%s"');
insert into Translation(translationGroup, translationKey, translation) values (1, 'DOMAIN_FOREIGNLANGUAGE', 'ForeignLanguage');

-- Russian
insert into Translation(translationGroup, translationKey, translation) values (2, 'FOO', 'FOO сообщение');
insert into Translation(translationGroup, translationKey, translation) values (2, 'Estonian', 'Эстонский язык');
insert into Translation(translationGroup, translationKey, translation) values (2, 'Russian', 'русский язык');
insert into Translation(translationGroup, translationKey, translation) values (2, 'TOPIC_MATHEMATICS', 'Mатематика');
insert into Translation(translationGroup, translationKey, translation) values (2, 'FEED_ID', 'e-Koolikott:ru');
insert into Translation(translationGroup, translationKey, translation) values (2, 'FEED_TITLE', 'Новая версия "%s"');
insert into Translation(translationGroup, translationKey, translation) values (2, 'FEED_VERSION_TITLE', 'Feed version title rus');
insert into Translation(translationGroup, translationKey, translation) values (2, 'FEED_PORTFOLIO_TITLE', 'Новый портфель "%s"');
insert into Translation(translationGroup, translationKey, translation) values (2, 'FEED_MATERIAL_TITLE', 'Новый материал "%s"');
insert into Translation(translationGroup, translationKey, translation) values (2, 'DOMAIN_FOREIGNLANGUAGE', 'ForeignLanguage');

-- English
insert into Translation(translationGroup, translationKey, translation) values (3, 'FOO', 'FOO message');
insert into Translation(translationGroup, translationKey, translation) values (3, 'Estonian', 'Estonian');
insert into Translation(translationGroup, translationKey, translation) values (3, 'Russian', 'Russian');
insert into Translation(translationGroup, translationKey, translation) values (3, 'TOPIC_MATHEMATICS', 'Mathematics');
insert into Translation(translationGroup, translationKey, translation) values (3, 'FEED_ID', 'e-Koolikott:en');
insert into Translation(translationGroup, translationKey, translation) values (3, 'FEED_TITLE', 'e-Koolikott - News');
insert into Translation(translationGroup, translationKey, translation) values (3, 'FEED_VERSION_TITLE', 'New version "%s"');
insert into Translation(translationGroup, translationKey, translation) values (3, 'FEED_PORTFOLIO_TITLE', 'New portfolio "%s"');
insert into Translation(translationGroup, translationKey, translation) values (3, 'FEED_MATERIAL_TITLE', 'New material "%s"');
insert into Translation(translationGroup, translationKey, translation) values (3, 'DOMAIN_FOREIGNLANGUAGE', 'ForeignLanguage');

-- Landing Page
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'LANDING_PAGE_DESCRIPTION', 'Avalehe kirjeldus');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'LANDING_PAGE_DESCRIPTION', 'Landing page description');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'LANDING_PAGE_DESCRIPTION', 'Avalehe kirjeldus VENE KEELES');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1, 'LANDING_PAGE_NOTICE', '');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3, 'LANDING_PAGE_NOTICE', '');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2, 'LANDING_PAGE_NOTICE', '');