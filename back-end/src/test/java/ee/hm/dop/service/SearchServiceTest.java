package ee.hm.dop.service;

import ee.hm.dop.dao.LearningObjectDAO;
import ee.hm.dop.dao.ReducedLearningObjectDAO;
import ee.hm.dop.dao.UserFavoriteDAO;
import ee.hm.dop.model.CrossCurricularTheme;
import ee.hm.dop.model.KeyCompetence;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.ReducedLearningObject;
import ee.hm.dop.model.ReducedMaterial;
import ee.hm.dop.model.ReducedPortfolio;
import ee.hm.dop.model.ResourceType;
import ee.hm.dop.model.Role;
import ee.hm.dop.model.SearchFilter;
import ee.hm.dop.model.SearchResult;
import ee.hm.dop.model.Searchable;
import ee.hm.dop.model.TargetGroup;
import ee.hm.dop.model.TargetGroupEnum;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

@RunWith(EasyMockRunner.class)
public class SearchServiceTest {

    @Mock
    private SolrEngineService solrEngineService;

    @Mock
    private LearningObjectDAO learningObjectDAO;

    @Mock
    private UserFavoriteDAO userFavoriteDAO;

    @Mock
    private TargetGroupService targetGroupService;

    @Mock
    private ReducedLearningObjectDAO reducedLearningObjectDAO;

    @TestSubject
    private SearchService searchService = new SearchService();

