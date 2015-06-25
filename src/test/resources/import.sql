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

-- License Type

insert into LicenseType(id, text) values (1, 'CCBY');
insert into LicenseType(id, text) values (2, 'CCBYSA');
insert into LicenseType(id, text) values (3, 'CCBYND');

-- Materials

insert into Material(id, lang, issueDate, licenseType, source) values(1, 1, 1, 1, 'https://www.youtube.com/watch?v=gSWbx3CvVUk');
insert into Material(id, lang, issueDate, licenseType, source) values(2, 2, 2, 2, 'https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes');
insert into Material(id, lang, issueDate, licenseType, source) values(3, 4, 3, 3,  'http://eloquentjavascript.net/Eloquent_JavaScript.pdf');
insert into Material(id, lang, issueDate, licenseType, source) values(4, 5, 4, 1,  'https://en.wikipedia.org/wiki/Power_Architecture');
insert into Material(id, lang, issueDate, licenseType, source) values(5, 5, 5, 2,  'https://en.wikipedia.org/wiki/Power_Architecture');
insert into Material(id, lang, issueDate, licenseType, source) values(6, null, null, null, 'http://www.planalto.gov.br/ccivil_03/Constituicao/Constituicao.htm');
insert into Material(id, lang, issueDate, licenseType, source) values(7, 4, 6, 3, 'https://president.ee/en/republic-of-estonia/the-constitution/index.html');
insert into Material(id, lang, issueDate, licenseType, source) values(8, 5, 7, 1, 'http://www.palmeiras.com.br/historia/titulos');

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
insert into LanguageKeyCodes(lang, code) values (3, 'pt');
insert into LanguageKeyCodes(lang, code) values (3, 'pt-br');
insert into LanguageKeyCodes(lang, code) values (5, 'en');

-- Material Descriptions

insert into LanguageString(id, lang, textValue) values (1, 1, 'Test description in estonian. (Russian available)');
insert into LanguageString(id, lang, textValue) values (2, 2, 'Test description in russian, which is the only language available.');
insert into LanguageString(id, lang, textValue) values (3, 2, 'Test description in russian. (Estonian available)');
insert into LanguageString(id, lang, textValue) values (4, 3, 'Test description in portugese, as the material language (english) not available.');
insert into LanguageString(id, lang, textValue) values (5, 4, 'Test description in arabic (material language). No estonian or russian available.');
insert into LanguageString(id, lang, textValue) values (6, 5, 'Test description in english, which is the material language.');
insert into LanguageString(id, lang, textValue) values (7, 5, 'Test description in english, which is not the material language. Others are also available, but arent estonian or russian.');
insert into LanguageString(id, lang, textValue) values (8, 3, 'Test description in portugese, english also available.');

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

-- Classifications

insert into Classification(id, classificationName, parent) values (1, 'Biology', null);
insert into Classification(id, classificationName, parent) values(2, 'Plants', 1);
insert into Classification(id, classificationName, parent) values(3, 'Trees', 2);
insert into Classification(id, classificationName, parent) values (4, 'Math', null);
insert into Classification(id, classificationName, parent) values(5, 'Algebra', 4);
insert into Classification(id, classificationName, parent) values(6, 'Linear', 5);
insert into Classification(id, classificationName, parent) values(7, 'Quadratic', 5);


-- Material classifications

insert into Material_Classification(classification, material) values(1,1);
insert into Material_Classification(classification, material) values(2,1);
insert into Material_Classification(classification, material) values(2,2);
insert into Material_Classification(classification, material) values(1,3);
insert into Material_Classification(classification, material) values(1,4);
insert into Material_Classification(classification, material) values(5,4);
insert into Material_Classification(classification, material) values(7,5);

-- ResourceType

insert into ResourceType(id, resourceType) values (1001, 'TEXTBOOK');
insert into ResourceType(id, resourceType) values (1002, 'EXPERIMENT');
insert into ResourceType(id, resourceType) values (1003, 'SIMULATION');
insert into ResourceType(id, resourceType) values (1004, 'GLOSSARY');
insert into ResourceType(id, resourceType) values (1005, 'ROLEPLAY');

-- Material_ResourceType

insert into Material_ResourceType(material, resourceType) values (1, 1001);
insert into Material_ResourceType(material, resourceType) values (1, 1002);
insert into Material_ResourceType(material, resourceType) values (2, 1003);
insert into Material_ResourceType(material, resourceType) values (3, 1004);
insert into Material_ResourceType(material, resourceType) values (4, 1005);
insert into Material_ResourceType(material, resourceType) values (5, 1003);
insert into Material_ResourceType(material, resourceType) values (6, 1002);
insert into Material_ResourceType(material, resourceType) values (7, 1004);

-- EducationalContext

insert into EducationalContext(id, educationalContext) values (1001, 'PRESCHOOL');
insert into EducationalContext(id, educationalContext) values (1002, 'COMPULSORYEDUCATION');
insert into EducationalContext(id, educationalContext) values (1003, 'VOCATIONALEDUCATION');
insert into EducationalContext(id, educationalContext) values (1004, 'HIGHEREDUCATION');
insert into EducationalContext(id, educationalContext) values (1005, 'CONTINUINGEDUCATION');
insert into EducationalContext(id, educationalContext) values (1006, 'PROFESSIONALDEVELOPMENT');
insert into EducationalContext(id, educationalContext) values (1007, 'SPECIALEDUCATION');
insert into EducationalContext(id, educationalContext) values (1008, 'OTHER');


-- EducationalContext

insert into Material_EducationalContext(material, educationalContext) values (1, 1001);
insert into Material_EducationalContext(material, educationalContext) values (1, 1002);
insert into Material_EducationalContext(material, educationalContext) values (2, 1003);
insert into Material_EducationalContext(material, educationalContext) values (3, 1004);
insert into Material_EducationalContext(material, educationalContext) values (4, 1005);
insert into Material_EducationalContext(material, educationalContext) values (5, 1003);
insert into Material_EducationalContext(material, educationalContext) values (6, 1002);
insert into Material_EducationalContext(material, educationalContext) values (7, 1004);

-- Publishers

insert into Publisher(id, text, website) values (1, 'Koolibri', 'http://www.koolibri.ee');
insert into Publisher(id, text, website) values (2, 'Pegasus', 'http://www.pegasus.ee');
insert into Publisher(id, text, website) values (3, 'Varrak', 'http://www.varrak.ee');

-- MaterialPublisher

insert into Material_Publisher(material, publisher) values (1, 1);
insert into Material_Publisher(material, publisher) values (1, 2);
insert into Material_Publisher(material, publisher) values (2, 2);
insert into Material_Publisher(material, publisher) values (3, 3);

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
