package ee.hm.dop.service.synchronizer.oaipmh.waramu;

import ee.hm.dop.model.Author;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.LanguageString;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.ResourceType;
import ee.hm.dop.model.Tag;
import ee.hm.dop.model.TargetGroup;
import ee.hm.dop.model.enums.TargetGroupEnum;
import ee.hm.dop.model.taxon.Domain;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Module;
import ee.hm.dop.model.taxon.Specialization;
import ee.hm.dop.model.taxon.Subject;
import ee.hm.dop.model.taxon.Subtopic;
import ee.hm.dop.model.taxon.Topic;
import ee.hm.dop.service.synchronizer.oaipmh.ParseException;
import ee.hm.dop.service.AuthorService;
import ee.hm.dop.service.LanguageService;
import ee.hm.dop.service.ResourceTypeService;
import ee.hm.dop.service.TagService;
import ee.hm.dop.service.TargetGroupService;
import ee.hm.dop.service.TaxonService;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

@RunWith(EasyMockRunner.class)
public class MaterialParserWaramuTest {

    @TestSubject
    private MaterialParserWaramu materialParser = new MaterialParserWaramu();

    @Mock
    private LanguageService languageService;

    @Mock
    private TagService tagService;

    @Mock
    private ResourceTypeService resourceTypeService;

    @Mock
    private TaxonService taxonService;

    @Mock
    private AuthorService authorService;

    @Mock
    private TargetGroupService targetGroupService;

    private Language language = new Language();

    @Test(expected = ee.hm.dop.service.synchronizer.oaipmh.ParseException.class)
    public void parseXMLisNull() throws ParseException {
        materialParser.parse(null);
    }

    @Test(expected = ee.hm.dop.service.synchronizer.oaipmh.ParseException.class)
    public void parseDocumentIsEmpty() throws ParseException {
        Document document = createMock(Document.class);
        materialParser.parse(document);
    }

