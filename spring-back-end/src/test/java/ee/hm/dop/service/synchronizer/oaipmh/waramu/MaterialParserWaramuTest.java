package ee.hm.dop.service.synchronizer.oaipmh.waramu;

import com.google.common.collect.Sets;
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
import ee.hm.dop.service.author.AuthorService;
import ee.hm.dop.service.metadata.LanguageService;
import ee.hm.dop.service.metadata.ResourceTypeService;
import ee.hm.dop.service.metadata.TagService;
import ee.hm.dop.service.metadata.TargetGroupService;
import ee.hm.dop.service.metadata.TaxonService;
import ee.hm.dop.service.synchronizer.oaipmh.BaseParserTest;
import ee.hm.dop.service.synchronizer.oaipmh.ParseException;
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
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(EasyMockRunner.class)
public class MaterialParserWaramuTest extends BaseParserTest {

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

    @Test(expected = ParseException.class)
    public void parseXMLisNull() throws ParseException {
        materialParser.parse(null);
    }

    @Test(expected = ParseException.class)
    public void parseDocumentIsEmpty() throws ParseException {
        Document document = createMock(Document.class);
        materialParser.parse(document);
    }

    @Test
    public void parse() throws Exception {
        File fXmlFile = getResourceAsFile("oaipmh/waramu/parseWaramu.xml");

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        Language french = language(1L, "French");
        Language estonian = language(2L, "Estonian");
        Tag tag = tag(325L, "grammaire");
        ResourceType resourceType1 = resourceType(444L, "WEBSITE");
        ResourceType resourceType2 = resourceType(555L, "COURSE");
        Author author = author("Andrew", "Balaam");
        EducationalContext educationalContext1 = educationalContext("preschoolEducation");
        EducationalContext educationalContext2 = educationalContext("basicEducation");
        EducationalContext educationalContext3 = educationalContext("secondaryEducation");
        EducationalContext educationalContext4 = educationalContext("vocationalEducation");
        Domain domain1 = domain(educationalContext2, "Foreign_language");
        Domain domain5 = domain(educationalContext2, "Cross-curricular_themes");
        Domain domain6 = domain(educationalContext2, "Key_competences");
        educationalContext2.setDomains(Sets.newHashSet(domain1, domain5, domain6));
        Domain domain2 = domain(educationalContext1, "Me_and_the_environment");
        educationalContext1.setDomains(Sets.newHashSet(domain2));
        Domain domain3 = domain(educationalContext4, "Computer_science");
        educationalContext4.setDomains(Sets.newHashSet(domain3));
        Specialization specialization = specialization(domain3);
        domain3.setSpecializations(Sets.newHashSet(specialization));
        Domain domain4 = domain(educationalContext3, "Language_and_literature");
        educationalContext3.setDomains(Sets.newHashSet(domain4));
        Subject subject1 = subject(domain4, "Estonian");
        domain4.setSubjects(Sets.newHashSet(subject1));
        Subject subject2 = subject(domain1, "English");
        domain1.setSubjects(Sets.newHashSet(subject2));
        Subject subject3 = subject(domain5, "Lifelong_learning_and_career_planning");
        domain5.setSubjects(Sets.newHashSet(subject3));
        Subject subject4 = subject(domain5, "Cultural_and_value_competence");
        domain6.setSubjects(Sets.newHashSet(subject4));
        Topic topic1 = topic(subject1, "Basic_history");
        subject1.setTopics(Sets.newHashSet(topic1));
        Topic topic2 = topic(subject2, "Estonian_history");
        subject2.setTopics(Sets.newHashSet(topic2));
        Topic topic3 = topic(subject2, "Preschool_Topic1");
        domain2.setTopics(Sets.newHashSet(topic3));
        Module module = module(specialization);
        specialization.setModules(Sets.newHashSet(module));
        Topic topic4 = topic(module);
        module.setTopics(Sets.newHashSet(topic4));
        Subtopic subtopic1 = subTopic(topic3, "Subtopic_for_Preschool_Topic1");
        topic3.setSubtopics(Sets.newHashSet(subtopic1));
        Subtopic subtopic2 = subTopic(topic1, "Ajaarvamine");
        topic1.setSubtopics(Sets.newHashSet(subtopic2));
        Subtopic subtopic3 = subTopic(topic4, "Subtopic_for_Vocational_Education");
        topic4.setSubtopics(Sets.newHashSet(subtopic3));
        Subtopic subtopic4 = subTopic(topic2, "Ajalooallikad");
        topic2.setSubtopics(Sets.newHashSet(subtopic4));

        expect(languageService.getLanguage("fr")).andReturn(french).times(2);
        expect(languageService.getLanguage("et")).andReturn(estonian).times(2);
        expect(languageService.getLanguage("fren")).andReturn(french);
        expect(tagService.getTagByName("grammaire")).andReturn(tag);
        expect(resourceTypeService.getResourceTypeByName(newArrayList(resourceType1.getName(), resourceType2.getName())))
                .andReturn(newArrayList(resourceType1, resourceType2));
        expect(authorService.getAuthorByFullName(author.getName(), author.getSurname())).andReturn(author);


        // first taxon
        expect(taxonService.getTaxonByEstCoreName(educationalContext1.getName(), EducationalContext.class)).andReturn(educationalContext1).anyTimes();
        expect(taxonService.getTaxonsByEstCoreName("Mina ja keskkond", Domain.class)).andReturn(newArrayList(domain2));
        expect(taxonService.getTaxonsByEstCoreName("Preschool Topic1", Topic.class)).andReturn(newArrayList(topic3));
        expect(taxonService.getTaxonsByEstCoreName("Subtopic for Preschool Topic1", Subtopic.class)).andReturn(newArrayList(subtopic1));

        // second taxon
        expect(taxonService.getTaxonByEstCoreName(educationalContext3.getName(), EducationalContext.class)).andReturn(educationalContext3).anyTimes();
        expect(taxonService.getTaxonsByEstCoreName("Language and literature", Domain.class)).andReturn(newArrayList(domain4));
        expect(taxonService.getTaxonsByEstCoreName(subject1.getName(), Subject.class)).andReturn(newArrayList(subject1));
        expect(taxonService.getTaxonsByEstCoreName("Ajaloo algõpetus", Topic.class)).andReturn(newArrayList(topic1));
        expect(taxonService.getTaxonsByEstCoreName("Ajaarvamine", Subtopic.class)).andReturn(newArrayList(subtopic2));

        // third taxon
        expect(taxonService.getTaxonByEstCoreName(educationalContext4.getName(), EducationalContext.class)).andReturn(
                educationalContext4).anyTimes();
        expect(taxonService.getTaxonsByEstCoreName("Computer Science", Domain.class)).andReturn(newArrayList(domain3));
        expect(taxonService.getTaxonsByEstCoreName("Computers and Networks", Specialization.class)).andReturn(newArrayList(specialization));
        expect(taxonService.getTaxonsByEstCoreName("Majanduse alused", Module.class)).andReturn(newArrayList(module));
        expect(taxonService.getTaxonsByEstCoreName("Vocational Education Topic1", Topic.class)).andReturn(newArrayList(topic4));
        expect(taxonService.getTaxonsByEstCoreName("Subtopic for Vocational Education", Subtopic.class)).andReturn(newArrayList(subtopic3));

        // fourth taxon
        expect(taxonService.getTaxonByEstCoreName(educationalContext2.getName(), EducationalContext.class)).andReturn(
                educationalContext2).anyTimes();
        expect(taxonService.getTaxonsByEstCoreName("Foreign language", Domain.class)).andReturn(newArrayList(domain1));
        expect(taxonService.getTaxonsByEstCoreName(subject2.getName(), Subject.class)).andReturn(newArrayList(subject2));
        expect(taxonService.getTaxonsByEstCoreName("Eesti ajalugu", Topic.class)).andReturn(newArrayList(topic2));
        expect(taxonService.getTaxonsByEstCoreName("Ajalooallikad", Subtopic.class)).andReturn(newArrayList(subtopic4));

        LanguageString title1 = languageString(french, "Subjonctif");
        LanguageString title2 = languageString(estonian, "Les exercices du subjonctif.");

        List<LanguageString> titles = newArrayList(title1, title2);
        LanguageString description1 = languageString(french, "Exercice a completer");
        LanguageString description2 = languageString(estonian, "Veebipõhised harjutused kahtleva kõneviisi kohta.");
        List<LanguageString> descriptions = newArrayList(description1, description2);
        List<Tag> tags = newArrayList(tag);
        List<ResourceType> resourceTypes = newArrayList(resourceType1, resourceType2);
        List<Author> authors = newArrayList(author);

        expect(targetGroupService.getTargetGroupsByAge(13, 15)).andReturn(targetGroup6to9());


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
        //todo time
        //assertNotNull(material.getIssueDate());
        assertEquals(4, material.getTaxons().size());
    }

