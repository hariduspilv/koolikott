-- Material issue dates

insert into IssueDate(id, day, month, year) values(1, 2, 2, 1983);
insert into IssueDate(id, day, month, year) values(2, 27, 1, -983);
insert into IssueDate(id, year) values(3, -1500);
insert into IssueDate(id, day, month, year) values(4, 31, 3, 1923);
insert into IssueDate(id, day, month, year) values(5, 9, 12, 1978);
insert into IssueDate(id, day, month, year) values(6, 27, 1, 1986);
insert into IssueDate(id, month, year) values(7, 3, 1991);

-- Materials

insert into Material(id, title, lang, issueDate) values(1, 'Matemaatika õpik üheksandale klassile', 'est', 1);
insert into Material(id, title, lang, issueDate) values(2, '?????????? ??????? ??? 8-?? ??????', 'rus', 2);
insert into Material(id, title, lang, issueDate) values(3, '8???????? ????? ???????? ???? 6', 'ara', 3);
insert into Material(id, title, lang, issueDate) values(4, 'Mathematics textbook for 6th grade', 'eng', 4);
insert into Material(id, title, lang,issueDate) values(5, 'Mathematics textbook for 5th grade', 'eng', 5);
insert into Material(id, title) values(6, 'Mathematics textbook for 4th grade');
insert into Material(id, title, lang, issueDate) values(7, 'Mathematics textbook for 3th grade', 'eng', 6);
insert into Material(id, title, lang, issueDate) values(8, 'The Capital', 'eng', 7);

-- Authors

insert into Author(id, name, surname) values(1, 'Isaac mart', 'samjang Newton');
insert into Author(id, name, surname) values(2, 'Karl voldemar meelis', 'jaanus albert Marx');
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

-- LanguageTable

insert into LanguageTable(id, name, code) values (1, 'Estonian', 'est');
insert into LanguageTable(id, name, code) values (2, 'Russian', 'rus');
insert into LanguageTable(id, name, code) values (3, 'Portuguese', 'por');
insert into LanguageTable(id, name, code) values (4, 'Arabic', 'ara');
insert into LanguageTable(id, name, code) values (5, 'English', 'eng');

-- LanguageKeyCodes

insert into LanguageKeyCodes(lang, code) values (1, 'et');
insert into LanguageKeyCodes(lang, code) values (2, 'ru');
insert into LanguageKeyCodes(lang, code) values (3, 'pt');
insert into LanguageKeyCodes(lang, code) values (3, 'pt-br');

-- LanguageDescription

insert into LanguageString(id, lang, textValue, material) values (1, 1, 'test description in estonian', 1);
insert into LanguageString(id, lang, textValue, material) values (2, 2, 'test description in russian', 2);
insert into LanguageString(id, lang, textValue, material) values (3, 2, 'test description in russian', 1);
insert into LanguageString(id, lang, textValue, material) values (4, 5, 'test description in english', 3);
insert into LanguageString(id, lang, textValue, material) values (5, 4, 'test description in arabic', 3);