    @Test
    public void parse() throws Exception {
        File fXmlFile = getResourceAsFile("oaipmh/waramu/parseWaramu.xml");

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        Language french = new Language();
        french.setId(1L);
        french.setName("French");

        Language estonian = new Language();
        estonian.setId(2L);
        estonian.setName("Estonian");

        Tag tag = new Tag();
        tag.setId(325L);
        tag.setName("grammaire");

        ResourceType resourceType1 = new ResourceType();
        resourceType1.setId(444L);
        resourceType1.setName("WEBSITE");

        ResourceType resourceType2 = new ResourceType();
        resourceType2.setId(555L);
        resourceType2.setName("COURSE");

        Author author = new Author();
        author.setName("Andrew");
        author.setSurname("Balaam");


        EducationalContext educationalContext1 = new EducationalContext();
        educationalContext1.setName("preschoolEducation");

        EducationalContext educationalContext2 = new EducationalContext();
        educationalContext2.setName("basicEducation");

        EducationalContext educationalContext3 = new EducationalContext();
        educationalContext3.setName("secondaryEducation");

        EducationalContext educationalContext4 = new EducationalContext();
        educationalContext4.setName("vocationalEducation");

        Domain domain1 = new Domain();
        domain1.setName("Foreign_language");
        domain1.setEducationalContext(educationalContext2);
        Set<Domain> domains = new HashSet<>();
        domains.add(domain1);
        educationalContext2.setDomains(domains);

        Domain domain2 = new Domain();
        domain2.setName("Me_and_the_environment");
        domain2.setEducationalContext(educationalContext1);
        domains = new HashSet<>();
        domains.add(domain2);
        educationalContext1.setDomains(domains);

        Domain domain3 = new Domain();
        domain3.setName("Computer_science");
        domain3.setEducationalContext(educationalContext4);
        domains = new HashSet<>();
        domains.add(domain3);
        educationalContext4.setDomains(domains);

        Specialization specialization = new Specialization();
        specialization.setName("Computers_and_Networks");
        specialization.setDomain(domain3);
        Set<Specialization> specializations = new HashSet<>();
        specializations.add(specialization);
        domain3.setSpecializations(specializations);

        Domain domain4 = new Domain();
        domain4.setName("Language_and_literature");
        domain4.setEducationalContext(educationalContext3);
        domains = new HashSet<>();
        domains.add(domain4);
        educationalContext3.setDomains(domains);

        Domain domain5 = new Domain();
        domain5.setName("Cross-curricular_themes");
        domain5.setEducationalContext(educationalContext2);
        domains = educationalContext2.getDomains();
        domains.add(domain5);
        educationalContext2.setDomains(domains);

        Domain domain6 = new Domain();
        domain6.setName("Key_competences");
        domain6.setEducationalContext(educationalContext2);
        domains = educationalContext2.getDomains();
        domains.add(domain6);
        educationalContext2.setDomains(domains);

        Subject subject1 = new Subject();
        subject1.setName("Estonian");
        subject1.setDomain(domain4);
        Set<Subject> subjects = new HashSet<>();
        subjects.add(subject1);
        domain4.setSubjects(subjects);

        Subject subject2 = new Subject();
        subject2.setName("English");
        subject2.setDomain(domain1);
        subjects = new HashSet<>();
        subjects.add(subject2);
        domain1.setSubjects(subjects);

        Subject subject3 = new Subject();
        subject3.setName("Lifelong_learning_and_career_planning");
        subject3.setDomain(domain5);
        subjects = new HashSet<>();
        subjects.add(subject3);
        domain5.setSubjects(subjects);

        Subject subject4 = new Subject();
        subject4.setName("Cultural_and_value_competence");
        subject4.setDomain(domain5);
        subjects = new HashSet<>();
        subjects.add(subject4);
        domain6.setSubjects(subjects);

        Topic topic1 = new Topic();
        topic1.setName("Basic_history");
        topic1.setSubject(subject1);
        Set<Topic> topics = new HashSet<>();
        topics.add(topic1);
        subject1.setTopics(topics);

        Topic topic2 = new Topic();
        topic2.setName("Estonian_history");
        topic2.setSubject(subject2);
        topics = new HashSet<>();
        topics.add(topic2);
        subject2.setTopics(topics);

        Topic topic3 = new Topic();
        topic3.setName("Preschool_Topic1");
        topic3.setSubject(subject2);
        topics = new HashSet<>();
        topics.add(topic3);
        domain2.setTopics(topics);

        Module module = new Module();
        module.setName("Majanduse_alused");
        module.setSpecialization(specialization);
        Set<Module> modules = new HashSet<>();
        modules.add(module);
        specialization.setModules(modules);

        Topic topic4 = new Topic();
        topic4.setName("Vocational_Education_Topic1");
        topic4.setModule(module);
        topics = new HashSet<>();
        topics.add(topic4);
        module.setTopics(topics);

        Subtopic subtopic1 = new Subtopic();
        subtopic1.setName("Subtopic_for_Preschool_Topic1");
        subtopic1.setTopic(topic3);
        Set<Subtopic> subtopics = new HashSet<>();
        subtopics.add(subtopic1);
        topic3.setSubtopics(subtopics);

        Subtopic subtopic2 = new Subtopic();
        subtopic2.setName("Ajaarvamine");
        subtopic2.setTopic(topic1);
        subtopics = new HashSet<>();
        subtopics.add(subtopic2);
        topic1.setSubtopics(subtopics);

        Subtopic subtopic3 = new Subtopic();
        subtopic3.setName("Subtopic_for_Vocational_Education");
        subtopic3.setTopic(topic4);
        subtopics = new HashSet<>();
        subtopics.add(subtopic3);
        topic4.setSubtopics(subtopics);

        Subtopic subtopic4 = new Subtopic();
        subtopic4.setName("Ajalooallikad");
        subtopic4.setTopic(topic2);
        subtopics = new HashSet<>();
        subtopics.add(subtopic4);
        topic2.setSubtopics(subtopics);

        expect(languageService.getLanguage("fr")).andReturn(french).times(2);
        expect(languageService.getLanguage("et")).andReturn(estonian).times(2);
        expect(languageService.getLanguage("fren")).andReturn(french);
        expect(tagService.getTagByName("grammaire")).andReturn(tag);
        expect(resourceTypeService.getResourceTypeByName(resourceType1.getName())).andReturn(resourceType1);
        expect(resourceTypeService.getResourceTypeByName(resourceType2.getName())).andReturn(resourceType2);
        expect(authorService.getAuthorByFullName(author.getName(), author.getSurname())).andReturn(author);


        // first taxon
        expect(taxonService.getTaxonByEstCoreName(educationalContext1.getName(), EducationalContext.class)).andReturn(
                educationalContext1).anyTimes();
        expect(taxonService.getTaxonByEstCoreName("Mina ja keskkond", Domain.class)).andReturn(domain2);
        expect(taxonService.getTaxonByEstCoreName("Preschool Topic1", Topic.class)).andReturn(topic3);
        expect(taxonService.getTaxonByEstCoreName("Subtopic for Preschool Topic1", Subtopic.class))
                .andReturn(subtopic1);

        // second taxon
        expect(taxonService.getTaxonByEstCoreName(educationalContext3.getName(), EducationalContext.class)).andReturn(
                educationalContext3).anyTimes();
        expect(taxonService.getTaxonByEstCoreName("Language and literature", Domain.class)).andReturn(domain4);
        expect(taxonService.getTaxonByEstCoreName(subject1.getName(), Subject.class)).andReturn(subject1);
        expect(taxonService.getTaxonByEstCoreName("Ajaloo algõpetus", Topic.class)).andReturn(topic1);
        expect(taxonService.getTaxonByEstCoreName("Ajaarvamine", Subtopic.class)).andReturn(subtopic2);

        // third taxon
        expect(taxonService.getTaxonByEstCoreName(educationalContext4.getName(), EducationalContext.class)).andReturn(
                educationalContext4).anyTimes();
        expect(taxonService.getTaxonByEstCoreName("Computer Science", Domain.class)).andReturn(domain3);
        expect(taxonService.getTaxonByEstCoreName("Computers and Networks", Specialization.class)).andReturn(
                specialization);
        expect(taxonService.getTaxonByEstCoreName("Majanduse alused", Module.class)).andReturn(module);
        expect(taxonService.getTaxonByEstCoreName("Vocational Education Topic1", Topic.class)).andReturn(topic4);
        expect(taxonService.getTaxonByEstCoreName("Subtopic for Vocational Education", Subtopic.class)).andReturn(
                subtopic3);

        // fourth taxon
        expect(taxonService.getTaxonByEstCoreName(educationalContext2.getName(), EducationalContext.class)).andReturn(
                educationalContext2).anyTimes();
        expect(taxonService.getTaxonByEstCoreName("Foreign language", Domain.class)).andReturn(domain1);
        expect(taxonService.getTaxonByEstCoreName(subject2.getName(), Subject.class)).andReturn(subject2);
        expect(taxonService.getTaxonByEstCoreName("Eesti ajalugu", Topic.class)).andReturn(topic2);
        expect(taxonService.getTaxonByEstCoreName("Ajalooallikad", Subtopic.class)).andReturn(subtopic4);


        LanguageString title1 = new LanguageString();
        title1.setLanguage(french);
        title1.setText("Subjonctif");

        LanguageString title2 = new LanguageString();
        title2.setLanguage(estonian);
        title2.setText("Les exercices du subjonctif.");

        List<LanguageString> titles = new ArrayList<>();
        titles.add(title1);
        titles.add(title2);

        LanguageString description1 = new LanguageString();
        description1.setLanguage(french);
        description1.setText("Exercice a completer");

        LanguageString description2 = new LanguageString();
        description2.setLanguage(estonian);
        description2.setText("Veebipõhised harjutused kahtleva kõneviisi kohta.");

        List<LanguageString> descriptions = new ArrayList<>();
        descriptions.add(description1);
        descriptions.add(description2);

        List<Tag> tags = new ArrayList<>();
        tags.add(tag);

        List<ResourceType> resourceTypes = new ArrayList<>();
        resourceTypes.add(resourceType1);
        resourceTypes.add(resourceType2);

        List<Author> authors = new ArrayList<>();
        authors.add(author);

        TargetGroup targetGroupGrade6 = new TargetGroup();
        targetGroupGrade6.setName(TargetGroupEnum.GRADE6.name());

        TargetGroup targetGroupGrade7 = new TargetGroup();
        targetGroupGrade7.setName(TargetGroupEnum.GRADE7.name());

        TargetGroup targetGroupGrade8 = new TargetGroup();
        targetGroupGrade8.setName(TargetGroupEnum.GRADE8.name());

        TargetGroup targetGroupGrade9 = new TargetGroup();
        targetGroupGrade9.setName(TargetGroupEnum.GRADE9.name());

        expect(targetGroupService.getTargetGroupsByAge(13, 15))
                .andReturn(new HashSet<>(Arrays.asList(targetGroupGrade6, targetGroupGrade7, targetGroupGrade8, targetGroupGrade9)));


        replay(languageService, tagService, resourceTypeService, taxonService, authorService, targetGroupService);

        Document doc = dBuilder.parse(fXmlFile);
        Material material = materialParser.parse(doc);

        verify(languageService, tagService, resourceTypeService, taxonService, authorService, targetGroupService);

        assertEquals("oai:ait.opetaja.ee:437556e69c7ee410b3ff27ad3eaec360219c3990", material.getRepositoryIdentifier());
        assertEquals(titles, material.getTitles());
        assertEquals(descriptions, material.getDescriptions());
        assertEquals(tags, material.getTags());
        assertEquals(french, material.getLanguage());
        assertEquals("http://koolitaja.eenet.ee:57219/Waramu3Web/metadata?id=437556e69c7ee410b3ff27ad3eaec360219c3990",
                material.getSource());
        assertEquals(resourceTypes, material.getResourceTypes());
        assertEquals(authors, material.getAuthors());
        assertEquals(4, material.getTargetGroups().size());
        assertNotNull(material.getIssueDate());
        assertEquals(4, material.getTaxons().size());
    }

