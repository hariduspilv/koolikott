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

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import ee.hm.dop.dao.MaterialDAO;
import ee.hm.dop.dao.PortfolioDAO;
import ee.hm.dop.model.Domain;
import ee.hm.dop.model.EducationalContext;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.SearchFilter;
import ee.hm.dop.model.SearchResult;
import ee.hm.dop.model.Searchable;
import ee.hm.dop.model.Subject;
import ee.hm.dop.model.solr.Document;
import ee.hm.dop.model.solr.Response;
import ee.hm.dop.model.solr.SearchResponse;

@RunWith(EasyMockRunner.class)
public class SearchServiceTest {

    @Mock
    private SearchEngineService searchEngineService;

    @Mock
    private MaterialDAO materialDAO;

    @Mock
    private PortfolioDAO portfolioDAO;

    @TestSubject
    private SearchService searchService = new SearchService();

    @Test
    public void search() {
        String query = "people";
        String tokenizedQuery = "people*";
        SearchFilter searchFilter = new SearchFilter();
        long start = 0;
        List<Searchable> searchables = Arrays.asList(createMaterial(7L), createMaterial(1L), createPortfolio(4L),
                createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    // To test asynchronous problems that may occur when search returns deleted
    // materials
    @Test
    public void searchWhenDatabaseReturnsLessValuesThanSearch() {
        String query = "people";
        String tokenizedQuery = "people*";
        SearchFilter searchFilter = new SearchFilter();
        long start = 10;

        List<Searchable> searchables = new ArrayList<>();
        searchables.add(createMaterial(7L));
        searchables.add(createMaterial(4L));
        searchables.add(createPortfolio(2L));

        Material material1 = createMaterial(1L);
        searchables.add(material1);

        Portfolio portfolio2 = createPortfolio(2L);
        searchables.add(portfolio2);

        SearchResponse searchResponse = createSearchResponseWithDocuments(searchables, start, searchables.size());
        List<Material> materials = collectMaterialsFrom(searchables);
        List<Long> materialIdentifiers = getIdentifiers(materials);
        materials.remove(material1);

        List<Portfolio> portfolios = collectPortfoliosFrom(searchables);
        List<Long> portfoliosIdentifiers = getIdentifiers(portfolios);
        portfolios.remove(portfolio2);

        // Have to remove from searchables as well so we can compare the return
        // with the expected
        searchables.remove(material1);
        searchables.remove(portfolio2);

        expect(searchEngineService.search(tokenizedQuery, start)).andReturn(searchResponse);
        expect(materialDAO.findAllById(materialIdentifiers)).andReturn(materials);
        expect(portfolioDAO.findAllById(portfoliosIdentifiers)).andReturn(portfolios);

        replayAll();

        SearchResult result = searchService.search(query, start, searchFilter);

        verifyAll();

        assertSameSearchable(searchables, result.getItems());
        assertEquals(searchables.size(), result.getTotalResults());
        assertEquals(start, result.getStart());
    }

    @Test
    public void searchNoResult() {
        String query = "people";
        String tokenizedQuery = "people*";
        long start = 0;

        SearchResponse searchResponse = createSearchResponseWithDocuments(new ArrayList<>(), 0, 0);

        expect(searchEngineService.search(tokenizedQuery, 0)).andReturn(searchResponse);

        replayAll();

        List<Searchable> result = searchService.search(query, start).getItems();

        verifyAll();

        assertEquals(0, result.size());
    }

    @Test
    public void searchTokenizeQuery() {
        String query = "people 123";
        String tokenizedQuery = "people* 123";
        long start = 0;
        SearchResponse searchResponse = new SearchResponse();

        expect(searchEngineService.search(tokenizedQuery, start)).andReturn(searchResponse);

        replayAll();

        searchService.search(query, start);

        verifyAll();
    }

    @Test
    public void searchTokenizeQueryExactMatch() {
        String query = "\"people 123\"";
        String tokenizedQuery = "\"people\\ 123\"";
        long start = 0;
        SearchResponse searchResponse = new SearchResponse();

        expect(searchEngineService.search(tokenizedQuery, start)).andReturn(searchResponse);

        replayAll();

        searchService.search(query, start);

        verifyAll();
    }

    @Test
    public void searchTokenizeQueryEvenQuotes() {
        String query = "\"people 123\" blah\"";
        String tokenizedQuery = "\"people\\ 123\" blah\\\"*";
        long start = 0;
        SearchResponse searchResponse = new SearchResponse();

        expect(searchEngineService.search(tokenizedQuery, start)).andReturn(searchResponse);

        replayAll();

        searchService.search(query, start);

        verifyAll();
    }

    @Test
    public void searchEmptyQuery() {
        replayAll();

        SearchResult result = null;
        try {
            result = searchService.search("", 0);
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
        String tokenizedQuery = "educational_context:\"preschool\"";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(3L), createMaterial(4L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    @Test
    public void searchWithEmptyQueryAndTypeFilterAll() {
        String query = "";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setType("all");
        String tokenizedQuery = "(type:\"material\" OR type:\"portfolio\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    @Test
    public void searchNullQueryAndNullFilters() {
        replayAll();

        SearchResult result = null;
        try {
            result = searchService.search(null, 0);
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
        String tokenizedQuery = "educational_context:\"context\"";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(3L), createMaterial(4L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    @Test
    public void searchWithFiltersNull() {
        String query = "airplane";
        SearchFilter searchFilter = new SearchFilter();
        String tokenizedQuery = "airplane*";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    @Test
    public void searchWithTaxonFilter() {
        String query = "pythagoras";
        SearchFilter searchFilter = new SearchFilter();
        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setId(2L);
        educationalContext.setName("PRESCHOOL");
        searchFilter.setTaxon(educationalContext);
        String tokenizedQuery = "(pythagoras*) AND educational_context:\"preschool\"";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
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
        String tokenizedQuery = "(pythagoras*) AND domain:\"cool_domain\" AND educational_context:\"preschool\"";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    @Test
    public void searchWithTaxonSubjctFilter() {
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

        String tokenizedQuery = "(pythagoras*) AND subject:\"cool_subject\" AND domain:\"cool_domain\" AND educational_context:\"preschool\"";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    @Test
    public void searchWithPaidFilterFalse() {
        String query = "textbooks";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPaid(false);
        String tokenizedQuery = "(textbooks*) AND (paid:\"false\" OR type:\"portfolio\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    @Test
    public void searchWithPaidFilterTrue() {
        String query = "textbooks";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPaid(true);
        String tokenizedQuery = "textbooks*";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    @Test
    public void searchWithTypeFilter() {
        String query = "sky";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setType("material");
        String tokenizedQuery = "(sky) AND type:\"material\"";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    @Test
    public void searchWithTypeFilterAll() {
        String query = "sky";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setType("all");
        String tokenizedQuery = "(sky) AND (type:\"material\" OR type:\"portfolio\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
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
        String tokenizedQuery = "(pythagoras*) AND educational_context:\"preschool\" AND (paid:\"false\" OR type:\"portfolio\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
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
        String tokenizedQuery = "(pythagoras*) AND educational_context:\"preschool\" AND type:\"portfolio\"";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    @Test
    public void searchWithPaidAndTypeFilter() {
        String query = "pythagoras";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPaid(false);
        searchFilter.setType("material");
        String tokenizedQuery = "(pythagoras*) AND (paid:\"false\" OR type:\"portfolio\") AND type:\"material\"";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
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

        searchFilter.setTaxon(subject);

        searchFilter.setPaid(false);
        searchFilter.setType("material");
        String tokenizedQuery = "(pythagoras*) AND subject:\"cool_subject\" AND domain:\"cool_domain\" AND educational_context:\"preschool\""
                + " AND (paid:\"false\" OR type:\"portfolio\") AND type:\"material\"";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    @Test
    public void searchWithLanguageFilter() {
        String query = "alpha beta";
        SearchFilter searchFilter = new SearchFilter();
        Language language = new Language();
        language.setCode("mmm");
        searchFilter.setLanguage(language);
        String tokenizedQuery = "(alpha* beta*) AND (language:\"mmm\" OR type:\"portfolio\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    @Test
    public void searchNotFromStart() {
        String query = "people";
        String tokenizedQuery = "people*";
        SearchFilter searchFilter = new SearchFilter();
        long start = 50;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    @Test
    public void searchHasMoreResultsThanMaxResultsPerPage() {
        String query = "people";
        String tokenizedQuery = "people*";
        SearchFilter searchFilter = new SearchFilter();
        long start = 0;
        long totalResults = 1450;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, totalResults, searchFilter);
    }

    @Test
    public void searchReturnsOnlyMaterials() {
        String query = "people";
        String tokenizedQuery = "people*";
        SearchFilter searchFilter = new SearchFilter();
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    @Test
    public void searchReturnsOnlyPortfolios() {
        String query = "people";
        String tokenizedQuery = "people*";
        SearchFilter searchFilter = new SearchFilter();
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createPortfolio(9L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    @Test
    public void searchReturnsMaterialAndPortfoliosWithSameIds() {
        String query = "people";
        String tokenizedQuery = "people*";
        SearchFilter searchFilter = new SearchFilter();
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createPortfolio(9L), createMaterial(9L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    private void testSearch(String query, String tokenizedQuery, List<Searchable> searchables, long start,
            long totalResults, SearchFilter searchFilter) {
        SearchResponse searchResponse = createSearchResponseWithDocuments(searchables, start, totalResults);
        List<Material> materials = collectMaterialsFrom(searchables);
        List<Long> materialIdentifiers = getIdentifiers(materials);
        List<Portfolio> portfolios = collectPortfoliosFrom(searchables);
        List<Long> portfoliosIdentifiers = getIdentifiers(portfolios);

        expect(searchEngineService.search(tokenizedQuery, start)).andReturn(searchResponse);

        if (!materialIdentifiers.isEmpty()) {
            expect(materialDAO.findAllById(materialIdentifiers)).andReturn(materials);
        }

        if (!portfoliosIdentifiers.isEmpty()) {
            expect(portfolioDAO.findAllById(portfoliosIdentifiers)).andReturn(portfolios);
        }

        replayAll();

        SearchResult result = searchService.search(query, start, searchFilter);

        verifyAll();

        assertSameSearchable(searchables, result.getItems());
        assertEquals(totalResults, result.getTotalResults());
        assertEquals(start, result.getStart());
    }

    private void testSearch(String query, String tokenizedQuery, List<Searchable> searchables, long start,
            SearchFilter searchFilter) {
        testSearch(query, tokenizedQuery, searchables, start, searchables.size(), searchFilter);
    }

    private List<Long> getIdentifiers(List<? extends Searchable> searchables) {
        List<Long> identifiers = new ArrayList<>();
        searchables.stream().forEach(s -> identifiers.add(s.getId()));
        return identifiers;
    }

    private SearchResponse createSearchResponseWithDocuments(List<Searchable> searchables, long start,
            long totalResults) {
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
        replay(searchEngineService, materialDAO, portfolioDAO);
    }

    private void verifyAll() {
        verify(searchEngineService, materialDAO, portfolioDAO);
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
