SET foreign_key_checks = 0;

insert into TaxonPosition(taxon, educationalContext, domain, subject, topic, subtopic, module, specialization)
SELECT t.id  as taxon,
       Ed.id as educationalContext,
       null  as domain,
       null  as subject,
       null  as topic,
       null  as subtopic,
       null  as module,
       null  as specialization
FROM Taxon t
       join EducationalContext Ed on t.id = Ed.id;

insert into TaxonPosition(taxon, educationalContext, domain, subject, topic, subtopic, module, specialization)
SELECT t.id                 as taxon,
       D.educationalContext as educationalContext,
       D.id                 as domain,
       null                 as subject,
       null                 as topic,
       null                 as subtopic,
       null                 as module,
       null                 as specialization
FROM Taxon t
       join Domain D on S.domain = D.id;

insert into TaxonPosition(taxon, educationalContext, domain, subject, topic, subtopic, module, specialization)
SELECT t.id                 as taxon,
       D.educationalContext as educationalContext,
       D.id                 as domain,
       S.id                 as subject,
       null                 as topic,
       null                 as subtopic,
       null                 as module,
       null                 as specialization
FROM Taxon t
       join Subject S on t.id = S.id
       join Domain D on S.domain = D.id;

insert into TaxonPosition(taxon, educationalContext, domain, subject, topic, subtopic, module, specialization)
SELECT t.id                 as taxon,
       D.educationalContext as educationalContext,
       D.id                 as domain,
       S.id                 as subject,
       topic.id             as topic,
       null                 as subtopic,
       null                 as module,
       null                 as specialization
FROM Taxon t
       join Topic topic on t.id = topic.id
       join Subject S on topic.subject = S.id
       join Domain D on S.domain = D.id;

insert into TaxonPosition(taxon, educationalContext, domain, subject, topic, subtopic, module, specialization)
SELECT t.id                 as taxon,
       D.educationalContext as educationalContext,
       D.id                 as domain,
       null                 as subject,
       topic.id             as topic,
       null                 as subtopic,
       null                 as module,
       null                 as specialization
FROM Taxon t
       join Topic topic on t.id = topic.id
       join Domain D on topic.domain = D.id;

insert into TaxonPosition(taxon, educationalContext, domain, subject, topic, subtopic, module, specialization)
SELECT t.id                 as taxon,
       D.educationalContext as educationalContext,
       D.id                 as domain,
       subject.id           as subject,
       topic.id             as topic,
       S.id                 as subtopic,
       null                 as module,
       null                 as specialization
FROM Taxon t
       join Subtopic S on t.id = S.id
       join Topic topic on S.topic = topic.id
       join Subject subject on topic.subject = subject.id
       join Domain D on subject.domain = D.id;

insert into TaxonPosition(taxon, educationalContext, domain, subject, topic, subtopic, module, specialization)
SELECT t.id                 as taxon,
       D.educationalContext as educationalContext,
       D.id                 as domain,
       null                 as subject,
       topic.id             as topic,
       S.id                 as subtopic,
       null                 as module,
       null                 as specialization
FROM Taxon t
       join Subtopic S on t.id = S.id
       join Topic topic on S.topic = topic.id
       join Domain D on topic.domain = D.id;

SET foreign_key_checks = 1;