    @Test
    public void parseWithNewAuthor() throws Exception {
        File fXmlFile = getResourceAsFile("oaipmh/waramu/parseNewAuthor.xml");

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        Language french = new Language();
        french.setId(1L);
        french.setName("French");

        Language estonian = new Language();
        estonian.setId(2L);
        estonian.setName("Estonian");

        Author author = new Author();
        author.setName("Author");
        author.setSurname("Supernew");

        expect(languageService.getLanguage("fr")).andReturn(french).times(2);
        expect(languageService.getLanguage("et")).andReturn(estonian).times(2);
        expect(languageService.getLanguage("fren")).andReturn(french);
        expect(authorService.getAuthorByFullName(author.getName(), author.getSurname())).andReturn(null);
        expect(authorService.createAuthor(author.getName(), author.getSurname())).andReturn(author);
        List<Author> authors = new ArrayList<>();
        authors.add(author);

        TargetGroup targetGroupGrade6 = new TargetGroup();
        targetGroupGrade6.setName(TargetGroupEnum.GRADE6.name());

        TargetGroup targetGroupGrade7 = new TargetGroup();
        targetGroupGrade7.setName(TargetGroupEnum.GRADE7.name());

        TargetGroup targetGroupGrade8 = new TargetGroup();
        targetGroupGrade8.setName(TargetGroupEnum.GRADE8.name());

        TargetGroup targetGroupGrade9 = new TargetGroup();
        targetGroupGrade9.setName(TargetGroupEnum.GRADE9.name());

        expect(targetGroupService.getTargetGroupsByAge(13, 15))
                .andReturn(new HashSet<>(Arrays.asList(targetGroupGrade6, targetGroupGrade7, targetGroupGrade8, targetGroupGrade9)));

        replay(languageService, authorService, targetGroupService);

        Document doc = dBuilder.parse(fXmlFile);
        Material material = materialParser.parse(doc);

        verify(languageService, authorService, targetGroupService);

        assertEquals("oai:ait.opetaja.ee:437556e69c7ee410b3ff27ad3eaec360219c3990", material.getRepositoryIdentifier());
        assertEquals(french, material.getLanguage());
        assertEquals("http://koolitaja.eenet.ee:57219/Waramu3Web/metadata?id=437556e69c7ee410b3ff27ad3eaec360219c3990",
                material.getSource());
        assertEquals(authors, material.getAuthors());
    }

