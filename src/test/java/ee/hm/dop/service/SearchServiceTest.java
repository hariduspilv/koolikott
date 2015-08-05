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
        long start = 0;

        List<Long> documentIds = Arrays.asList(7L, 1L, 4L);
        SearchResponse searchResponse = createSearchResponseWithDocuments(documentIds);

        Material material7 = new Material();
        material7.setId((long) 7);
        Material material1 = new Material();
        material1.setId((long) 1);
        Material material4 = new Material();
        material4.setId((long) 4);

        List<Material> materials = new ArrayList<>();
        materials.add(material1);
        materials.add(material4);
        materials.add(material7);

        expect(searchEngineService.search(tokenizedQuery, start)).andReturn(searchResponse);
        expect(materialDAO.findAllById(documentIds)).andReturn(materials);

        replayAll();

        SearchResult result = searchService.search(query, start);

        verifyAll();

        assertEquals(3, result.getMaterials().size());
        assertSame(material7, result.getMaterials().get(0));
        assertSame(material1, result.getMaterials().get(1));
        assertSame(material4, result.getMaterials().get(2));
        assertEquals(3, result.getTotalResults());
        assertEquals(start, result.getStart());

    }

    // To test asynchronous problems that may occur when search returns deleted
    // materials
    @Test
    public void searchWhenDatabaseReturnsLessValuesThanSearch() {
        String query = "people";
        String tokenizedQuery = "people*";
        long start = 0;

        List<Long> documentIds = Arrays.asList(7L, 1L, 4L);
        SearchResponse searchResponse = createSearchResponseWithDocuments(documentIds);

        Material material7 = new Material();
        material7.setId((long) 7);
        Material material4 = new Material();
        material4.setId((long) 4);

        List<Material> materials = new ArrayList<>();
        materials.add(material4);
        materials.add(material7);

        expect(searchEngineService.search(tokenizedQuery, start)).andReturn(searchResponse);
        expect(materialDAO.findAllById(documentIds)).andReturn(materials);

        replayAll();

        SearchResult result = searchService.search(query, start);

        verifyAll();

        assertEquals(2, result.getMaterials().size());
        assertSame(material7, result.getMaterials().get(0));
        assertSame(material4, result.getMaterials().get(1));
        assertEquals(3, result.getTotalResults());
        assertEquals(start, result.getStart());
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
    public void searchWithSubjectFilter() {
        String query = "airplane";
        String subject = "Mathematics";
        String tokenizedQuery = "+subject:\"mathematics\" airplane*";
        long start = 0;

        List<Long> documentIds = Arrays.asList(9L, 2L);
        SearchResponse searchResponse = createSearchResponseWithDocuments(documentIds);

        Material material9 = new Material();
        material9.setId((long) 9);
        Material material2 = new Material();
        material2.setId((long) 2);

        List<Material> materials = new ArrayList<>();
        materials.add(material9);
        materials.add(material2);

        expect(searchEngineService.search(tokenizedQuery, start)).andReturn(searchResponse);
        expect(materialDAO.findAllById(documentIds)).andReturn(materials);

        replayAll();

        SearchResult result = searchService.search(query, start, subject);

        verifyAll();

        assertEquals(2, result.getMaterials().size());
        assertSame(material9, result.getMaterials().get(0));
        assertSame(material2, result.getMaterials().get(1));
        assertEquals(2, result.getTotalResults());
        assertEquals(start, result.getStart());
    }

    @Test
    public void searchWithSubjectFilterNull() {
        String query = "airplane";
        String subject = null;
        String tokenizedQuery = "airplane*";
        long start = 0;

        List<Long> documentIds = Arrays.asList(9L);
        SearchResponse searchResponse = createSearchResponseWithDocuments(documentIds);

        Material material9 = new Material();
        material9.setId((long) 9);

        List<Material> materials = new ArrayList<>();
        materials.add(material9);

        expect(searchEngineService.search(tokenizedQuery, start)).andReturn(searchResponse);
        expect(materialDAO.findAllById(documentIds)).andReturn(materials);

        replayAll();

        SearchResult result = searchService.search(query, start, subject);

        verifyAll();

        assertEquals(1, result.getMaterials().size());
        assertSame(material9, result.getMaterials().get(0));
        assertEquals(1, result.getTotalResults());
        assertEquals(start, result.getStart());
    }

    private SearchResponse createSearchResponseWithDocuments(List<Long> documentIds) {
        List<Document> documents = new ArrayList<>();
        for (Long id : documentIds) {
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

    private void replayAll() {
        replay(searchEngineService, materialDAO);
    }

    private void verifyAll() {
        verify(searchEngineService, materialDAO);
    }

}
