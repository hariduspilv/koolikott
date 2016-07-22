package ee.hm.dop.service;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ee.hm.dop.dao.LearningObjectDAO;
import ee.hm.dop.model.CrossCurricularTheme;
import ee.hm.dop.model.KeyCompetence;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.ResourceType;
import ee.hm.dop.model.Role;
import ee.hm.dop.model.SearchFilter;
import ee.hm.dop.model.SearchResult;
import ee.hm.dop.model.Searchable;
import ee.hm.dop.model.TargetGroup;
import ee.hm.dop.model.User;
import ee.hm.dop.model.solr.Document;
import ee.hm.dop.model.solr.Response;
import ee.hm.dop.model.solr.SearchResponse;
import ee.hm.dop.model.taxon.Domain;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Module;
import ee.hm.dop.model.taxon.Specialization;
import ee.hm.dop.model.taxon.Subject;
import ee.hm.dop.model.taxon.Subtopic;
import ee.hm.dop.model.taxon.Topic;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EasyMockRunner.class)
public class SearchServiceTest {

    @Mock
    private SearchEngineService searchEngineService;

    @Mock
    private LearningObjectDAO learningObjectDAO;

    @TestSubject
    private SearchService searchService = new SearchService();

    @Test
    public void search() {
        String query = "people";
        String tokenizedQuery = "(people) AND (visibility:\"public\" OR type:\"material\")";
        SearchFilter searchFilter = new SearchFilter();
        long start = 0;
        List<Searchable> searchables = Arrays.asList(createMaterial(7L), createMaterial(1L), createPortfolio(4L),
                createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchEmptyQueryAsAdmin() {
        User loggedInUser = new User();
        loggedInUser.setRole(Role.ADMIN);

        replayAll();

        SearchResult result = null;
        try {
            result = searchService.search("", 0, null, new SearchFilter(), loggedInUser);
            fail("Exception expected.");
        } catch (RuntimeException e) {
            assertEquals("No query string and filters present.", e.getMessage());
        }

        verifyAll();

        assertNull(result);
    }

    @Test
    public void searchEmptyQueryAndTaxonFilter() {
        String query = "";
        SearchFilter searchFilter = new SearchFilter();
        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setId(1L);
        educationalContext.setName("PRESCHOOL");
        searchFilter.setTaxon(educationalContext);
        String tokenizedQuery = "educational_context:\"preschool\" AND (visibility:\"public\" OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(3L), createMaterial(4L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchWithEmptyQueryAndTypeFilterAll() {
        String query = "";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setType("all");
        String tokenizedQuery = "(type:\"material\" OR type:\"portfolio\") AND (visibility:\"public\" OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchNullQueryAndNullFiltersAsAdmin() {
        User loggedInUser = new User();
        loggedInUser.setRole(Role.ADMIN);

        replayAll();

        SearchResult result = null;
        try {
            result = searchService.search(null, 0, null, new SearchFilter(), loggedInUser);
            fail("Exception expected.");
        } catch (RuntimeException e) {
            assertEquals("No query string and filters present.", e.getMessage());
        }

        verifyAll();

        assertNull(result);
    }

    @Test
    public void searchNullQueryAndTaxonFilter() {
        String query = null;
        SearchFilter searchFilter = new SearchFilter();
        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setId(2L);
        educationalContext.setName("context");
        searchFilter.setTaxon(educationalContext);
        String tokenizedQuery = "educational_context:\"context\" AND (visibility:\"public\" OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(3L), createMaterial(4L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchWithFiltersNull() {
        String query = "airplane";
        SearchFilter searchFilter = new SearchFilter();
        String tokenizedQuery = "(airplane) AND (visibility:\"public\" OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchWithTaxonFilter() {
        String query = "pythagoras";
        SearchFilter searchFilter = new SearchFilter();
        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setId(2L);
        educationalContext.setName("PRESCHOOL");
        searchFilter.setTaxon(educationalContext);
        String tokenizedQuery = "(pythagoras) AND educational_context:\"preschool\""
                + " AND (visibility:\"public\" OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchWithTaxonDomainFilter() {
        String query = "pythagoras";

        SearchFilter searchFilter = new SearchFilter();
        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setId(2L);
        educationalContext.setName("PRESCHOOL");

        Domain domain = new Domain();
        domain.setId(3L);
        domain.setName("COOL_DOMAIN");
        domain.setEducationalContext(educationalContext);

        searchFilter.setTaxon(domain);
        String tokenizedQuery = "(pythagoras) AND domain:\"cool_domain\" AND educational_context:\"preschool\""
                + " AND (visibility:\"public\" OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchWithTaxonSubjectFilter() {
        String query = "pythagoras";

        SearchFilter searchFilter = new SearchFilter();
        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setId(2L);
        educationalContext.setName("PRESCHOOL");

        Domain domain = new Domain();
        domain.setId(3L);
        domain.setName("COOL_DOMAIN");
        domain.setEducationalContext(educationalContext);

        Subject subject = new Subject();
        subject.setId(4L);
        subject.setName("COOL_SUBJECT");
        subject.setDomain(domain);

        searchFilter.setTaxon(subject);

        String tokenizedQuery = "(pythagoras) AND subject:\"cool_subject\" AND domain:\"cool_domain\""
                + " AND educational_context:\"preschool\" AND (visibility:\"public\" OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchWithTaxonSpecializationFilter() {
        String query = "pythagoras";

        SearchFilter searchFilter = new SearchFilter();
        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setId(2L);
        educationalContext.setName("PRESCHOOL");

        Domain domain = new Domain();
        domain.setId(3L);
        domain.setName("COOL_DOMAIN");
        domain.setEducationalContext(educationalContext);

        Specialization specialization = new Specialization();
        specialization.setId(4L);
        specialization.setName("COOL_SPECIALIZATION");
        specialization.setDomain(domain);

        searchFilter.setTaxon(specialization);

        String tokenizedQuery = "(pythagoras) AND specialization:\"cool_specialization\" AND domain:\"cool_domain\""
                + " AND educational_context:\"preschool\" AND (visibility:\"public\" OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchWithTaxonModuleFilter() {
        String query = "pythagoras";

        SearchFilter searchFilter = new SearchFilter();
        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setId(2L);
        educationalContext.setName("PRESCHOOL");

        Domain domain = new Domain();
        domain.setId(3L);
        domain.setName("COOL_DOMAIN");
        domain.setEducationalContext(educationalContext);

        Specialization specialization = new Specialization();
        specialization.setId(4L);
        specialization.setName("COOL_SPECIALIZATION");
        specialization.setDomain(domain);

        Module module = new Module();
        module.setId(5L);
        module.setName("COOL_MODULE");
        module.setSpecialization(specialization);

        searchFilter.setTaxon(module);

        String tokenizedQuery = "(pythagoras) AND module:\"cool_module\" AND specialization:\"cool_specialization\""
                + " AND domain:\"cool_domain\" AND educational_context:\"preschool\" AND (visibility:\"public\" OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchWithTaxonPreschoolTopicFilter() {
        String query = "pythagoras";

        SearchFilter searchFilter = new SearchFilter();
        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setId(2L);
        educationalContext.setName("PRESCHOOL");

        Domain domain = new Domain();
        domain.setId(3L);
        domain.setName("COOL_DOMAIN");
        domain.setEducationalContext(educationalContext);

        Topic topic = new Topic();
        topic.setId(4L);
        topic.setName("COOL_TOPIC");
        topic.setDomain(domain);

        searchFilter.setTaxon(topic);
        String tokenizedQuery = "(pythagoras) AND topic:\"cool_topic\" AND domain:\"cool_domain\""
                + " AND educational_context:\"preschool\" AND (visibility:\"public\" OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchWithTaxonBasicEducationTopicFilter() {
        String query = "pythagoras";

        SearchFilter searchFilter = new SearchFilter();
        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setId(2L);
        educationalContext.setName("PRESCHOOL");

        Domain domain = new Domain();
        domain.setId(3L);
        domain.setName("COOL_DOMAIN");
        domain.setEducationalContext(educationalContext);

        Subject subject = new Subject();
        subject.setId(4L);
        subject.setName("COOL_SUBJECT");
        subject.setDomain(domain);

        Topic topic = new Topic();
        topic.setId(5L);
        topic.setName("COOL_TOPIC");
        topic.setSubject(subject);

        searchFilter.setTaxon(topic);
        String tokenizedQuery = "(pythagoras) AND topic:\"cool_topic\" AND subject:\"cool_subject\""
                + " AND domain:\"cool_domain\" AND educational_context:\"preschool\" AND (visibility:\"public\" OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchWithTaxonVocationalEducationTopicFilter() {
        String query = "pythagoras";

        SearchFilter searchFilter = new SearchFilter();
        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setId(2L);
        educationalContext.setName("PRESCHOOL");

        Domain domain = new Domain();
        domain.setId(3L);
        domain.setName("COOL_DOMAIN");
        domain.setEducationalContext(educationalContext);

        Specialization specialization = new Specialization();
        specialization.setId(4L);
        specialization.setName("COOL_SPECIALIZATION");
        specialization.setDomain(domain);

        Module module = new Module();
        module.setId(5L);
        module.setName("COOL_MODULE");
        module.setSpecialization(specialization);

        Topic topic = new Topic();
        topic.setId(6L);
        topic.setName("COOL_TOPIC");
        topic.setModule(module);

        searchFilter.setTaxon(topic);

        String tokenizedQuery = "(pythagoras) AND topic:\"cool_topic\" AND module:\"cool_module\""
                + " AND specialization:\"cool_specialization\" AND domain:\"cool_domain\""
                + " AND educational_context:\"preschool\" AND (visibility:\"public\" OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchWithTaxonPreschoolSubtopicFilter() {
        String query = "pythagoras";

        SearchFilter searchFilter = new SearchFilter();
        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setId(2L);
        educationalContext.setName("PRESCHOOL");

        Domain domain = new Domain();
        domain.setId(3L);
        domain.setName("COOL_DOMAIN");
        domain.setEducationalContext(educationalContext);

        Topic topic = new Topic();
        topic.setId(4L);
        topic.setName("COOL_TOPIC");
        topic.setDomain(domain);

        Subtopic subtopic = new Subtopic();
        subtopic.setId(5L);
        subtopic.setName("COOL_SUBTOPIC");
        subtopic.setTopic(topic);

        searchFilter.setTaxon(subtopic);
        String tokenizedQuery = "(pythagoras) AND subtopic:\"cool_subtopic\" AND topic:\"cool_topic\""
                + " AND domain:\"cool_domain\" AND educational_context:\"preschool\" AND (visibility:\"public\" OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchWithTaxonBasicEducationSubtopicFilter() {
        String query = "pythagoras";

        SearchFilter searchFilter = new SearchFilter();
        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setId(2L);
        educationalContext.setName("PRESCHOOL");

        Domain domain = new Domain();
        domain.setId(3L);
        domain.setName("COOL_DOMAIN");
        domain.setEducationalContext(educationalContext);

        Subject subject = new Subject();
        subject.setId(4L);
        subject.setName("COOL_SUBJECT");
        subject.setDomain(domain);

        Topic topic = new Topic();
        topic.setId(5L);
        topic.setName("COOL_TOPIC");
        topic.setSubject(subject);

        Subtopic subtopic = new Subtopic();
        subtopic.setId(6L);
        subtopic.setName("COOL_SUBTOPIC");
        subtopic.setTopic(topic);

        searchFilter.setTaxon(subtopic);
        String tokenizedQuery = "(pythagoras) AND subtopic:\"cool_subtopic\" AND topic:\"cool_topic\""
                + " AND subject:\"cool_subject\" AND domain:\"cool_domain\" AND educational_context:\"preschool\""
                + " AND (visibility:\"public\" OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchWithTaxonVocationalEducationSubtopicFilter() {
        String query = "pythagoras";

        SearchFilter searchFilter = new SearchFilter();
        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setId(2L);
        educationalContext.setName("PRESCHOOL");

        Domain domain = new Domain();
        domain.setId(3L);
        domain.setName("COOL_DOMAIN");
        domain.setEducationalContext(educationalContext);

        Specialization specialization = new Specialization();
        specialization.setId(4L);
        specialization.setName("COOL_SPECIALIZATION");
        specialization.setDomain(domain);

        Module module = new Module();
        module.setId(5L);
        module.setName("COOL_MODULE");
        module.setSpecialization(specialization);

        Topic topic = new Topic();
        topic.setId(6L);
        topic.setName("COOL_TOPIC");
        topic.setModule(module);

        Subtopic subtopic = new Subtopic();
        subtopic.setId(7L);
        subtopic.setName("COOL_SUBTOPIC");
        subtopic.setTopic(topic);

        searchFilter.setTaxon(subtopic);

        String tokenizedQuery = "(pythagoras) AND subtopic:\"cool_subtopic\" AND topic:\"cool_topic\""
                + " AND module:\"cool_module\" AND specialization:\"cool_specialization\" AND domain:\"cool_domain\""
                + " AND educational_context:\"preschool\" AND (visibility:\"public\" OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchWithPaidFilterFalse() {
        String query = "textbooks";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPaid(false);
        String tokenizedQuery = "(textbooks) AND (paid:\"false\" OR type:\"portfolio\")"
                + " AND (visibility:\"public\" OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchWithPaidFilterTrue() {
        String query = "textbooks";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPaid(true);
        String tokenizedQuery = "(textbooks) AND (visibility:\"public\" OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchWithTypeFilter() {
        String query = "sky";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setType("material");
        String tokenizedQuery = "(sky) AND type:\"material\" AND (visibility:\"public\" OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchWithTypeFilterAll() {
        String query = "sky";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setType("all");
        String tokenizedQuery = "(sky) AND (type:\"material\" OR type:\"portfolio\") AND (visibility:\"public\" OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchWithTaxonEducationalContextAndPaidFilter() {
        String query = "pythagoras";
        SearchFilter searchFilter = new SearchFilter();
        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setId(2L);
        educationalContext.setName("PRESCHOOL");
        searchFilter.setTaxon(educationalContext);
        searchFilter.setPaid(false);
        String tokenizedQuery = "(pythagoras) AND educational_context:\"preschool\" AND (paid:\"false\" OR type:\"portfolio\")"
                + " AND (visibility:\"public\" OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchWithTaxonEducationalContextAndTypeFilter() {
        String query = "pythagoras";
        SearchFilter searchFilter = new SearchFilter();
        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setId(2L);
        educationalContext.setName("PRESCHOOL");
        searchFilter.setTaxon(educationalContext);
        searchFilter.setType("portfolio");
        String tokenizedQuery = "(pythagoras) AND educational_context:\"preschool\" AND type:\"portfolio\""
                + " AND (visibility:\"public\" OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchWithPaidAndTypeFilter() {
        String query = "pythagoras";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPaid(false);
        searchFilter.setType("material");
        String tokenizedQuery = "(pythagoras) AND (paid:\"false\" OR type:\"portfolio\") AND type:\"material\""
                + " AND (visibility:\"public\" OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchWithAllFilters() {
        String query = "pythagoras";

        SearchFilter searchFilter = new SearchFilter();

        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setId(2L);
        educationalContext.setName("PRESCHOOL");

        Domain domain = new Domain();
        domain.setId(3L);
        domain.setName("COOL_DOMAIN");
        domain.setEducationalContext(educationalContext);

        Subject subject = new Subject();
        subject.setId(4L);
        subject.setName("COOL_SUBJECT");
        subject.setDomain(domain);

        Topic topic = new Topic();
        topic.setId(5L);
        topic.setName("COOL_TOPIC");
        topic.setSubject(subject);

        searchFilter.setTaxon(topic);

        searchFilter.setPaid(false);
        searchFilter.setType("material");

        ResourceType resourceType = new ResourceType();
        resourceType.setId(1L);
        resourceType.setName("EXTRABOOK");
        searchFilter.setResourceType(resourceType);

        searchFilter.setSpecialEducation(true);

        CrossCurricularTheme crossCurricularTheme = new CrossCurricularTheme();
        crossCurricularTheme.setId(1L);
        crossCurricularTheme.setName("test_theme");
        searchFilter.setCrossCurricularTheme(crossCurricularTheme);

        KeyCompetence keyCompetence = new KeyCompetence();
        keyCompetence.setId(1L);
        keyCompetence.setName("test_competence");
        searchFilter.setKeyCompetence(keyCompetence);

        searchFilter.setIssuedFrom(2010);

        String tokenizedQuery = "(pythagoras) AND topic:\"cool_topic\" AND subject:\"cool_subject\""
                + " AND domain:\"cool_domain\" AND educational_context:\"preschool\""
                + " AND (paid:\"false\" OR type:\"portfolio\") AND type:\"material\" AND resource_type:\"extrabook\""
                + " AND special_education:\"true\""
                + " AND (issue_date_year:[2010 TO *] OR (added:[2010-01-01T00:00:00Z TO *] AND type:\"portfolio\"))"
                + " AND cross_curricular_theme:\"test_theme\""
                + " AND key_competence:\"test_competence\" AND (visibility:\"public\" OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchWithLanguageFilter() {
        String query = "alpha beta";
        SearchFilter searchFilter = new SearchFilter();
        Language language = new Language();
        language.setCode("mmm");
        searchFilter.setLanguage(language);
        String tokenizedQuery = "(alpha beta) AND (language:\"mmm\" OR type:\"portfolio\")"
                + " AND (visibility:\"public\" OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchWithTargetGroupFilter() {
        String query = "umm";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setTargetGroups(Arrays.asList(TargetGroup.SIX_SEVEN));
        String tokenizedQuery = "(umm) AND target_group:\"six_seven\" AND (visibility:\"public\" OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchWithMultipleTargetGroupsFilter() {
        String query = "umm";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setTargetGroups(Arrays.asList(TargetGroup.SIX_SEVEN, TargetGroup.ZERO_FIVE));
        String tokenizedQuery = "(umm) AND (target_group:\"six_seven\" OR target_group:\"zero_five\")"
                + " AND (visibility:\"public\" OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchWithResourceType() {
        String query = "test";
        SearchFilter searchFilter = new SearchFilter();
        ResourceType resourceType = new ResourceType();
        resourceType.setId(1L);
        resourceType.setName("EXTRABOOK");
        searchFilter.setResourceType(resourceType);
        String tokenizedQuery = "(test) AND resource_type:\"extrabook\" AND (visibility:\"public\" OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchWithSpecialEducation() {
        String query = "test";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setSpecialEducation(true);
        String tokenizedQuery = "(test) AND special_education:\"true\" AND (visibility:\"public\" OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchWithCrossCurricularTheme() {
        String query = "test";
        SearchFilter searchFilter = new SearchFilter();
        CrossCurricularTheme crossCurricularTheme = new CrossCurricularTheme();
        crossCurricularTheme.setId(1L);
        crossCurricularTheme.setName("test_theme");
        searchFilter.setCrossCurricularTheme(crossCurricularTheme);
        String tokenizedQuery = "(test) AND cross_curricular_theme:\"test_theme\" AND (visibility:\"public\" OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchWithKeyCompetence() {
        String query = "test";
        SearchFilter searchFilter = new SearchFilter();
        KeyCompetence keyCompetence = new KeyCompetence();
        keyCompetence.setId(1L);
        keyCompetence.setName("test_competence");
        searchFilter.setKeyCompetence(keyCompetence);
        String tokenizedQuery = "(test) AND key_competence:\"test_competence\" AND (visibility:\"public\" OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchWithKeyCompetenceAsAdmin() {
        String query = "test";
        SearchFilter searchFilter = new SearchFilter();
        KeyCompetence keyCompetence = new KeyCompetence();
        keyCompetence.setId(1L);
        keyCompetence.setName("test_competence");
        searchFilter.setKeyCompetence(keyCompetence);
        String tokenizedQuery = "(test) AND key_competence:\"test_competence\"";
        long start = 0;

        User loggedInUser = new User();
        loggedInUser.setRole(Role.ADMIN);

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchables.size(), searchFilter,
                loggedInUser);
    }

    @Test
    public void searchWithIssueDate() {
        String query = "airplane";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setIssuedFrom(2012);
        String tokenizedQuery = "(airplane) AND (issue_date_year:[2012 TO *]"
                + " OR (added:[2012-01-01T00:00:00Z TO *] AND type:\"portfolio\"))"
                + " AND (visibility:\"public\" OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchWithSorting() {
        String query = "english language";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setSort("somefield");
        searchFilter.setSortDirection(SearchFilter.SortDirection.DESCENDING);
        String tokenizedQuery = "(english language) AND (visibility:\"public\" OR type:\"material\")";
        String expectedSort = "somefield desc";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, expectedSort, searchables, start, null, searchFilter);
    }