    @Test
    public void parseWithNoAuthorData() throws Exception {
        File fXmlFile = getResourceAsFile("oaipmh/waramu/parseNoAuthor.xml");

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        Language french = new Language();
        french.setId(1L);
        french.setName("French");

        Language estonian = new Language();
        estonian.setId(2L);
        estonian.setName("Estonian");

        expect(languageService.getLanguage("fr")).andReturn(french).times(2);
        expect(languageService.getLanguage("et")).andReturn(estonian).times(2);
        expect(languageService.getLanguage("fren")).andReturn(french);

        TargetGroup targetGroupGrade6 = new TargetGroup();
        targetGroupGrade6.setName(TargetGroupEnum.GRADE6.name());

        TargetGroup targetGroupGrade7 = new TargetGroup();
        targetGroupGrade7.setName(TargetGroupEnum.GRADE7.name());

        TargetGroup targetGroupGrade8 = new TargetGroup();
        targetGroupGrade8.setName(TargetGroupEnum.GRADE8.name());

        TargetGroup targetGroupGrade9 = new TargetGroup();
        targetGroupGrade9.setName(TargetGroupEnum.GRADE9.name());

        expect(targetGroupService.getTargetGroupsByAge(13, 15))
                .andReturn(new HashSet<>(Arrays.asList(targetGroupGrade6, targetGroupGrade7, targetGroupGrade8, targetGroupGrade9)));


        replay(languageService, authorService, targetGroupService);

        Document doc = dBuilder.parse(fXmlFile);
        Material material = materialParser.parse(doc);

        verify(languageService, authorService, targetGroupService);

        assertEquals("oai:ait.opetaja.ee:437556e69c7ee410b3ff27ad3eaec360219c3990", material.getRepositoryIdentifier());
        assertEquals(french, material.getLanguage());
        assertEquals("http://koolitaja.eenet.ee:57219/Waramu3Web/metadata?id=437556e69c7ee410b3ff27ad3eaec360219c3990",
                material.getSource());
    }

