package ee.hm.dop.service.solr;

import ee.hm.dop.dao.LearningObjectDao;
import ee.hm.dop.dao.ReducedLearningObjectDao;
import ee.hm.dop.dao.UserFavoriteDao;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.ReducedLearningObject;
import ee.hm.dop.model.SearchFilter;
import ee.hm.dop.model.SearchResult;
import ee.hm.dop.model.Searchable;
import ee.hm.dop.model.TargetGroup;
import ee.hm.dop.model.enums.TargetGroupEnum;
import ee.hm.dop.model.User;
import ee.hm.dop.model.solr.SearchResponse;
import ee.hm.dop.service.metadata.TargetGroupService;
import org.apache.commons.lang3.StringUtils;
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
public class SearchServiceTest extends SearchServiceTestUtil {

    public static final long ZERO = 0;
    public static final long _3 = 3L;
    public static final long _50 = 50;
    public static final long _1450 = 1450;
    @Mock
    private SolrEngineService solrEngineService;
    @Mock
    private LearningObjectDao learningObjectDao;
    @Mock
    private UserFavoriteDao userFavoriteDao;
    @Mock
    private TargetGroupService targetGroupService;
    @Mock
    private ReducedLearningObjectDao reducedLearningObjectDao;
    @TestSubject
    private SearchService searchService = new SearchService();

    @Test
    public void search() {
        String tokenizedQuery = "((people) OR (\"people\")) AND (visibility:\"public\")";
        SearchFilter searchFilter = new SearchFilter();
        testSearch(PEOPLE, tokenizedQuery, null, M7_M1_P4_P2, ZERO, null, searchFilter, null);
    }

    @Test
    public void searchEmptyQueryAsAdmin() {
        String tokenizedQuery = "(visibility:\"public\" OR visibility:\"not_listed\" OR visibility:\"private\")";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setRequestingUser(ADMIN);

        testSearch(EMPTY, tokenizedQuery, null, M3_M4, ZERO, null, searchFilter, ADMIN);
    }

    @Test
    public void searchEmptyQueryAndTaxonFilter() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setTaxons(EDUCATIONAL_CONTEXT);
        String tokenizedQuery = "educational_context:\"preschool\" AND (visibility:\"public\")";

