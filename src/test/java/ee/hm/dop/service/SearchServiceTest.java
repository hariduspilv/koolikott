package ee.hm.dop.service;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
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
        String subject = null;
        String resourceType = null;
        String educationalContext = null;
        String licenseType = null;
        long start = 0;
        List<Searchable> searchables = new ArrayList<>();
        searchables.add(createMaterial(7L));
        searchables.add(createMaterial(1L));
        searchables.add(createMaterial(4L));
        searchables.add(createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, subject, resourceType, educationalContext, licenseType);
    }

    // To test asynchronous problems that may occur when search returns deleted
    // materials
    @Test
    public void searchWhenDatabaseReturnsLessValuesThanSearch() {
        String query = "people";
        String tokenizedQuery = "people*";
        String subject = null;
        String resourceType = null;
        String educationalContext = null;
        String licenseType = null;
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

        SearchResult result = searchService.search(query, start, subject, resourceType, educationalContext,
                licenseType);

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
        String query = "";
        String subject = null;
        String resourceType = null;
        String educationalContext = null;
        String licenseType = null;
        String tokenizedQuery = "";
        long start = 0;

        List<Searchable> searchables = new ArrayList<>();

        testSearch(query, tokenizedQuery, searchables, start, subject, resourceType, educationalContext, licenseType);
    }

    @Test
    public void searchEmptyQueryAndSubjectFilter() {
        String query = "";
        String subject = "testsubject";
        String resourceType = null;
        String educationalContext = null;
        String licenseType = null;
        String tokenizedQuery = "subject:\"testsubject\"";
        long start = 0;

        List<Searchable> searchables = new ArrayList<>();
        searchables.add(createMaterial(3L));
        searchables.add(createMaterial(4L));

        testSearch(query, tokenizedQuery, searchables, start, subject, resourceType, educationalContext, licenseType);
    }

    @Test
    public void searchNullQueryAndNullFilters() {
        String query = null;
        String subject = null;
        String resourceType = null;
        String educationalContext = null;
        String licenseType = null;
        String tokenizedQuery = "";
        long start = 0;

        List<Searchable> searchables = new ArrayList<>();

        testSearch(query, tokenizedQuery, searchables, start, subject, resourceType, educationalContext, licenseType);
    }

    @Test
    public void searchNullQueryAndSubjectFilter() {
        String query = null;
        String subject = "testsubject";
        String resourceType = null;
        String educationalContext = null;
        String licenseType = null;
        String tokenizedQuery = "subject:\"testsubject\"";
        long start = 0;

        List<Searchable> searchables = new ArrayList<>();
        searchables.add(createMaterial(3L));
        searchables.add(createMaterial(4L));

        testSearch(query, tokenizedQuery, searchables, start, subject, resourceType, educationalContext, licenseType);
    }

    @Test
    public void searchWithFiltersNull() {
        String query = "airplane";
        String subject = null;
        String resourceType = null;
        String educationalContext = null;
        String licenseType = null;
        String tokenizedQuery = "airplane*";
        long start = 0;

        List<Searchable> searchables = new ArrayList<>();
        searchables.add(createMaterial(9L));
        searchables.add(createMaterial(2L));
        searchables.add(createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, subject, resourceType, educationalContext, licenseType);
    }

    @Test
    public void searchWithSubjectFilter() {
        String query = "airplane";
        String subject = "Mathematics";
        String resourceType = null;
        String educationalContext = null;
        String licenseType = null;
        String tokenizedQuery = "(airplane*) AND subject:\"mathematics\"";
        long start = 0;

        List<Searchable> searchables = new ArrayList<>();
        searchables.add(createMaterial(9L));
        searchables.add(createMaterial(2L));
        searchables.add(createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, subject, resourceType, educationalContext, licenseType);
    }

    @Test
    public void searchWithResourceTypeFilter() {
        String query = "pythagoras";
        String subject = null;
        String resourceType = "TEXTBOOK";
        String educationalContext = null;
        String licenseType = null;
        String tokenizedQuery = "(pythagoras*) AND resource_type:\"textbook\"";
        long start = 0;

        List<Searchable> searchables = new ArrayList<>();
        searchables.add(createMaterial(15L));
        searchables.add(createMaterial(8L));
        searchables.add(createPortfolio(214L));

        testSearch(query, tokenizedQuery, searchables, start, subject, resourceType, educationalContext, licenseType);
    }

    @Test
    public void searchWithSubjectAndResourceTypeFilter() {
        String query = "pythagoras";
        String subject = "Mathematics";
        String resourceType = "TEXTBOOK";
        String educationalContext = null;
        String licenseType = null;
        String tokenizedQuery = "(pythagoras*) AND subject:\"mathematics\" AND resource_type:\"textbook\"";
        long start = 0;

        List<Searchable> searchables = new ArrayList<>();
        searchables.add(createMaterial(9L));
        searchables.add(createMaterial(2L));
        searchables.add(createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, subject, resourceType, educationalContext, licenseType);
    }

    @Test
    public void searchWithEducationalContextFilter() {
        String query = "pythagoras";
        String subject = null;
        String resourceType = null;
        String educationalContext = "PRESCHOOL";
        String licenseType = null;
        String tokenizedQuery = "(pythagoras*) AND educational_context:\"preschool\"";
        long start = 0;

        List<Searchable> searchables = new ArrayList<>();
        searchables.add(createMaterial(9L));
        searchables.add(createMaterial(2L));
        searchables.add(createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, subject, resourceType, educationalContext, licenseType);
    }

    @Test
    public void searchWithSubjectAndEducationalContextFilter() {
        String query = "pythagoras";
        String subject = "Mathematics";
        String resourceType = null;
        String educationalContext = "PRESCHOOL";
        String licenseType = null;
        String tokenizedQuery = "(pythagoras*) AND subject:\"mathematics\" AND educational_context:\"preschool\"";
        long start = 0;

        List<Searchable> searchables = new ArrayList<>();
        searchables.add(createMaterial(9L));
        searchables.add(createMaterial(2L));
        searchables.add(createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, subject, resourceType, educationalContext, licenseType);
    }

    @Test
    public void searchWithResourceTypeAndEducationalContextFilter() {
        String query = "pythagoras";
        String subject = null;
        String resourceType = "TEXTBOOK";
        String educationalContext = "PRESCHOOL";
        String licenseType = null;
        String tokenizedQuery = "(pythagoras*) AND resource_type:\"textbook\" AND educational_context:\"preschool\"";
        long start = 0;

        List<Searchable> searchables = new ArrayList<>();
        searchables.add(createMaterial(9L));
        searchables.add(createMaterial(2L));
        searchables.add(createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, subject, resourceType, educationalContext, licenseType);
    }

    @Test
    public void searchWithSubjectAndResourceTypeAndEducationalContextFilters() {
        String query = "pythagoras";
        String subject = "Mathematics";
        String resourceType = "TEXTBOOK";
        String educationalContext = "PRESCHOOL";
        String licenseType = null;
        String tokenizedQuery = "(pythagoras*) AND subject:\"mathematics\" AND resource_type:\"textbook\" AND educational_context:\"preschool\"";
        long start = 0;

        List<Searchable> searchables = new ArrayList<>();
        searchables.add(createMaterial(9L));
        searchables.add(createMaterial(2L));
        searchables.add(createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, subject, resourceType, educationalContext, licenseType);
    }

    // Tests with License Type

    @Test
    public void searchWithLicenseTypeFilter() {
        String query = "airplane";
        String subject = null;
        String resourceType = null;
        String educationalContext = null;
        String licenseType = "CCBY";
        String tokenizedQuery = "(airplane*) AND license_type:\"ccby\"";
        long start = 0;

        List<Searchable> searchables = new ArrayList<>();
        searchables.add(createMaterial(9L));
        searchables.add(createMaterial(2L));
        searchables.add(createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, subject, resourceType, educationalContext, licenseType);
    }

    @Test
    public void searchWithSubjectAndLicenseTypeFilter() {
        String query = "airplane";
        String subject = "Mathematics";
        String resourceType = null;
        String educationalContext = null;
        String licenseType = "CCSA";
        String tokenizedQuery = "(airplane*) AND subject:\"mathematics\" AND license_type:\"ccsa\"";
        long start = 0;

        List<Searchable> searchables = new ArrayList<>();
        searchables.add(createMaterial(9L));
        searchables.add(createMaterial(2L));
        searchables.add(createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, subject, resourceType, educationalContext, licenseType);
    }

    @Test
    public void searchWithResourceTypeAndLicenseTypeFilter() {
        String query = "pythagoras";
        String subject = null;
        String resourceType = "TEXTBOOK";
        String educationalContext = null;
        String licenseType = "CCBYSA";
        String tokenizedQuery = "(pythagoras*) AND resource_type:\"textbook\" AND license_type:\"ccbysa\"";
        long start = 0;

        List<Searchable> searchables = new ArrayList<>();
        searchables.add(createMaterial(9L));
        searchables.add(createMaterial(2L));
        searchables.add(createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, subject, resourceType, educationalContext, licenseType);
    }

    @Test
    public void searchWithEducationalContextAndLicenseTypeFilter() {
        String query = "pythagoras";
        String subject = null;
        String resourceType = null;
        String educationalContext = "PRESCHOOL";
        String licenseType = "CCBYNC";
        String tokenizedQuery = "(pythagoras*) AND educational_context:\"preschool\" AND license_type:\"ccbync\"";
        long start = 0;

        List<Searchable> searchables = new ArrayList<>();
        searchables.add(createMaterial(9L));
        searchables.add(createMaterial(2L));
        searchables.add(createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, subject, resourceType, educationalContext, licenseType);
    }

    @Test
    public void searchWithSubjectAndResourceTypeAndLicenseTypeFilter() {
        String query = "pythagoras";
        String subject = "Mathematics";
        String resourceType = "TEXTBOOK";
        String educationalContext = null;
        String licenseType = "CC";
        String tokenizedQuery = "(pythagoras*) AND subject:\"mathematics\" AND resource_type:\"textbook\" AND license_type:\"cc\"";
        long start = 0;

        List<Searchable> searchables = new ArrayList<>();
        searchables.add(createMaterial(9L));
        searchables.add(createMaterial(2L));
        searchables.add(createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, subject, resourceType, educationalContext, licenseType);
    }

    @Test
    public void searchWithSubjectAndEducationalContextAndLicenseTypeFilter() {
        String query = "pythagoras";
        String subject = "Mathematics";
        String resourceType = null;
        String educationalContext = "PRESCHOOL";
        String licenseType = "CCBY";
        String tokenizedQuery = "(pythagoras*) AND subject:\"mathematics\" AND educational_context:\"preschool\" AND license_type:\"ccby\"";
        long start = 0;

        List<Searchable> searchables = new ArrayList<>();
        searchables.add(createMaterial(9L));
        searchables.add(createMaterial(2L));
        searchables.add(createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, subject, resourceType, educationalContext, licenseType);
    }

    @Test
    public void searchWithResourceTypeAndEducationalContextAndLicenseTypeFilter() {
        String query = "pythagoras";
        String subject = null;
        String resourceType = "TEXTBOOK";
        String educationalContext = "PRESCHOOL";
        String licenseType = "CCBYNCND";
        String tokenizedQuery = "(pythagoras*) AND resource_type:\"textbook\" AND educational_context:\"preschool\" AND license_type:\"ccbyncnd\"";
        long start = 0;

        List<Searchable> searchables = new ArrayList<>();
        searchables.add(createMaterial(9L));
        searchables.add(createMaterial(2L));
        searchables.add(createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, subject, resourceType, educationalContext, licenseType);
    }

    @Test
    public void searchWithAllFilters() {
        String query = "pythagoras";
        String subject = "Mathematics";
        String resourceType = "TEXTBOOK";
        String educationalContext = "PRESCHOOL";
        String licenseType = "CC";
        String tokenizedQuery = "(pythagoras*) AND subject:\"mathematics\" AND resource_type:\"textbook\" AND educational_context:\"preschool\" AND license_type:\"cc\"";
        long start = 0;

        List<Searchable> searchables = new ArrayList<>();
        searchables.add(createMaterial(9L));
        searchables.add(createMaterial(2L));
        searchables.add(createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, subject, resourceType, educationalContext, licenseType);
    }

    @Test
    public void searchNotFromStart() {
        String query = "people";
        String tokenizedQuery = "people*";
        String subject = null;
        String resourceType = null;
        String educationalContext = null;
        String licenseType = null;
        long start = 50;

        List<Searchable> searchables = new ArrayList<>();
        searchables.add(createMaterial(9L));
        searchables.add(createMaterial(2L));
        searchables.add(createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, subject, resourceType, educationalContext, licenseType);
    }

    @Test
    public void searchHasMoreResultsThanMaxResultsPerPage() {
        String query = "people";
        String tokenizedQuery = "people*";
        String subject = null;
        String resourceType = null;
        String educationalContext = null;
        String licenseType = null;
        long start = 0;
        long totalResults = 1450;

        List<Searchable> searchables = new ArrayList<>();
        searchables.add(createMaterial(9L));
        searchables.add(createMaterial(2L));
        searchables.add(createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, totalResults, subject, resourceType, educationalContext,
                licenseType);
    }

    @Test
    public void searchReturnsOnlyMaterials() {
        String query = "people";
        String tokenizedQuery = "people*";
        String subject = null;
        String resourceType = null;
        String educationalContext = null;
        String licenseType = null;
        long start = 0;

        List<Searchable> searchables = new ArrayList<>();
        searchables.add(createMaterial(9L));
        searchables.add(createMaterial(2L));

        testSearch(query, tokenizedQuery, searchables, start, subject, resourceType, educationalContext, licenseType);
    }

    @Test
    public void searchReturnsOnlyPortfolios() {
        String query = "people";
        String tokenizedQuery = "people*";
        String subject = null;
        String resourceType = null;
        String educationalContext = null;
        String licenseType = null;
        long start = 0;

        List<Searchable> searchables = new ArrayList<>();
        searchables.add(createPortfolio(9L));
        searchables.add(createPortfolio(2L));

        testSearch(query, tokenizedQuery, searchables, start, subject, resourceType, educationalContext, licenseType);
    }

    @Test
    public void searchReturnsMaterialAndPortfoliosWithSameIds() {
        String query = "people";
        String tokenizedQuery = "people*";
        String subject = null;
        String resourceType = null;
        String educationalContext = null;
        String licenseType = null;
        long start = 0;

        List<Searchable> searchables = new ArrayList<>();
        searchables.add(createPortfolio(9L));
        searchables.add(createMaterial(9L));

        testSearch(query, tokenizedQuery, searchables, start, subject, resourceType, educationalContext, licenseType);
    }

    private void testSearch(String query, String tokenizedQuery, List<Searchable> searchables, long start,
            long totalResults, String subject, String resourceType, String educationalContext, String licenseType) {
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

        SearchResult result = searchService.search(query, start, subject, resourceType, educationalContext,
                licenseType);

        verifyAll();

        assertSameSearchable(searchables, result.getItems());
        assertEquals(totalResults, result.getTotalResults());
        assertEquals(start, result.getStart());
    }

    private void testSearch(String query, String tokenizedQuery, List<Searchable> searchables, long start,
            String subject, String resourceType, String educationalContext, String licenseType) {
        testSearch(query, tokenizedQuery, searchables, start, searchables.size(), subject, resourceType,
                educationalContext, licenseType);
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
