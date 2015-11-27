package ee.hm.dop.oaipmh.estcore;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;

import ee.hm.dop.model.Author;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.LanguageString;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.ResourceType;
import ee.hm.dop.model.Tag;
import ee.hm.dop.model.taxon.Domain;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Subject;
import ee.hm.dop.oaipmh.ParseException;
import ee.hm.dop.service.AuthorService;
import ee.hm.dop.service.LanguageService;
import ee.hm.dop.service.ResourceTypeService;
import ee.hm.dop.service.TagService;
import ee.hm.dop.service.TaxonService;

/**
 * Created by mart on 6.11.15.
 */
@RunWith(EasyMockRunner.class)
public class MaterialParserEstCoreTest {

    @TestSubject
    private MaterialParserEstCore materialParser = new MaterialParserEstCore();

    @Mock
    private LanguageService languageService;

    @Mock
    private AuthorService authorService;

    @Mock
    private TagService tagService;

    @Mock
    private ResourceTypeService resourceTypeService;

    @Mock
    private TaxonService taxonService;

    @Test(expected = ee.hm.dop.oaipmh.ParseException.class)
    public void parseXMLisNull() throws ParseException {
        materialParser.parse(null);
    }

    @Test(expected = ee.hm.dop.oaipmh.ParseException.class)
    public void parseDocumentIsEmpty() throws ParseException {
        Document document = createMock(Document.class);
        materialParser.parse(document);
    }