    @Test
    public void search() {
        String query = "people";
        String tokenizedQuery = "((people) OR (\"people\")) AND ((visibility:\"public\") OR type:\"material\")";
        SearchFilter searchFilter = new SearchFilter();
        long start = 0;
        List<Searchable> searchables = Arrays.asList(createMaterial(7L), createMaterial(1L), createPortfolio(4L),
                createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
    }

    @Test
    public void searchEmptyQueryAsAdmin() {
        User loggedInUser = new User();
        loggedInUser.setRole(Role.ADMIN);

        String query = "";
        String tokenizedQuery = "((visibility:\"public\" OR visibility:\"not_listed\" OR visibility:\"private\") OR type:\"material\")";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setRequestingUser(loggedInUser);
        long start = 0;
        List<Searchable> searchables = Arrays.asList(createMaterial(3L), createMaterial(4L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, loggedInUser);
    }

    @Test
    public void searchEmptyQueryAndTaxonFilter() {
        String query = "";
        SearchFilter searchFilter = new SearchFilter();
        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setId(1L);
        educationalContext.setName("PRESCHOOL");
        searchFilter.setTaxons(Collections.singletonList(educationalContext));
        String tokenizedQuery = "educational_context:\"preschool\" AND ((visibility:\"public\") OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(3L), createMaterial(4L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
    }

    @Test
    public void searchWithEmptyQueryAndTypeFilterAll() {
        String query = "";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setType("all");
        String tokenizedQuery = "(type:\"material\" OR type:\"portfolio\") AND ((visibility:\"public\") OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
    }

    @Test
    public void searchNullQueryAndNullFiltersAsAdmin() {
        User loggedInUser = new User();
        loggedInUser.setRole(Role.ADMIN);


        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setRequestingUser(loggedInUser);
        String query = "";
        String tokenizedQuery = "((visibility:\"public\" OR visibility:\"not_listed\" OR visibility:\"private\") OR type:\"material\")";
        List<Searchable> searchables = Arrays.asList(createMaterial(3L), createMaterial(4L));
        long start = 0;


        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, loggedInUser);

    }

    @Test
    public void searchNullQueryAndTaxonFilter() {
        String query = null;
        SearchFilter searchFilter = new SearchFilter();
        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setId(2L);
        educationalContext.setName("context");
        searchFilter.setTaxons(Collections.singletonList(educationalContext));
        String tokenizedQuery = "educational_context:\"context\" AND ((visibility:\"public\") OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(3L), createMaterial(4L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
    }

    @Test
    public void searchWithFiltersNull() {
        String query = "airplane";
        SearchFilter searchFilter = new SearchFilter();
        String tokenizedQuery = "((airplane) OR (\"airplane\")) AND ((visibility:\"public\") OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
    }

    @Test
    public void searchWithTaxonFilter() {
        String query = "pythagoras";
        SearchFilter searchFilter = new SearchFilter();
        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setId(2L);
        educationalContext.setName("PRESCHOOL");
        searchFilter.setTaxons(Collections.singletonList(educationalContext));
        String tokenizedQuery = "((pythagoras) OR (\"pythagoras\")) AND educational_context:\"preschool\""
                + " AND ((visibility:\"public\") OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
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

        searchFilter.setTaxons(Collections.singletonList(domain));
        String tokenizedQuery = "((pythagoras) OR (\"pythagoras\")) AND domain:\"cool_domain\" AND educational_context:\"preschool\""
                + " AND ((visibility:\"public\") OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
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

        searchFilter.setTaxons(Collections.singletonList(subject));

        String tokenizedQuery = "((pythagoras) OR (\"pythagoras\")) AND subject:\"cool_subject\" AND domain:\"cool_domain\""
                + " AND educational_context:\"preschool\" AND ((visibility:\"public\") OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
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

        searchFilter.setTaxons(Collections.singletonList(specialization));

        String tokenizedQuery = "((pythagoras) OR (\"pythagoras\")) AND specialization:\"cool_specialization\" AND domain:\"cool_domain\""
                + " AND educational_context:\"preschool\" AND ((visibility:\"public\") OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
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

        searchFilter.setTaxons(Collections.singletonList(module));

        String tokenizedQuery = "((pythagoras) OR (\"pythagoras\")) AND module:\"cool_module\" AND specialization:\"cool_specialization\""
                + " AND domain:\"cool_domain\" AND educational_context:\"preschool\" AND ((visibility:\"public\") OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
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

        searchFilter.setTaxons(Collections.singletonList(topic));
        String tokenizedQuery = "((pythagoras) OR (\"pythagoras\")) AND topic:\"cool_topic\" AND domain:\"cool_domain\""
                + " AND educational_context:\"preschool\" AND ((visibility:\"public\") OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
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

        searchFilter.setTaxons(Collections.singletonList(topic));
        String tokenizedQuery = "((pythagoras) OR (\"pythagoras\")) AND topic:\"cool_topic\" AND subject:\"cool_subject\""
                + " AND domain:\"cool_domain\" AND educational_context:\"preschool\" AND ((visibility:\"public\") OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
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

        searchFilter.setTaxons(Collections.singletonList(topic));

        String tokenizedQuery = "((pythagoras) OR (\"pythagoras\")) AND topic:\"cool_topic\" AND module:\"cool_module\""
                + " AND specialization:\"cool_specialization\" AND domain:\"cool_domain\""
                + " AND educational_context:\"preschool\" AND ((visibility:\"public\") OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
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

        searchFilter.setTaxons(Collections.singletonList(subtopic));
        String tokenizedQuery = "((pythagoras) OR (\"pythagoras\")) AND subtopic:\"cool_subtopic\" AND topic:\"cool_topic\""
                + " AND domain:\"cool_domain\" AND educational_context:\"preschool\" AND ((visibility:\"public\") OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
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

        searchFilter.setTaxons(Collections.singletonList(subtopic));
        String tokenizedQuery = "((pythagoras) OR (\"pythagoras\")) AND subtopic:\"cool_subtopic\" AND topic:\"cool_topic\""
                + " AND subject:\"cool_subject\" AND domain:\"cool_domain\" AND educational_context:\"preschool\""
                + " AND ((visibility:\"public\") OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
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

        searchFilter.setTaxons(Collections.singletonList(subtopic));

        String tokenizedQuery = "((pythagoras) OR (\"pythagoras\")) AND subtopic:\"cool_subtopic\" AND topic:\"cool_topic\""
                + " AND module:\"cool_module\" AND specialization:\"cool_specialization\" AND domain:\"cool_domain\""
                + " AND educational_context:\"preschool\" AND ((visibility:\"public\") OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
    }

    @Test
    public void searchWithPaidFilterFalse() {
        String query = "textbooks";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPaid(false);
        String tokenizedQuery = "((textbooks) OR (\"textbooks\")) AND (paid:\"false\" OR type:\"portfolio\")"
                + " AND ((visibility:\"public\") OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
    }

    @Test
    public void searchWithPaidFilterTrue() {
        String query = "textbooks";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPaid(true);
        String tokenizedQuery = "((textbooks) OR (\"textbooks\")) AND ((visibility:\"public\") OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
    }

    @Test
    public void searchWithTypeFilter() {
        String query = "sky";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setType("material");
        String tokenizedQuery = "((sky) OR (\"sky\")) AND type:\"material\" AND ((visibility:\"public\") OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
    }

    @Test
    public void searchWithTypeFilterAll() {
        String query = "sky";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setType("all");
        String tokenizedQuery = "((sky) OR (\"sky\")) AND (type:\"material\" OR type:\"portfolio\") AND ((visibility:\"public\") OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
    }

    @Test
    public void searchWithTaxonEducationalContextAndPaidFilter() {
        String query = "pythagoras";
        SearchFilter searchFilter = new SearchFilter();
        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setId(2L);
        educationalContext.setName("PRESCHOOL");
        searchFilter.setTaxons(Collections.singletonList(educationalContext));
        searchFilter.setPaid(false);
        String tokenizedQuery = "((pythagoras) OR (\"pythagoras\")) AND educational_context:\"preschool\" AND (paid:\"false\" OR type:\"portfolio\")"
                + " AND ((visibility:\"public\") OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
    }

    @Test
    public void searchWithTaxonEducationalContextAndTypeFilter() {
        String query = "pythagoras";
        SearchFilter searchFilter = new SearchFilter();
        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setId(2L);
        educationalContext.setName("PRESCHOOL");
        searchFilter.setTaxons(Collections.singletonList(educationalContext));
        searchFilter.setType("portfolio");
        String tokenizedQuery = "((pythagoras) OR (\"pythagoras\")) AND educational_context:\"preschool\" AND type:\"portfolio\""
                + " AND ((visibility:\"public\") OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
    }

    @Test
    public void searchWithPaidAndTypeFilter() {
        String query = "pythagoras";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPaid(false);
        searchFilter.setType("material");
        String tokenizedQuery = "((pythagoras) OR (\"pythagoras\")) AND (paid:\"false\" OR type:\"portfolio\") AND type:\"material\""
                + " AND ((visibility:\"public\") OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
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

        searchFilter.setTaxons(Collections.singletonList(topic));

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
        searchFilter.setCrossCurricularThemes(Collections.singletonList(crossCurricularTheme));

        KeyCompetence keyCompetence = new KeyCompetence();
        keyCompetence.setId(1L);
        keyCompetence.setName("test_competence");
        searchFilter.setKeyCompetences(Collections.singletonList(keyCompetence));

        searchFilter.setIssuedFrom(2010);

        String tokenizedQuery = "((pythagoras) OR (\"pythagoras\")) AND topic:\"cool_topic\" AND subject:\"cool_subject\""
                + " AND domain:\"cool_domain\" AND educational_context:\"preschool\""
                + " AND (paid:\"false\" OR type:\"portfolio\") AND type:\"material\" AND resource_type:\"extrabook\""
                + " AND special_education:\"true\""
                + " AND (issue_date_year:[2010 TO *] OR (added:[2010-01-01T00:00:00Z TO *] AND type:\"portfolio\"))"
                + " AND cross_curricular_theme:\"test_theme\""
                + " AND key_competence:\"test_competence\" AND ((visibility:\"public\") OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
    }

    @Test
    public void searchWithLanguageFilter() {
        String query = "alpha beta";
        SearchFilter searchFilter = new SearchFilter();
        Language language = new Language();
        language.setCode("mmm");
        searchFilter.setLanguage(language);
        String tokenizedQuery = "((alpha beta) OR (\"alpha beta\")) AND (language:\"mmm\" OR type:\"portfolio\")"
                + " AND ((visibility:\"public\") OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
    }

    @Test
    public void searchWithTargetGroupFilter() {
        String query = "umm";
        SearchFilter searchFilter = new SearchFilter();

        TargetGroup targetGroupSixSeven = new TargetGroup();
        targetGroupSixSeven.setId(2L);
        targetGroupSixSeven.setName(TargetGroupEnum.SIX_SEVEN.name());

        expect(targetGroupService.getByName(TargetGroupEnum.SIX_SEVEN.name())).andReturn(targetGroupSixSeven);

        replay(targetGroupService);

        searchFilter.setTargetGroups(Collections.singletonList(targetGroupService
                .getByName(TargetGroupEnum.SIX_SEVEN.name())));

        verify(targetGroupService);

        String tokenizedQuery = "((umm) OR (\"umm\")) AND target_group:\"2\" AND ((visibility:\"public\") OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
    }

    @Test
    public void searchWithMultipleTargetGroupsFilter() {
        String query = "umm";
        SearchFilter searchFilter = new SearchFilter();

        TargetGroup targetGroupSixSeven = new TargetGroup();
        targetGroupSixSeven.setId(2L);
        targetGroupSixSeven.setName(TargetGroupEnum.SIX_SEVEN.name());
        TargetGroup targetGroupZeroFive = new TargetGroup();
        targetGroupZeroFive.setId(1L);
        targetGroupZeroFive.setName(TargetGroupEnum.ZERO_FIVE.name());

        expect(targetGroupService.getByName(TargetGroupEnum.SIX_SEVEN.name())).andReturn(targetGroupSixSeven);
        expect(targetGroupService.getByName(TargetGroupEnum.ZERO_FIVE.name())).andReturn(targetGroupZeroFive);

        replay(targetGroupService);

        searchFilter.setTargetGroups(Arrays.asList(targetGroupService.getByName(TargetGroupEnum.SIX_SEVEN.name()),
                targetGroupService.getByName(TargetGroupEnum.ZERO_FIVE.name())));

        verify(targetGroupService);

        String tokenizedQuery = "((umm) OR (\"umm\")) AND (target_group:\"2\" OR target_group:\"1\")"
                + " AND ((visibility:\"public\") OR type:\"material\")";

        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
    }

    @Test
    public void searchWithResourceType() {
        String query = "test";
        SearchFilter searchFilter = new SearchFilter();
        ResourceType resourceType = new ResourceType();
        resourceType.setId(1L);
        resourceType.setName("EXTRABOOK");
        searchFilter.setResourceType(resourceType);
        String tokenizedQuery = "((test) OR (\"test\")) AND resource_type:\"extrabook\" AND ((visibility:\"public\") OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
    }

    @Test
    public void searchWithSpecialEducation() {
        String query = "test";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setSpecialEducation(true);
        String tokenizedQuery = "((test) OR (\"test\")) AND special_education:\"true\" AND ((visibility:\"public\") OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
    }

    @Test
    public void searchWithCrossCurricularTheme() {
        String query = "test";
        SearchFilter searchFilter = new SearchFilter();
        CrossCurricularTheme crossCurricularTheme = new CrossCurricularTheme();
        crossCurricularTheme.setId(1L);
        crossCurricularTheme.setName("test_theme");
        searchFilter.setCrossCurricularThemes(Collections.singletonList(crossCurricularTheme));
        String tokenizedQuery = "((test) OR (\"test\")) AND cross_curricular_theme:\"test_theme\" AND ((visibility:\"public\") OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
    }

    @Test
    public void searchWithKeyCompetence() {
        String query = "test";
        SearchFilter searchFilter = new SearchFilter();
        KeyCompetence keyCompetence = new KeyCompetence();
        keyCompetence.setId(1L);
        keyCompetence.setName("test_competence");
        searchFilter.setKeyCompetences(Collections.singletonList(keyCompetence));
        String tokenizedQuery = "((test) OR (\"test\")) AND key_competence:\"test_competence\" AND ((visibility:\"public\") OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
    }

    @Test
    public void searchWithKeyCompetenceAsAdmin() {
        String query = "test";
        SearchFilter searchFilter = new SearchFilter();
        KeyCompetence keyCompetence = new KeyCompetence();
        keyCompetence.setId(1L);
        keyCompetence.setName("test_competence");
        searchFilter.setKeyCompetences(Collections.singletonList(keyCompetence));
        String tokenizedQuery = "((test) OR (\"test\")) AND key_competence:\"test_competence\" AND ((visibility:\"public\" OR visibility:\"not_listed\" OR visibility:\"private\") OR type:\"material\")";
        long start = 0;

        User loggedInUser = new User();
        loggedInUser.setRole(Role.ADMIN);

        searchFilter.setRequestingUser(loggedInUser);

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchables.size(), searchFilter,
                loggedInUser);
    }