        testSearch(EMPTY, tokenizedQuery, searchFilter, M3_M4);
    }

    @Test
    public void searchWithEmptyQueryAndTypeFilterAll() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setType("all");
        String tokenizedQuery = "(type:\"material\" OR type:\"portfolio\") AND (visibility:\"public\")";

        testSearch(EMPTY, tokenizedQuery, searchFilter, M9_M2_P1);
    }

    @Test
    public void searchNullQueryAndNullFiltersAsAdmin() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setRequestingUser(ADMIN);
        String tokenizedQuery = "(visibility:\"public\" OR visibility:\"not_listed\" OR visibility:\"private\")";

        testSearch(EMPTY, tokenizedQuery, null, M3_M4, ZERO, null, searchFilter, ADMIN);
    }

    @Test
    public void searchNullQueryAndTaxonFilter() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setTaxons(CONTEXT);
        String tokenizedQuery = "educational_context:\"context\" AND (visibility:\"public\")";

        testSearch(null, tokenizedQuery, searchFilter, M3_M4);
    }

    @Test
    public void searchWithFiltersNull() {
        SearchFilter searchFilter = new SearchFilter();
        String tokenizedQuery = "((airplane) OR (\"airplane\")) AND (visibility:\"public\")";

        testSearch(AIRPLANE, tokenizedQuery, searchFilter, M9_M2_P1);
    }

    @Test
    public void searchWithTaxonFilter() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setTaxons(EDUCATIONAL_CONTEXT);
        String tokenizedQuery = "((pythagoras) OR (\"pythagoras\")) AND educational_context:\"preschool\""
                + " AND (visibility:\"public\")";

        testSearch(PYTHAGORAS, tokenizedQuery, searchFilter, M9_M2_P1);
    }

    @Test
    public void searchWithTaxonDomainFilter() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setTaxons(DOMAIN_EDUC_CONT);
        String tokenizedQuery = "((pythagoras) OR (\"pythagoras\")) AND domain:\"cool_domain\" AND educational_context:\"preschool\""
                + " AND (visibility:\"public\")";

        testSearch(PYTHAGORAS, tokenizedQuery, searchFilter, M9_M2_P1);
    }

    @Test
    public void searchWithTaxonSubjectFilter() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setTaxons(SUBJECT_DOMAIN_EDUC_CONT);

        String tokenizedQuery = "((pythagoras) OR (\"pythagoras\")) AND subject:\"cool_subject\" AND domain:\"cool_domain\""
                + " AND educational_context:\"preschool\" AND (visibility:\"public\")";

        testSearch(PYTHAGORAS, tokenizedQuery, searchFilter, M9_M2_P1);
    }

    @Test
    public void searchWithTaxonSpecializationFilter() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setTaxons(SPECIAL_DOMAIN_EDUC_CONT);

        String tokenizedQuery = "((pythagoras) OR (\"pythagoras\")) AND specialization:\"cool_specialization\" AND domain:\"cool_domain\""
                + " AND educational_context:\"preschool\" AND (visibility:\"public\")";

        testSearch(PYTHAGORAS, tokenizedQuery, searchFilter, M9_M2_P1);
    }

    @Test
    public void searchWithTaxonModuleFilter() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setTaxons(MODULE_SPECIAL_DOMAIN_EDUC_CONT);

        String tokenizedQuery = "((pythagoras) OR (\"pythagoras\")) AND module:\"cool_module\" AND specialization:\"cool_specialization\""
                + " AND domain:\"cool_domain\" AND educational_context:\"preschool\" AND (visibility:\"public\")";

        testSearch(PYTHAGORAS, tokenizedQuery, searchFilter, M9_M2_P1);
    }

    @Test
    public void searchWithTaxonPreschoolTopicFilter() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setTaxons(TOPIC_DOMAIN_EDUC_CONT);
        String tokenizedQuery = "((pythagoras) OR (\"pythagoras\")) AND topic:\"cool_topic\" AND domain:\"cool_domain\""
                + " AND educational_context:\"preschool\" AND (visibility:\"public\")";

        testSearch(PYTHAGORAS, tokenizedQuery, searchFilter, M9_M2_P1);
    }

    @Test
    public void searchWithTaxonBasicEducationTopicFilter() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setTaxons(TOPIC_SUBJECT_DOMAIN_EDUC_CONT);
        String tokenizedQuery = "((pythagoras) OR (\"pythagoras\")) AND topic:\"cool_topic\" AND subject:\"cool_subject\""
                + " AND domain:\"cool_domain\" AND educational_context:\"preschool\" AND (visibility:\"public\")";

        testSearch(PYTHAGORAS, tokenizedQuery, searchFilter, M9_M2_P1);
    }

    @Test
    public void searchWithTaxonVocationalEducationTopicFilter() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setTaxons(TOPIC_MODULE_SPECIAL_DOMAIN_EDUC_CONT);

        String tokenizedQuery = "((pythagoras) OR (\"pythagoras\")) AND topic:\"cool_topic\" AND module:\"cool_module\""
                + " AND specialization:\"cool_specialization\" AND domain:\"cool_domain\""
                + " AND educational_context:\"preschool\" AND (visibility:\"public\")";

        testSearch(PYTHAGORAS, tokenizedQuery, searchFilter, M9_M2_P1);
    }

    @Test
    public void searchWithTaxonPreschoolSubtopicFilter() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setTaxons(SUBTOPIC_TOPIC_DOMAIN_EDUC_CONT);
        String tokenizedQuery = "((pythagoras) OR (\"pythagoras\")) AND subtopic:\"cool_subtopic\" AND topic:\"cool_topic\""
                + " AND domain:\"cool_domain\" AND educational_context:\"preschool\" AND (visibility:\"public\")";

        testSearch(PYTHAGORAS, tokenizedQuery, searchFilter, M9_M2_P1);
    }

    @Test
    public void searchWithTaxonBasicEducationSubtopicFilter() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setTaxons(SUBTOPIC_TOPIC_SUBJECT_DOMAIN_EDUC_CONT);
        String tokenizedQuery = "((pythagoras) OR (\"pythagoras\")) AND subtopic:\"cool_subtopic\" AND topic:\"cool_topic\""
                + " AND subject:\"cool_subject\" AND domain:\"cool_domain\" AND educational_context:\"preschool\""
                + " AND (visibility:\"public\")";

        testSearch(PYTHAGORAS, tokenizedQuery, searchFilter, M9_M2_P1);
    }

    @Test
    public void searchWithTaxonVocationalEducationSubtopicFilter() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setTaxons(SUBTOPIC_TOPIC_MODULE_SPECIAL_DOMAIN_EDUC_CONT);

        String tokenizedQuery = "((pythagoras) OR (\"pythagoras\")) AND subtopic:\"cool_subtopic\" AND topic:\"cool_topic\""
                + " AND module:\"cool_module\" AND specialization:\"cool_specialization\" AND domain:\"cool_domain\""
                + " AND educational_context:\"preschool\" AND (visibility:\"public\")";

        testSearch(PYTHAGORAS, tokenizedQuery, searchFilter, M9_M2_P1);
    }

    @Test
    public void searchWithPaidFilterFalse() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPaid(false);
        String tokenizedQuery = "((textbooks) OR (\"textbooks\")) AND (paid:\"false\" OR type:\"portfolio\")"
                + " AND (visibility:\"public\")";

        testSearch(TEXTBOOKS, tokenizedQuery, searchFilter, M9_M2_P1);
    }

    @Test
    public void searchWithPaidFilterTrue() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPaid(true);
        String tokenizedQuery = "((textbooks) OR (\"textbooks\")) AND (visibility:\"public\")";

        testSearch(TEXTBOOKS, tokenizedQuery, searchFilter, M9_M2_P1);
    }

    @Test
    public void searchWithTypeFilter() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setType(MATERIAL);
        String tokenizedQuery = "((sky) OR (\"sky\")) AND type:\"material\" AND (visibility:\"public\")";

        testSearch(SKY, tokenizedQuery, searchFilter, M9_M2_P1);
    }

    @Test
    public void searchWithTypeFilterAll() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setType("all");
        String tokenizedQuery = "((sky) OR (\"sky\")) AND (type:\"material\" OR type:\"portfolio\") AND (visibility:\"public\")";

        testSearch(SKY, tokenizedQuery, searchFilter, M9_M2_P1);
    }

    @Test
    public void searchWithTaxonEducationalContextAndPaidFilter() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setTaxons(EDUCATIONAL_CONTEXT);
        searchFilter.setPaid(false);
        String tokenizedQuery = "((pythagoras) OR (\"pythagoras\")) AND educational_context:\"preschool\" AND (paid:\"false\" OR type:\"portfolio\")"
                + " AND (visibility:\"public\")";

        testSearch(PYTHAGORAS, tokenizedQuery, searchFilter, M9_M2_P1);
    }

    @Test
    public void searchWithTaxonEducationalContextAndTypeFilter() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setTaxons(EDUCATIONAL_CONTEXT);
        searchFilter.setType(PORTFOLIO);
        String tokenizedQuery = "((pythagoras) OR (\"pythagoras\")) AND educational_context:\"preschool\" AND type:\"portfolio\""
                + " AND (visibility:\"public\")";

        testSearch(PYTHAGORAS, tokenizedQuery, searchFilter, M9_M2_P1);
    }

    @Test
    public void searchWithPaidAndTypeFilter() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPaid(false);
        searchFilter.setType(MATERIAL);
        String tokenizedQuery = "((pythagoras) OR (\"pythagoras\")) AND (paid:\"false\" OR type:\"portfolio\") AND type:\"material\""
                + " AND (visibility:\"public\")";

        testSearch(PYTHAGORAS, tokenizedQuery, searchFilter, M9_M2_P1);
    }

    @Test
    public void searchWithAllFilters() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setTaxons(TOPIC_SUBJECT_DOMAIN_EDUC_CONT);
        searchFilter.setPaid(false);
        searchFilter.setType(MATERIAL);
        searchFilter.setResourceType(resourceType());
        searchFilter.setSpecialEducation(true);
        searchFilter.setCrossCurricularThemes(CROSS_CURRICULAR_THEMES);
        searchFilter.setKeyCompetences(KEY_COMPETENCE);
        searchFilter.setIssuedFrom(2010);

        String tokenizedQuery = "((pythagoras) OR (\"pythagoras\")) AND topic:\"cool_topic\" AND subject:\"cool_subject\""
                + " AND domain:\"cool_domain\" AND educational_context:\"preschool\""
                + " AND (paid:\"false\" OR type:\"portfolio\") AND type:\"material\" AND resource_type:\"extrabook\""
                + " AND special_education:\"true\""
                + " AND (issue_date_year:[2010 TO *] OR (added:[2010-01-01T00:00:00Z TO *] AND type:\"portfolio\"))"
                + " AND cross_curricular_theme:\"test_theme\""
                + " AND key_competence:\"test_competence\" AND (visibility:\"public\")";

        testSearch(PYTHAGORAS, tokenizedQuery, searchFilter, M9_M2_P1);
    }

    @Test
    public void searchWithLanguageFilter() {
        SearchFilter searchFilter = new SearchFilter();
        Language language = language();
        searchFilter.setLanguage(language);
        String tokenizedQuery = "((alpha beta) OR (\"alpha beta\")) AND (language:\"mmm\" OR type:\"portfolio\")"
                + " AND (visibility:\"public\")";

        testSearch(ALPHA_BETA, tokenizedQuery, searchFilter, M9_M2_P1);
    }

    @Test
    public void searchWithTargetGroupFilter() {
        SearchFilter searchFilter = new SearchFilter();

        TargetGroup targetGroupSixSeven = sixSeven();

        expect(targetGroupService.getByName(TargetGroupEnum.SIX_SEVEN.name())).andReturn(targetGroupSixSeven);

        replay(targetGroupService);

        searchFilter.setTargetGroups(Collections.singletonList(targetGroupService.getByName(TargetGroupEnum.SIX_SEVEN.name())));

        verify(targetGroupService);

        String tokenizedQuery = "((umm) OR (\"umm\")) AND target_group:\"2\" AND (visibility:\"public\")";

        testSearch(UMM, tokenizedQuery, searchFilter, M9_M2_P1);
    }

    @Test
    public void searchWithMultipleTargetGroupsFilter() {
        SearchFilter searchFilter = new SearchFilter();

        expect(targetGroupService.getByName(TargetGroupEnum.SIX_SEVEN.name())).andReturn(sixSeven());
        expect(targetGroupService.getByName(TargetGroupEnum.ZERO_FIVE.name())).andReturn(zeroFive());

        replay(targetGroupService);

        searchFilter.setTargetGroups(Arrays.asList(targetGroupService.getByName(TargetGroupEnum.SIX_SEVEN.name()),
                targetGroupService.getByName(TargetGroupEnum.ZERO_FIVE.name())));

        verify(targetGroupService);

        String tokenizedQuery = "((umm) OR (\"umm\")) AND (target_group:\"2\" OR target_group:\"1\")"
                + " AND (visibility:\"public\")";

        testSearch(UMM, tokenizedQuery, searchFilter, M9_M2_P1);
    }

    @Test
    public void searchWithResourceType() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setResourceType(resourceType());
        String tokenizedQuery = "((test) OR (\"test\")) AND resource_type:\"extrabook\" AND (visibility:\"public\")";

        testSearch(TEST, tokenizedQuery, searchFilter, M9_M2_P1);
    }

    @Test
    public void searchWithSpecialEducation() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setSpecialEducation(true);
        String tokenizedQuery = "((test) OR (\"test\")) AND special_education:\"true\" AND (visibility:\"public\")";

        testSearch(TEST, tokenizedQuery, searchFilter, M9_M2_P1);
    }

    @Test
    public void searchWithCrossCurricularTheme() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setCrossCurricularThemes(CROSS_CURRICULAR_THEMES);
        String tokenizedQuery = "((test) OR (\"test\")) AND cross_curricular_theme:\"test_theme\" AND (visibility:\"public\")";

        testSearch(TEST, tokenizedQuery, searchFilter, M9_M2_P1);
    }

    @Test
    public void searchWithKeyCompetence() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setKeyCompetences(KEY_COMPETENCE);
        String tokenizedQuery = "((test) OR (\"test\")) AND key_competence:\"test_competence\" AND (visibility:\"public\")";

        testSearch(TEST, tokenizedQuery, searchFilter, M9_M2_P1);
    }

    @Test
    public void searchWithKeyCompetenceAsAdmin() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setKeyCompetences(KEY_COMPETENCE);
        String tokenizedQuery = "((test) OR (\"test\")) AND key_competence:\"test_competence\" AND (visibility:\"public\" OR visibility:\"not_listed\" OR visibility:\"private\")";

        searchFilter.setRequestingUser(ADMIN);

        testSearch(TEST, tokenizedQuery, null, M9_M2, ZERO, null, searchFilter, ADMIN);
    }

    @Test
    public void searchWithIssueDate() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setIssuedFrom(2012);
        String tokenizedQuery = "((airplane) OR (\"airplane\")) AND (issue_date_year:[2012 TO *]"
                + " OR (added:[2012-01-01T00:00:00Z TO *] AND type:\"portfolio\"))"
                + " AND (visibility:\"public\")";

        testSearch(AIRPLANE, tokenizedQuery, searchFilter, M9_M2_P1);
    }

    @Test
    public void searchWithSorting() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setSort("somefield");
        searchFilter.setSortDirection(SearchFilter.SortDirection.DESCENDING);
        String tokenizedQuery = "((english language) OR (\"english language\")) AND (visibility:\"public\")";
        String expectedSort = "somefield desc";

        testSearch(ENGLISH_LANGUAGE, tokenizedQuery, expectedSort, searchFilter, M9_M2_P1);
    }

    @Test
    public void searchWithCurriculumLiteratureTrue() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setCurriculumLiterature(true);
        String tokenizedQuery = "((german language) OR (\"german language\")) AND (peerReview:[* TO *] OR curriculum_literature:\"true\")"
                + " AND (visibility:\"public\")";

        testSearch(GERMAN_LANGUAGE, tokenizedQuery, searchFilter, M9_M2_P1);
    }

    @Test
    public void searchWithCurriculumLiteratureFalse() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setCurriculumLiterature(false);
        String tokenizedQuery = "((german language) OR (\"german language\")) AND (visibility:\"public\")";

        testSearch(GERMAN_LANGUAGE, tokenizedQuery, searchFilter, M9_M2_P1);
    }

    @Test
    public void searchWithLimit() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setCurriculumLiterature(false);
        String tokenizedQuery = "((german language) OR (\"german language\")) AND (visibility:\"public\")";

        testSearch(GERMAN_LANGUAGE, tokenizedQuery, null, M9_M2_P1, ZERO, _3, searchFilter, null);
    }

    @Test
    public void searchNotFromStart() {
        String tokenizedQuery = "((people) OR (\"people\")) AND (visibility:\"public\")";
        SearchFilter searchFilter = new SearchFilter();

        testSearch(PEOPLE, tokenizedQuery, null, M9_M2_P1, _50, null, searchFilter, null);
    }

    @Test
    public void searchHasMoreResultsThanMaxResultsPerPage() {
        String tokenizedQuery = "((people) OR (\"people\")) AND (visibility:\"public\")";
        SearchFilter searchFilter = new SearchFilter();

        testSearch(PEOPLE, tokenizedQuery, null, M9_M2_P1, ZERO, null, _1450, searchFilter, null);
    }

    @Test
    public void searchReturnsOnlyMaterials() {
        String tokenizedQuery = "((people) OR (\"people\")) AND (visibility:\"public\")";
        SearchFilter searchFilter = new SearchFilter();

        testSearch(PEOPLE, tokenizedQuery, searchFilter, M9_M2);
    }

    @Test
    public void searchReturnsOnlyPortfolios() {
        String tokenizedQuery = "((people) OR (\"people\")) AND (visibility:\"public\")";
        SearchFilter searchFilter = new SearchFilter();

        testSearch(PEOPLE, tokenizedQuery, searchFilter, P9_P2);
    }

    private void testSearch(String query, String tokenizedQuery, String expectedSort, SearchFilter searchFilter, List<Searchable> searchables) {
        testSearch(query, tokenizedQuery, expectedSort, searchables, ZERO, null, searchFilter, null);
    }

    private void testSearch(String query, String tokenizedQuery, SearchFilter searchFilter, List<Searchable> searchables) {
        testSearch(query, tokenizedQuery, null, searchables, ZERO, null, searchFilter, null);
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

    private void testSearch(String query, String tokenizedQuery, String expectedSort, List<Searchable> searchables,
                            long start, Long limit, long totalResults, SearchFilter searchFilter, User loggedInUser) {
        SearchResponse searchResponse = createSearchResponseWithDocuments(searchables, start, totalResults);

        List<ReducedLearningObject> learningObjects = new ArrayList<>();
        List<Long> learningObjectIdentifiers = getIdentifiers(searchables);

        learningObjects.addAll(collectMaterialsFrom(searchables));
        learningObjects.addAll(collectPortfoliosFrom(searchables));

        if (limit == null) {
            expect(solrEngineService.search(tokenizedQuery, start, expectedSort)).andReturn(searchResponse);
        } else {
            expect(solrEngineService.search(tokenizedQuery, start, expectedSort, limit)).andReturn(searchResponse);
        }

        if (StringUtils.isBlank(query) && searchFilter.isEmptySearch()) {
            expect(learningObjectDao.findAllNotDeleted()).andReturn(totalResults);
        }
        expect(reducedLearningObjectDao.findAllById(learningObjectIdentifiers)).andReturn(learningObjects);
        if (loggedInUser != null) {
            for (Long id : learningObjectIdentifiers) {
                expect(userFavoriteDao.findFavoriteByUserAndLearningObject(id, loggedInUser)).andReturn(null);
            }
        }

        replayAll();

        SearchResult result = searchService.search(query, start, limit, searchFilter);

        verifyAll();

        assertEquals(totalResults, result.getTotalResults());
        assertSameSearchable(searchables, result.getItems());
        assertEquals(start, result.getStart());

    }

    private void assertSameSearchable(List<Searchable> expected, List<Searchable> actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertSame(expected.get(i), actual.get(i));
        }
    }

    private void replayAll() {
        replay(solrEngineService, learningObjectDao, userFavoriteDao, reducedLearningObjectDao);
    }

    private void verifyAll() {
        verify(solrEngineService, learningObjectDao, userFavoriteDao, reducedLearningObjectDao);
    }
}
