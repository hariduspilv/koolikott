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
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.SearchFilter;
import ee.hm.dop.model.SearchResult;
import ee.hm.dop.model.Searchable;
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
    public void searchEmptyQueryAndSubjectFilter() {
        String query = "";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setSubject("testSubject");
        String tokenizedQuery = "subject:\"testsubject\"";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(3L), createMaterial(4L));

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
    public void searchNullQueryAndSubjectFilter() {
        String query = null;
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setSubject("testSubject");
        String tokenizedQuery = "subject:\"testsubject\"";
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
    public void searchWithSubjectFilter() {
        String query = "airplane";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setSubject("Mathematics");
        String tokenizedQuery = "(airplane*) AND subject:\"mathematics\"";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    @Test
    public void searchWithResourceTypeFilter() {
        String query = "pythagoras";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setResourceType("TEXTBOOK");
        String tokenizedQuery = "(pythagoras*) AND resource_type:\"textbook\"";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(15L), createMaterial(8L), createPortfolio(214L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    @Test
    public void searchWithSubjectAndResourceTypeFilter() {
        String query = "pythagoras";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setSubject("Mathematics");
        searchFilter.setResourceType("TEXTBOOK");
        String tokenizedQuery = "(pythagoras*) AND subject:\"mathematics\" AND resource_type:\"textbook\"";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    @Test
    public void searchWithEducationalContextFilter() {
        String query = "pythagoras";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setEducationalContext("PRESCHOOL");
        String tokenizedQuery = "(pythagoras*) AND educational_context:\"preschool\"";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    @Test
    public void searchWithSubjectAndEducationalContextFilter() {
        String query = "pythagoras";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setSubject("Mathematics");
        searchFilter.setEducationalContext("PRESCHOOL");
        String tokenizedQuery = "(pythagoras*) AND subject:\"mathematics\" AND educational_context:\"preschool\"";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    @Test
    public void searchWithResourceTypeAndEducationalContextFilter() {
        String query = "pythagoras";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setResourceType("TEXTBOOK");
        searchFilter.setEducationalContext("PRESCHOOL");
        String tokenizedQuery = "(pythagoras*) AND resource_type:\"textbook\" AND educational_context:\"preschool\"";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    @Test
    public void searchWithSubjectAndResourceTypeAndEducationalContextFilters() {
        String query = "pythagoras";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setSubject("Mathematics");
        searchFilter.setResourceType("TEXTBOOK");
        searchFilter.setEducationalContext("PRESCHOOL");
        String tokenizedQuery = "(pythagoras*) AND subject:\"mathematics\" AND resource_type:\"textbook\""
                + " AND educational_context:\"preschool\"";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    // Tests with License Type

    @Test
    public void searchWithLicenseTypeFilter() {
        String query = "airplane";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setLicenseType("CCBY");
        String tokenizedQuery = "(airplane*) AND license_type:\"ccby\"";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    @Test
    public void searchWithSubjectAndLicenseTypeFilter() {
        String query = "airplane";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setSubject("Mathematics");
        searchFilter.setLicenseType("CCSA");
        String tokenizedQuery = "(airplane*) AND subject:\"mathematics\" AND license_type:\"ccsa\"";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    @Test
    public void searchWithResourceTypeAndLicenseTypeFilter() {
        String query = "pythagoras";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setResourceType("TEXTBOOK");
        searchFilter.setLicenseType("CCBYSA");
        String tokenizedQuery = "(pythagoras*) AND resource_type:\"textbook\" AND license_type:\"ccbysa\"";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    @Test
    public void searchWithEducationalContextAndLicenseTypeFilter() {
        String query = "pythagoras";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setEducationalContext("PRESCHOOL");
        searchFilter.setLicenseType("CCBYNC");
        String tokenizedQuery = "(pythagoras*) AND educational_context:\"preschool\" AND license_type:\"ccbync\"";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    @Test
    public void searchWithSubjectAndResourceTypeAndLicenseTypeFilter() {
        String query = "pythagoras";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setSubject("Mathematics");
        searchFilter.setResourceType("TEXTBOOK");
        searchFilter.setLicenseType("CC");
        String tokenizedQuery = "(pythagoras*) AND subject:\"mathematics\" AND resource_type:\"textbook\""
                + " AND license_type:\"cc\"";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    @Test
    public void searchWithSubjectAndEducationalContextAndLicenseTypeFilter() {
        String query = "pythagoras";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setSubject("Mathematics");
        searchFilter.setEducationalContext("PRESCHOOL");
        searchFilter.setLicenseType("CCBY");
        String tokenizedQuery = "(pythagoras*) AND subject:\"mathematics\" AND educational_context:\"preschool\" "
                + "AND license_type:\"ccby\"";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    @Test
    public void searchWithResourceTypeAndEducationalContextAndLicenseTypeFilter() {
        String query = "pythagoras";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setResourceType("TEXTBOOK");
        searchFilter.setEducationalContext("PRESCHOOL");
        searchFilter.setLicenseType("CCBYNCND");
        String tokenizedQuery = "(pythagoras*) AND resource_type:\"textbook\" AND educational_context:\"preschool\" "
                + "AND license_type:\"ccbyncnd\"";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    @Test
    public void searchWithAllFilters() {
        String query = "pythagoras";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setSubject("Mathematics");
        searchFilter.setResourceType("TEXTBOOK");
        searchFilter.setEducationalContext("PRESCHOOL");
        searchFilter.setLicenseType("CC");
        searchFilter.setAuthor("Mary");
        searchFilter.setCombinedDescription("This is description.");
        searchFilter.setPaid(false);
        searchFilter.setType("material");
        String tokenizedQuery = "(pythagoras*) AND subject:\"mathematics\" AND resource_type:\"textbook\""
                + " AND educational_context:\"preschool\" AND license_type:\"cc\""
                + " AND author:\"mary\" AND (description:\"this\\ is\\ description.\" OR summary:\"this\\ is\\ description.\")"
                + " AND (paid:\"false\" OR type:\"portfolio\") AND type:\"material\"";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    // Tests with author
    @Test
    public void searchWithAuthorFilter() {
        String query = "books";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setAuthor("John Author");
        String tokenizedQuery = "(books*) AND author:\"john\\ author\"";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    @Test
    public void searchWithLicenseTypeAndAuthorFilter() {
        String query = "many books";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setLicenseType("CC");
        searchFilter.setAuthor("John Author");
        String tokenizedQuery = "(many* books*) AND license_type:\"cc\" AND author:\"john\\ author\"";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    @Test
    public void searchWithSubjectAndAuthorFilter() {
        String query = "some books";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setSubject("Interesting");
        searchFilter.setAuthor("John");
        String tokenizedQuery = "(some* books*) AND subject:\"interesting\" AND author:\"john\"";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    // Tests with combined description

    @Test
    public void searchWithCombinedDescriptionFilter() {
        String query = "querytest";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setCombinedDescription("Lorem ipsum.");
        String tokenizedQuery = "(querytest*) AND (description:\"lorem\\ ipsum.\" OR summary:\"lorem\\ ipsum.\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    @Test
    public void searchWithSubjectAndAuthorAndCombinedDescriptionFilter() {
        String query = "querytest";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setSubject("English");
        searchFilter.setAuthor("Lewis");
        searchFilter.setCombinedDescription("Lorem ipsum.");
        String tokenizedQuery = "(querytest*) AND subject:\"english\" AND author:\"lewis\""
                + " AND (description:\"lorem\\ ipsum.\" OR summary:\"lorem\\ ipsum.\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    @Test
    public void searchWithEducationalContextAndLicenseTypeAndCombinedDescriptionFilter() {
        String query = "querytest";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setEducationalContext("OTHER");
        searchFilter.setLicenseType("CCBY");
        searchFilter.setCombinedDescription("What is this about.");
        String tokenizedQuery = "(querytest*) AND educational_context:\"other\" AND license_type:\"ccby\""
                + " AND (description:\"what\\ is\\ this\\ about.\" OR summary:\"what\\ is\\ this\\ about.\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    // Tests with paid/free

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
    public void searchWithResourceTypeAndAuthorAndPaidFilterFalse() {
        String query = "story";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setResourceType("pdf");
        searchFilter.setAuthor("Mr. Writer");
        searchFilter.setPaid(false);
        String tokenizedQuery = "(story*) AND resource_type:\"pdf\" AND author:\"mr.\\ writer\" AND (paid:\"false\" OR type:\"portfolio\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    @Test
    public void searchWithSubjectAndPaidFilterFalse() {
        String query = "story";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setSubject("astronomy");
        searchFilter.setPaid(false);
        String tokenizedQuery = "(story*) AND subject:\"astronomy\" AND (paid:\"false\" OR type:\"portfolio\")";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    // Tests with type

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
    public void searchWithResourceTypeAndTypeFilter() {
        String query = "sky";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setResourceType("something");
        searchFilter.setType("portfolio");
        String tokenizedQuery = "(sky) AND resource_type:\"something\" AND type:\"portfolio\"";
        long start = 0;

        List<Searchable> searchables = Arrays.asList(createMaterial(9L), createMaterial(2L), createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, searchFilter);
    }

    @Test
    public void searchWithSubjectAndPaidFalseAndTypeFilter() {
        String query = "sky";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setSubject("ufo");
        searchFilter.setType("material");
        searchFilter.setPaid(false);
        String tokenizedQuery = "(sky) AND subject:\"ufo\" AND (paid:\"false\" OR type:\"portfolio\") AND type:\"material\"";
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
