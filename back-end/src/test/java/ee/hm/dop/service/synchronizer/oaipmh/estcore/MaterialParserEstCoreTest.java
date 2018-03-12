package ee.hm.dop.service.synchronizer.oaipmh.estcore;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.TargetGroupEnum;
import ee.hm.dop.model.taxon.Domain;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Module;
import ee.hm.dop.model.taxon.Specialization;
import ee.hm.dop.model.taxon.Subject;
import ee.hm.dop.model.taxon.Subtopic;
import ee.hm.dop.model.taxon.Topic;
import ee.hm.dop.service.synchronizer.oaipmh.BaseParserTest;
import ee.hm.dop.service.useractions.PeerReviewService;
import ee.hm.dop.service.author.AuthorService;
import ee.hm.dop.service.author.PublisherService;
import ee.hm.dop.service.metadata.*;
import ee.hm.dop.service.synchronizer.oaipmh.ParseException;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;

@RunWith(EasyMockRunner.class)
public class MaterialParserEstCoreTest extends BaseParserTest{

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
    @Mock
    private PublisherService publisherService;
    @Mock
    private CrossCurricularThemeService crossCurricularThemeService;
    @Mock
    private KeyCompetenceService keyCompetenceService;
    @Mock
    private PeerReviewService peerReviewService;
    @Mock
    private TargetGroupService targetGroupService;

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
        File fXmlFile = getResourceAsFile("oaipmh/estcore/parseEstcore.xml");

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        Language english = language(1L, "English");
        Language estonian = language(2L, "Estonian");
        Author author1 = author("Jonathan", "Doe");
        Author author2 = author("Andrew", "Balaam");
        LanguageString description1 = languageString(english, "description 1");
        LanguageString description2 = languageString(estonian, "description 2");
        Tag tag1 = tag(325L, "tag1");
        Tag tag2 = tag(326L, "tag2");
        ResourceType resourceType1 = resourceType(448L, "AUDIO");
        ResourceType resourceType2 = resourceType(559L, "VIDEO");
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
        Publisher publisher = publisher();
        CrossCurricularTheme crossCurricularTheme = crossCurricularTheme();
        KeyCompetence keyCompetence = keyCompetence();
        PeerReview peerReview = peerReview();
        List<PeerReview> peerReviews = Lists.newArrayList(peerReview);

        expect(languageService.getLanguage("en")).andReturn(english).times(3);
        expect(languageService.getLanguage("et")).andReturn(estonian).times(2);
        expect(authorService.getAuthorByFullName(author1.getName(), author1.getSurname())).andReturn(author1);
        expect(authorService.getAuthorByFullName(author2.getName(), author2.getSurname())).andReturn(author2);
        expect(tagService.getTagByName(tag1.getName())).andReturn(tag1);
        expect(tagService.getTagByName(tag2.getName())).andReturn(tag2);
        expect(resourceTypeService.getResourceTypeByName(Lists.newArrayList(resourceType1.getName(), resourceType2.getName())))
                .andReturn(Lists.newArrayList(resourceType1, resourceType2));
        expect(publisherService.getPublisherByName(publisher.getName())).andReturn(null);
        expect(publisherService.createPublisher(publisher.getName(), publisher.getWebsite())).andReturn(publisher);

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
        expect(taxonService.getTaxonByEstCoreName(educationalContext4.getName(), EducationalContext.class)).andReturn(educationalContext4).anyTimes();
        expect(taxonService.getTaxonByEstCoreName("Computer Science", Domain.class)).andReturn(domain3);
        expect(taxonService.getTaxonByEstCoreName("Computers and Networks", Specialization.class)).andReturn(specialization);
        expect(taxonService.getTaxonByEstCoreName("Majanduse alused", Module.class)).andReturn(module);
        expect(taxonService.getTaxonByEstCoreName("Vocational Education Topic1", Topic.class)).andReturn(topic4);
        expect(taxonService.getTaxonByEstCoreName("Subtopic for Vocational Education", Subtopic.class)).andReturn(
                subtopic3);

        // fourth taxon
        expect(taxonService.getTaxonByEstCoreName(educationalContext2.getName(), EducationalContext.class)).andReturn(educationalContext2).anyTimes();
        expect(taxonService.getTaxonByEstCoreName("Foreign language", Domain.class)).andReturn(domain1);
        expect(taxonService.getTaxonByEstCoreName(subject2.getName(), Subject.class)).andReturn(subject2);
        expect(taxonService.getTaxonByEstCoreName("Eesti ajalugu", Topic.class)).andReturn(topic2);
        expect(taxonService.getTaxonByEstCoreName("Ajalooallikad", Subtopic.class)).andReturn(subtopic4);

