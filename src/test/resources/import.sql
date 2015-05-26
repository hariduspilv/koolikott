-- Materials

insert into Material(id, title) values(1, 'Mathematics textbook for 9th grade');
insert into Material(id, title) values(2, 'Mathematics textbook for 8th grade');
insert into Material(id, title) values(3, 'Mathematics textbook for 7th grade');
insert into Material(id, title) values(4, 'Mathematics textbook for 6th grade');
insert into Material(id, title) values(5, 'Mathematics textbook for 5th grade');
insert into Material(id, title) values(6, 'Mathematics textbook for 4th grade');
insert into Material(id, title) values(7, 'Mathematics textbook for 3th grade');
insert into Material(id, title) values(8, 'The Capital');

insert into Author(id, name, surname) values(1, 'Isaac', 'Newton');
insert into Author(id, name, surname) values(2, 'Karl', 'Marx');
insert into Author(id, name, surname) values(3, 'Leonardo', 'Fibonacci');

insert into Material_Author(material, author) values(1, 1);
insert into Material_Author(material, author) values(2, 1);
insert into Material_Author(material, author) values(2, 3);
insert into Material_Author(material, author) values(3, 1);
insert into Material_Author(material, author) values(4, 1);
insert into Material_Author(material, author) values(5, 3);
insert into Material_Author(material, author) values(6, 3);
insert into Material_Author(material, author) values(7, 3);
insert into Material_Author(material, author) values(8, 2);