    @Test
    public void searchWithIssueDate() {
        String query = "airplane";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setIssuedFrom(2012);
        String tokenizedQuery = "((airplane) OR (\"airplane\")) AND (issue_date_year:[2012 TO *]"
                + " OR (added:[2012-01-01T00:00:00Z TO *] AND type:\"portfolio\"))"
                + " AND ((visibility:\"public\") OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
    }

    @Test
    public void searchWithSorting() {
        String query = "english language";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setSort("somefield");
        searchFilter.setSortDirection(SearchFilter.SortDirection.DESCENDING);
        String tokenizedQuery = "((english language) OR (\"english language\")) AND ((visibility:\"public\") OR type:\"material\")";
        String expectedSort = "somefield desc";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, expectedSort, searchables, start, null, searchFilter, null);
    }

    @Test
    public void searchWithCurriculumLiteratureTrue() {
        String query = "german language";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setCurriculumLiterature(true);
        String tokenizedQuery = "((german language) OR (\"german language\")) AND peerReview:[* TO *]"
                + " AND ((visibility:\"public\") OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
    }

    @Test
    public void searchWithCurriculumLiteratureFalse() {
        String query = "german language";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setCurriculumLiterature(false);
        String tokenizedQuery = "((german language) OR (\"german language\"))"
                + " AND ((visibility:\"public\") OR type:\"material\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
    }

    @Test
    public void searchWithLimit() {
        String query = "german language";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setCurriculumLiterature(false);
        String tokenizedQuery = "((german language) OR (\"german language\"))"
                + " AND ((visibility:\"public\") OR type:\"material\")";
        long start = 0;
        Long limit = 3L;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, null, searchables, start, limit, searchFilter, null);
    }

    @Test
    public void searchNotFromStart() {
        String query = "people";
        String tokenizedQuery = "((people) OR (\"people\")) AND ((visibility:\"public\") OR type:\"material\")";
        SearchFilter searchFilter = new SearchFilter();
        long start = 50;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
    }

    @Test
    public void searchHasMoreResultsThanMaxResultsPerPage() {
        String query = "people";
        String tokenizedQuery = "((people) OR (\"people\")) AND ((visibility:\"public\") OR type:\"material\")";
        SearchFilter searchFilter = new SearchFilter();
        long start = 0;
        long totalResults = 1450;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(1L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, totalResults, searchFilter, null);
    }

    @Test
    public void searchReturnsOnlyMaterials() {
        String query = "people";
        String tokenizedQuery = "((people) OR (\"people\")) AND ((visibility:\"public\") OR type:\"material\")";
        SearchFilter searchFilter = new SearchFilter();
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
    }

    @Test
    public void searchReturnsOnlyPortfolios() {
        String query = "people";
        String tokenizedQuery = "((people) OR (\"people\")) AND ((visibility:\"public\") OR type:\"material\")";
        SearchFilter searchFilter = new SearchFilter();
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createPortfolio(9L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, null, searchables, start, null, searchFilter, null);
    }


    private void testSearch(String query, String tokenizedQuery, String expectedSort, List<Searchable> searchables,
                            long start, Long limit, long totalResults, SearchFilter searchFilter, User loggedInUser) {
        SearchResponse searchResponse = createSearchResponseWithDocuments(searchables, start, totalResults);

        List<ReducedLearningObject> learningObjects = new ArrayList<>();
        List<Long> learningObjectIdentifiers = getIdentifiers(searchables);

        List<ReducedMaterial> materials = collectMaterialsFrom(searchables);
        learningObjects.addAll(materials);
        List<ReducedPortfolio> portfolios = collectPortfoliosFrom(searchables);
        learningObjects.addAll(portfolios);

        if (limit == null) {
            expect(solrEngineService.search(tokenizedQuery, start, expectedSort)).andReturn(searchResponse);
        } else {
            expect(solrEngineService.search(tokenizedQuery, start, limit, expectedSort)).andReturn(searchResponse);
        }

        expect(reducedLearningObjectDAO.findAllById(learningObjectIdentifiers)).andReturn(learningObjects);
        if (loggedInUser != null) {
            for (Long id : learningObjectIdentifiers) {
                expect(userFavoriteDAO.findFavoriteByUserAndLearningObject(id, loggedInUser)).andReturn(null);
            }
        }

        replayAll();

        SearchResult result = searchService.search(query, start, limit, searchFilter);

        verifyAll();

        assertEquals(totalResults, result.getTotalResults());
        assertSameSearchable(searchables, result.getItems());
        assertEquals(start, result.getStart());
    }

    private void testSearch(String query, String tokenizedQuery, String expectedSort, List<Searchable> searchables,
                            long start, Long limit, SearchFilter searchFilter, User user) {
        if (limit == null) {
            testSearch(query, tokenizedQuery, expectedSort, searchables, start, null, searchables.size(),
                    searchFilter, user);
        } else {
            testSearch(query, tokenizedQuery, expectedSort, searchables, start, limit, limit, searchFilter, user);
        }
    }

    private List<Long> getIdentifiers(List<Searchable> searchables) {
        List<Long> identifiers = new ArrayList<>();
        searchables.forEach(s -> identifiers.add(s.getId()));
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

    private List<ReducedMaterial> collectMaterialsFrom(List<Searchable> searchables) {
        List<ReducedMaterial> materials = new ArrayList<>();

        for (Searchable searchable : searchables) {
            if (searchable instanceof ReducedMaterial) {
                materials.add((ReducedMaterial) searchable);
            }
        }

        return materials;
    }

    private List<ReducedPortfolio> collectPortfoliosFrom(List<Searchable> searchables) {
        List<ReducedPortfolio> portfolios = new ArrayList<>();

        for (Searchable searchable : searchables) {
            if (searchable instanceof ReducedPortfolio) {
                portfolios.add((ReducedPortfolio) searchable);
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
        replay(solrEngineService, learningObjectDAO, userFavoriteDAO, reducedLearningObjectDAO);
    }

    private void verifyAll() {
        verify(solrEngineService, learningObjectDAO, userFavoriteDAO, reducedLearningObjectDAO);
    }

    private ReducedMaterial createMaterial(Long id) {
        ReducedMaterial material = new ReducedMaterial();
        material.setId(id);
        return material;
    }

    private ReducedPortfolio createPortfolio(Long id) {
        ReducedPortfolio portfolio = new ReducedPortfolio();
        portfolio.setId(id);
        return portfolio;
    }

}