    @Test
    public void parse() throws Exception {
        File fXmlFile = getResourceAsFile("oaipmh/estcore/parseEstcore.xml");

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        Language english = new Language();
        english.setId(1L);
        english.setName("English");

        Language estonian = new Language();
        estonian.setId(2L);
        estonian.setName("Estonian");

        Author author1 = new Author();
        author1.setName("Jonathan");
        author1.setSurname("Doe");

        Author author2 = new Author();
        author2.setName("Andrew");
        author2.setSurname("Balaam");

        LanguageString description1 = new LanguageString();
        description1.setLanguage(english);
        description1.setText("description 1");

        LanguageString description2 = new LanguageString();
        description2.setLanguage(estonian);
        description2.setText("description 2");

        Tag tag1 = new Tag();
        tag1.setId(325L);
        tag1.setName("tag1");
        Tag tag2 = new Tag();
        tag2.setId(326L);
        tag2.setName("tag2");

        ResourceType resourceType1 = new ResourceType();
        resourceType1.setId(448L);
        resourceType1.setName("AUDIO");

        ResourceType resourceType2 = new ResourceType();
        resourceType2.setId(559L);
        resourceType2.setName("VIDEO");

        EducationalContext educationalContext1 = new EducationalContext();
        educationalContext1.setName("PRESCHOOLEDUCATION");

        EducationalContext educationalContext2 = new EducationalContext();
        educationalContext2.setName("BASICEDUCATION");

        EducationalContext educationalContext3 = new EducationalContext();
        educationalContext3.setName("SECONDARYEDUCATION");

        EducationalContext educationalContext4 = new EducationalContext();
        educationalContext4.setName("VOCATIONALEDUCATION");

        Domain domain1 = new Domain();
        domain1.setName("Me_and_the_environment");
        domain1.setEducationalContext(educationalContext2);
        Set<Domain> domains = new HashSet<>();
        domains.add(domain1);
        educationalContext2.setDomains(domains);

        Domain domain2 = new Domain();
        domain2.setName("Matemaatika");
        domain2.setEducationalContext(educationalContext1);
        domains = new HashSet<>();
        domains.add(domain2);
        educationalContext1.setDomains(domains);

        Domain domain3 = new Domain();
        domain3.setName("Computer Science");
        domain3.setEducationalContext(educationalContext4);
        domains = new HashSet<>();
        domains.add(domain3);
        educationalContext4.setDomains(domains);

        Domain domain4 = new Domain();
        domain4.setName("Language_and_literature");
        domain4.setEducationalContext(educationalContext3);
        domains = new HashSet<>();
        domains.add(domain4);
        educationalContext3.setDomains(domains);

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

        expect(languageService.getLanguage("en")).andReturn(english).times(3);
        expect(languageService.getLanguage("et")).andReturn(estonian).times(2);
        expect(authorService.getAuthorByFullName(author1.getName(), author1.getSurname())).andReturn(author1);
        expect(authorService.getAuthorByFullName(author2.getName(), author2.getSurname())).andReturn(author2);
        expect(tagService.getTagByName(tag1.getName())).andReturn(tag1);
        expect(tagService.getTagByName(tag2.getName())).andReturn(tag2);
        expect(resourceTypeService.getResourceTypeByName(resourceType1.getName())).andReturn(resourceType1);
        expect(resourceTypeService.getResourceTypeByName(resourceType2.getName())).andReturn(resourceType2);
        expect(taxonService.getTaxonByEstCoreName(educationalContext1.getName(), EducationalContext.class)).andReturn(
                educationalContext1).anyTimes();
        expect(taxonService.getTaxonByEstCoreName(domain2.getName(), Domain.class)).andReturn(domain2);
        expect(taxonService.getTaxonByEstCoreName(educationalContext3.getName(), EducationalContext.class)).andReturn(
                educationalContext3).anyTimes();
        expect(taxonService.getTaxonByEstCoreName("Language and literature", Domain.class)).andReturn(domain4);
        expect(taxonService.getTaxonByEstCoreName(subject1.getName(), Subject.class)).andReturn(subject1);
        expect(taxonService.getTaxonByEstCoreName(educationalContext4.getName(), EducationalContext.class)).andReturn(
                educationalContext4).anyTimes();
        expect(taxonService.getTaxonByEstCoreName(domain3.getName(), Domain.class)).andReturn(domain3);
        expect(taxonService.getTaxonByEstCoreName(educationalContext2.getName(), EducationalContext.class)).andReturn(
                educationalContext2).anyTimes();
        expect(taxonService.getTaxonByEstCoreName("Foreign language", Domain.class)).andReturn(domain1);
        expect(taxonService.getTaxonByEstCoreName(subject2.getName(), Subject.class)).andReturn(subject2);

        LanguageString title1 = new LanguageString();
        title1.setLanguage(english);
        title1.setText("first title");

        LanguageString title2 = new LanguageString();
        title2.setLanguage(estonian);
        title2.setText("teine pealkiri");

        List<LanguageString> titles = new ArrayList<>();
        titles.add(title1);
        titles.add(title2);

        List<Author> authors = new ArrayList<>();
        authors.add(author1);
        authors.add(author2);

        List<LanguageString> descriptions = new ArrayList<>();
        descriptions.add(description1);
        descriptions.add(description2);

        List<Tag> tags = new ArrayList<>();
        tags.add(tag1);
        tags.add(tag2);

        List<ResourceType> resourceTypes = new ArrayList<>();
        resourceTypes.add(resourceType1);
        resourceTypes.add(resourceType2);

        replay(languageService, authorService, tagService, resourceTypeService, taxonService);

        Document doc = dBuilder.parse(fXmlFile);
        Material material = materialParser.parse(doc);

        verify(languageService, authorService, tagService, resourceTypeService, taxonService);

        assertEquals(titles, material.getTitles());
        assertEquals("https://oxygen.netgroupdigital.com/rest/repoMaterialSource", material.getSource());
        assertEquals(english, material.getLanguage());
        assertEquals(authors, material.getAuthors());
        assertEquals(descriptions, material.getDescriptions());
        assertEquals(tags, material.getTags());
        assertEquals(resourceTypes, material.getResourceTypes());
        assertEquals(4, material.getTaxons().size());
    }

    private File getResourceAsFile(String resourcePath) throws URISyntaxException {
        URI resource = getClass().getClassLoader().getResource(resourcePath).toURI();
        return new File(resource);
    }
}
