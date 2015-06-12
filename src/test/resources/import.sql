-- Material issue dates

insert into IssueDate(id, day, month, year) values(1, 2, 2, 1983);
insert into IssueDate(id, day, month, year) values(2, 27, 1, -983);
insert into IssueDate(id, year) values(3, -1500);
insert into IssueDate(id, day, month, year) values(4, 31, 3, 1923);
insert into IssueDate(id, day, month, year) values(5, 9, 12, 1978);
insert into IssueDate(id, day, month, year) values(6, 27, 1, 1986);
insert into IssueDate(id, month, year) values(7, 3, 1991);

-- Materials

insert into Material(id, title, issueDate) values(1, 'Mathematics textbook for 9th grade', 1);
insert into Material(id, title, issueDate) values(2, 'Mathematics textbook for 8th grade', 2);
insert into Material(id, title, issueDate) values(3, 'Mathematics textbook for 7th grade', 3);
insert into Material(id, title, issueDate) values(4, 'Mathematics textbook for 6th grade', 4);
insert into Material(id, title, issueDate) values(5, 'Mathematics textbook for 5th grade', 5);
insert into Material(id, title) values(6, 'Mathematics textbook for 4th grade');
insert into Material(id, title, issueDate) values(7, 'Mathematics textbook for 3th grade', 6);
insert into Material(id, title, issueDate) values(8, 'The Capital', 7);

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

-- LanguageDescription

insert into LanguageString(id, lang, textValue, material) values (1, 'est', 'test description in estonian', 1);
insert into LanguageString(id, lang, textValue, material) values (2, 'rus', 'test description in russian', 2);
