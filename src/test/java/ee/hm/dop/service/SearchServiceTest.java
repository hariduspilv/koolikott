package ee.hm.dop.service;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
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
import ee.hm.dop.model.Material;
import ee.hm.dop.model.SearchResult;
import ee.hm.dop.model.solr.Document;
import ee.hm.dop.model.solr.Response;
import ee.hm.dop.model.solr.SearchResponse;

@RunWith(EasyMockRunner.class)
public class SearchServiceTest {

    @Mock
    private SearchEngineService searchEngineService;

    @Mock
    private MaterialDAO materialDAO;

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
        List<Long> identifiers = Arrays.asList(7L, 1L, 4L);

        testSearch(query, tokenizedQuery, identifiers, start, subject, resourceType, educationalContext, licenseType);
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
        long start = 0;
        List<Long> identifiers = Arrays.asList(7L, 1L, 4L);

        testSearch(query, tokenizedQuery, identifiers, start, subject, resourceType, educationalContext, licenseType);
    }

    @Test
    public void searchNoResult() {
        String query = "people";
        String tokenizedQuery = "people*";
        long start = 0;

        SearchResponse searchResponse = createSearchResponseWithDocuments(new ArrayList<>());

        expect(searchEngineService.search(tokenizedQuery, 0)).andReturn(searchResponse);

        replayAll();

        List<Material> result = searchService.search(query, start).getMaterials();

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
        try {
            searchService.search("", 0);
            fail(); // Expecting exception
        } catch (RuntimeException e) {
            // OK
        }
        verifyAll();
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
        List<Long> identifiers = Arrays.asList(9L, 2L);

        testSearch(query, tokenizedQuery, identifiers, start, subject, resourceType, educationalContext, licenseType);
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
        List<Long> identifiers = Arrays.asList(9L, 2L);

        testSearch(query, tokenizedQuery, identifiers, start, subject, resourceType, educationalContext, licenseType);
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
        List<Long> identifiers = Arrays.asList(15L, 8L);

        testSearch(query, tokenizedQuery, identifiers, start, subject, resourceType, educationalContext, licenseType);
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
        List<Long> identifiers = Arrays.asList(16L, 9L);

        testSearch(query, tokenizedQuery, identifiers, start, subject, resourceType, educationalContext, licenseType);
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
        List<Long> identifiers = Arrays.asList(5L, 7L);

        testSearch(query, tokenizedQuery, identifiers, start, subject, resourceType, educationalContext, licenseType);
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
        List<Long> identifiers = Arrays.asList(16L, 8L);

        testSearch(query, tokenizedQuery, identifiers, start, subject, resourceType, educationalContext, licenseType);
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
        List<Long> identifiers = Arrays.asList(17L, 8L);

        testSearch(query, tokenizedQuery, identifiers, start, subject, resourceType, educationalContext, licenseType);
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
        List<Long> identifiers = Arrays.asList(15L, 8L);

        testSearch(query, tokenizedQuery, identifiers, start, subject, resourceType, educationalContext, licenseType);
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
        List<Long> identifiers = Arrays.asList(9L, 2L);

        testSearch(query, tokenizedQuery, identifiers, start, subject, resourceType, educationalContext, licenseType);
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
        List<Long> identifiers = Arrays.asList(9L, 2L);

        testSearch(query, tokenizedQuery, identifiers, start, subject, resourceType, educationalContext, licenseType);
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
        List<Long> identifiers = Arrays.asList(15L, 8L);

        testSearch(query, tokenizedQuery, identifiers, start, subject, resourceType, educationalContext, licenseType);
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
        List<Long> identifiers = Arrays.asList(5L, 7L);

        testSearch(query, tokenizedQuery, identifiers, start, subject, resourceType, educationalContext, licenseType);
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
        List<Long> identifiers = Arrays.asList(16L, 9L);

        testSearch(query, tokenizedQuery, identifiers, start, subject, resourceType, educationalContext, licenseType);
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
        List<Long> identifiers = Arrays.asList(16L, 8L);

        testSearch(query, tokenizedQuery, identifiers, start, subject, resourceType, educationalContext, licenseType);
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
        List<Long> identifiers = Arrays.asList(17L, 8L);

        testSearch(query, tokenizedQuery, identifiers, start, subject, resourceType, educationalContext, licenseType);
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
        List<Long> identifiers = Arrays.asList(15L, 8L);

        testSearch(query, tokenizedQuery, identifiers, start, subject, resourceType, educationalContext, licenseType);
    }

    private void testSearch(String query, String tokenizedQuery, List<Long> identifiers, long start, String subject,
            String resourceType, String educationalContext, String licenseType) {
        SearchResponse searchResponse = createSearchResponseWithDocuments(identifiers);
        List<Material> materials = createMaterials(identifiers);

        expect(searchEngineService.search(tokenizedQuery, start)).andReturn(searchResponse);
        expect(materialDAO.findAllById(identifiers)).andReturn(materials);

        replayAll();

        SearchResult result = searchService
                .search(query, start, subject, resourceType, educationalContext, licenseType);

        verifyAll();

        assertEquals(identifiers.size(), result.getMaterials().size());
        assertSameMaterials(materials, result.getMaterials());
        assertEquals(identifiers.size(), result.getTotalResults());
        assertEquals(start, result.getStart());
    }

    private SearchResponse createSearchResponseWithDocuments(List<Long> identifers) {
        List<Document> documents = new ArrayList<>();
        for (Long id : identifers) {
            Document newDocument = new Document();
            newDocument.setId(Long.toString(id));
            documents.add(newDocument);
        }

        Response response = new Response();
        response.setDocuments(documents);
        response.setTotalResults(documents.size());
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setResponse(response);

        return searchResponse;
    }

    private List<Material> createMaterials(List<Long> identifiers) {
        List<Material> materials = new ArrayList<>();

        for (Long id : identifiers) {
            Material material = new Material();
            material.setId(id);
            materials.add(material);
        }

        return materials;
    }

    private void assertSameMaterials(List<Material> expected, List<Material> actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertSame(expected.get(i), actual.get(i));
        }
    }

    private void replayAll() {
        replay(searchEngineService, materialDAO);
    }

    private void verifyAll() {
        verify(searchEngineService, materialDAO);
    }

}