    @Test
    public void searchWithCurriculumLiteratureTrue() {
        String query = "german language";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setCurriculumLiterature(true);
        String tokenizedQuery = "(german language) AND curriculum_literature:\"true\""
                + " AND (visibility:\"public\" OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchWithCurriculumLiteratureFalse() {
        String query = "german language";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setCurriculumLiterature(false);
        String tokenizedQuery = "(german language) AND curriculum_literature:\"false\""
                + " AND (visibility:\"public\" OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchWithLimit() {
        String query = "german language";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setCurriculumLiterature(false);
        String tokenizedQuery = "(german language) AND curriculum_literature:\"false\""
                + " AND (visibility:\"public\" OR type:\"material\")";
        long start = 0;
        Long limit = 3L;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, limit, searchFilter);
    }

    @Test
    public void searchNotFromStart() {
        String query = "people";
        String tokenizedQuery = "(people) AND (visibility:\"public\" OR type:\"material\")";
        SearchFilter searchFilter = new SearchFilter();
        long start = 50;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchHasMoreResultsThanMaxResultsPerPage() {
        String query = "people";
        String tokenizedQuery = "(people) AND (visibility:\"public\" OR type:\"material\")";
        SearchFilter searchFilter = new SearchFilter();
        long start = 0;
        long totalResults = 1450;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, totalResults, searchFilter, null);
    }

    @Test
    public void searchReturnsOnlyMaterials() {
        String query = "people";
        String tokenizedQuery = "(people) AND (visibility:\"public\" OR type:\"material\")";
        SearchFilter searchFilter = new SearchFilter();
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchReturnsOnlyPortfolios() {
        String query = "people";
        String tokenizedQuery = "(people) AND (visibility:\"public\" OR type:\"material\")";
        SearchFilter searchFilter = new SearchFilter();
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createPortfolio(9L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    @Test
    public void searchReturnsMaterialAndPortfoliosWithSameIds() {
        String query = "people";
        String tokenizedQuery = "(people) AND (visibility:\"public\" OR type:\"material\")";
        SearchFilter searchFilter = new SearchFilter();
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createPortfolio(9L), createMaterial(9L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter);
    }

    private void testSearch(String query, String tokenizedQuery, String expectedSort, List<Searchable> searchables,
            long start, Long limit, long totalResults, SearchFilter searchFilter, User loggedInUser) {
        SearchResponse searchResponse = createSearchResponseWithDocuments(searchables, start, totalResults);

        List<LearningObject> learningObjects = new ArrayList<>();
        List<Long> learningObjectIdentifiers = getIdentifiers(searchables);

        List<Material> materials = collectMaterialsFrom(searchables);
        learningObjects.addAll(materials);
        List<Portfolio> portfolios = collectPortfoliosFrom(searchables);
        learningObjects.addAll(portfolios);

        if (limit == null) {
            expect(searchEngineService.search(tokenizedQuery, start, expectedSort)).andReturn(searchResponse);
        } else {
            expect(searchEngineService.search(tokenizedQuery, start, limit, expectedSort)).andReturn(searchResponse);
        }

        expect(learningObjectDAO.findAllById(learningObjectIdentifiers)).andReturn(learningObjects);

        replayAll();

        SearchResult result = searchService.search(query, start, limit, searchFilter, loggedInUser);

        verifyAll();

        assertEquals(totalResults, result.getTotalResults());
        assertSameSearchable(searchables, result.getItems());
        assertEquals(start, result.getStart());
    }

    private void testSearch(String query, String tokenizedQuery, String expectedSort, List<Searchable> searchables,
            long start, Long limit, SearchFilter searchFilter) {
        if (limit == null) {
            testSearch(query, tokenizedQuery, expectedSort, searchables, start, limit, searchables.size(),
                    searchFilter, null);
        } else {
            testSearch(query, tokenizedQuery, expectedSort, searchables, start, limit, limit, searchFilter, null);
        }
    }

    private List<Long> getIdentifiers(List<Searchable> searchables) {
        List<Long> identifiers = new ArrayList<>();
        searchables.stream().forEach(s -> identifiers.add(s.getId()));
        return identifiers;
    }

    private SearchResponse createSearchResponseWithDocuments(List<Searchable> searchables, long start, long totalResults) {
        List<Document> documents = new ArrayList<>();
        for (Searchable searchable : searchables) {
            Document newDocument = new Document();
            newDocument.setId(searchable.getId().toString());
            newDocument.setType(searchable.getType());
            documents.add(newDocument);
        }

        Response response = new Response();
        response.setDocuments(documents);
        response.setTotalResults(totalResults);
        response.setStart(start);
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setResponse(response);

        return searchResponse;
    }

    private List<Material> collectMaterialsFrom(List<Searchable> searchables) {
        List<Material> materials = new ArrayList<>();

        for (Searchable searchable : searchables) {
            if (searchable instanceof Material) {
                materials.add((Material) searchable);
            }
        }

        return materials;
    }

    private List<Portfolio> collectPortfoliosFrom(List<Searchable> searchables) {
        List<Portfolio> portfolios = new ArrayList<>();

        for (Searchable searchable : searchables) {
            if (searchable instanceof Portfolio) {
                portfolios.add((Portfolio) searchable);
            }
        }

        return portfolios;
    }

    private void assertSameSearchable(List<Searchable> expected, List<Searchable> actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertSame(expected.get(i), actual.get(i));
        }
    }

    private void replayAll() {
        replay(searchEngineService, learningObjectDAO);
    }

    private void verifyAll() {
        verify(searchEngineService, learningObjectDAO);
    }

    private Material createMaterial(Long id) {
        Material material = new Material();
        material.setId(id);
        return material;
    }

    private Portfolio createPortfolio(Long id) {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(id);
        return portfolio;
    }

}
