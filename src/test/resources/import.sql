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
insert into LanguageTable(id, name, code) values (3, 'Portuguese', 'por');
insert into LanguageTable(id, name, code) values (4, 'Arabic', 'ara');
insert into LanguageTable(id, name, code) values (5, 'English', 'eng');

-- Materials

insert into Material(id, title, lang, issueDate) values(1, 'Matemaatika õpik üheksandale klassile', 1, 1);
insert into Material(id, title, lang, issueDate) values(2, 'Математика учебник для 8-го класса', 2, 2);
insert into Material(id, title, lang, issueDate) values(3, 'الرياضيات الكتب المدرسية للصف 7', 4, 3);
insert into Material(id, title, lang, issueDate) values(4, 'Mathematics textbook for 6th grade', 5, 4);
insert into Material(id, title, lang,issueDate) values(5, 'Mathematics textbook for 5th grade', 5, 5);
insert into Material(id, title) values(6, 'Mathematics textbook for 4th grade');
insert into Material(id, title, lang, issueDate) values(7, 'Mathematics textbook for 3th grade', 4, 6);
insert into Material(id, title, lang, issueDate) values(8, 'The Capital', 5, 7);

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

-- LanguageDescription

insert into LanguageString(id, lang, material, textValue) values (1, 1, 1, 'Test description in estonian. (Russian available)');
insert into LanguageString(id, lang, material, textValue) values (2, 2, 2, 'Test description in russian, which is the only language available.');
insert into LanguageString(id, lang, material, textValue) values (3, 2, 1, 'Test description in russian. (Estonian available)');
insert into LanguageString(id, lang, material, textValue) values (4, 3, 4, 'Test description in portugese, as the material language (english) not available.');
insert into LanguageString(id, lang, material, textValue) values (5, 4, 3, 'Test description in arabic (material language). No estonian or russian available.');
insert into LanguageString(id, lang, material, textValue) values (6, 5, 5, 'Test description in english, which is the material language.');
insert into LanguageString(id, lang, material, textValue) values (7, 5, 7, 'Test description in english, which is not the material language. Others are also available, but arent estonian or russian.');
insert into LanguageString(id, lang, material, textValue) values (8, 3, 7, 'Test description in portugese, english also available.');


-- TranslationGroup

insert into TranslationGroup(id, lang) values (1, 1);
insert into TranslationGroup(id, lang) values (2, 2);

-- Translation

-- Estonian
insert into Translation(translationGroup, translationKey, translation) values (1, 'FOO', 'FOO sõnum');
insert into Translation(translationGroup, translationKey, translation) values (1, 'Estonian', 'Eesti keeles');
insert into Translation(translationGroup, translationKey, translation) values (1, 'Russian', 'Vene keel');

-- Russian
insert into Translation(translationGroup, translationKey, translation) values (2, 'FOO', 'FOO сообщение');
insert into Translation(translationGroup, translationKey, translation) values (2, 'Estonian', 'Эстонский язык');
insert into Translation(translationGroup, translationKey, translation) values (2, 'Russian', 'русский язык');