    @Test
    public void parseNullTitle() throws ParserConfigurationException, IOException, SAXException, URISyntaxException {
        String errorMessage = "Error in parsing Material title";

        URI resource = getClass().getClassLoader().getResource("oaipmh/waramu/parseNullTitle.xml").toURI();
        File fXmlFile = new File(resource);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        expect(languageService.getLanguage("fr")).andReturn(language).anyTimes();
        expect(languageService.getLanguage("et")).andReturn(language).anyTimes();

        replay(languageService);

        Document doc = dBuilder.parse(fXmlFile);

        try {
            materialParser.parse(doc);
            fail("Exception expected.");
        } catch (ParseException e) {
            assertEquals(errorMessage, e.getMessage());
        }

        verify(languageService);
    }

    @Test
    public void parseNullSource() throws ParserConfigurationException, IOException, SAXException, URISyntaxException {
        String errorMessage = "Error parsing document source.";

        URI resource = getClass().getClassLoader().getResource("oaipmh/waramu/parseNullSource.xml").toURI();
        File fXmlFile = new File(resource);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        expect(languageService.getLanguage("fr")).andReturn(language).anyTimes();
        expect(languageService.getLanguage("et")).andReturn(language).anyTimes();
        expect(languageService.getLanguage("fren")).andReturn(language);

        replay(languageService);

        Document doc = dBuilder.parse(fXmlFile);

        try {
            materialParser.parse(doc);
            fail("Exception expected.");
        } catch (ParseException e) {
            assertEquals(errorMessage, e.getMessage());
        }

        verify(languageService);
    }

    private File getResourceAsFile(String resourcePath) throws URISyntaxException {
        URI resource = getClass().getClassLoader().getResource(resourcePath).toURI();
        return new File(resource);
    }
}