        // special education taxon
        expect(taxonService.getTaxonByEstCoreName("SPECIALEDUCATION", EducationalContext.class)).andReturn(null);

        // Cross-curricular themes
        expect(crossCurricularThemeService.getThemeByName(Lists.newArrayList(crossCurricularTheme.getName()))).andReturn(Lists.newArrayList(crossCurricularTheme));

        // Key competence
        expect(keyCompetenceService.findKeyCompetenceByName(Lists.newArrayList(keyCompetence.getName()))).andReturn(Lists.newArrayList(keyCompetence));

        // Peer reviews
        expect(peerReviewService.getPeerReviewByURL(Lists.newArrayList(peerReview.getUrl().toUpperCase()))).andReturn(Lists.newArrayList(peerReview));

        TargetGroup targetGroupSixSeven = targetGroup(TargetGroupEnum.SIX_SEVEN);
        TargetGroup targetGroupGrade1 = targetGroup(TargetGroupEnum.GRADE1);
        TargetGroup targetGroupGrade3 = targetGroup(TargetGroupEnum.GRADE3);
        TargetGroup targetGroupGrade4 = targetGroup(TargetGroupEnum.GRADE4);
        TargetGroup targetGroupGrade5 = targetGroup(TargetGroupEnum.GRADE5);
        TargetGroup targetGroupGrade6 = targetGroup(TargetGroupEnum.GRADE6);
        TargetGroup targetGroupGrade7 = targetGroup(TargetGroupEnum.GRADE7);
        TargetGroup targetGroupGrade8 = targetGroup(TargetGroupEnum.GRADE8);
        TargetGroup targetGroupGrade9 = targetGroup(TargetGroupEnum.GRADE9);
        TargetGroup targetGroupGymnasium = targetGroup(TargetGroupEnum.GYMNASIUM);

        expect(targetGroupService.getTargetGroupsByAge(6, 7))
                .andReturn(Lists.newArrayList(targetGroupSixSeven, targetGroupGrade1));
        expect(targetGroupService.getTargetGroupsByAge(10, 13))
                .andReturn(Lists.newArrayList(targetGroupGrade3, targetGroupGrade4, targetGroupGrade5, targetGroupGrade6, targetGroupGrade7));
        expect(targetGroupService.getTargetGroupsByAge(14, 25))
                .andReturn(Lists.newArrayList(targetGroupGrade7, targetGroupGrade8, targetGroupGrade9, targetGroupGymnasium));

        LanguageString title1 = languageString(english, "first title");
        LanguageString title2 = languageString(estonian, "teine pealkiri");

        List<LanguageString> titles = Lists.newArrayList(title1, title2);
        List<Author> authors = Lists.newArrayList(author1, author2);
        List<LanguageString> descriptions = Lists.newArrayList(description1, description2);
        List<Tag> tags = Lists.newArrayList(tag1, tag2);
        List<ResourceType> resourceTypes = Lists.newArrayList(resourceType1, resourceType2);

        replay(languageService, authorService, tagService, resourceTypeService, taxonService, publisherService,
                crossCurricularThemeService, keyCompetenceService, peerReviewService, targetGroupService);

        Document doc = dBuilder.parse(fXmlFile);
        Material material = materialParser.parse(doc);

        verify(languageService, authorService, tagService, resourceTypeService, taxonService, publisherService,
                crossCurricularThemeService, keyCompetenceService, peerReviewService, targetGroupService);

        assertEquals(titles, material.getTitles());
        assertEquals("https://oxygen.netgroupdigital.com/rest/repoMaterialSource", material.getSource());
        assertEquals(english, material.getLanguage());
        assertEquals(authors, material.getAuthors());
        assertEquals(descriptions, material.getDescriptions());
        assertEquals(tags, material.getTags());
        assertEquals(resourceTypes, material.getResourceTypes());
        assertEquals(4, material.getTaxons().size());
        assertEquals(10, material.getTargetGroups().size());
        assertNotNull(material.getPicture());
        assertEquals(1, material.getPublishers().size());
        assertNotNull(material.getIssueDate());
        assertTrue(material.isSpecialEducation());
        assertEquals(1, material.getCrossCurricularThemes().size());
        assertEquals(1, material.getKeyCompetences().size());
        assertEquals(peerReviews, material.getPeerReviews());
    }

}