    @Test
    public void parseWithNewAuthor() throws Exception {
        File fXmlFile = getResourceAsFile("oaipmh/waramu/parseNewAuthor.xml");

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        Language french = language(1L, "French");
        Language estonian = language(2L, "Estonian");
        Author author = author("Author", "Supernew");

        expect(languageService.getLanguage("fr")).andReturn(french).times(2);
        expect(languageService.getLanguage("et")).andReturn(estonian).times(2);
        expect(languageService.getLanguage("fren")).andReturn(french);
        expect(authorService.getAuthorByFullName(author.getName(), author.getSurname())).andReturn(null);
        expect(authorService.createAuthor(author.getName(), author.getSurname())).andReturn(author);
        List<Author> authors = newArrayList(author);

        expect(targetGroupService.getTargetGroupsByAge(13, 15)).andReturn(targetGroup6to9());

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

        Language french = language(1L, "French");
        Language estonian = language(2L, "Estonian");

        expect(languageService.getLanguage("fr")).andReturn(french).times(2);
        expect(languageService.getLanguage("et")).andReturn(estonian).times(2);
        expect(languageService.getLanguage("fren")).andReturn(french);

        expect(targetGroupService.getTargetGroupsByAge(13, 15))
                .andReturn(targetGroup6to9());


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

    private List<TargetGroup> targetGroup6to9() {
        TargetGroup targetGroupGrade6 = targetGroup(TargetGroupEnum.GRADE6);
        TargetGroup targetGroupGrade7 = targetGroup(TargetGroupEnum.GRADE7);
        TargetGroup targetGroupGrade8 = targetGroup(TargetGroupEnum.GRADE8);
        TargetGroup targetGroupGrade9 = targetGroup(TargetGroupEnum.GRADE9);
        return newArrayList(targetGroupGrade6, targetGroupGrade7, targetGroupGrade8, targetGroupGrade9);
    }
}
