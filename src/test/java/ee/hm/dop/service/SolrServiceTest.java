package ee.hm.dop.service;

import static ee.hm.dop.utils.ConfigurationProperties.SEARCH_SERVER;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import ee.hm.dop.model.SearchResponse;
import ee.hm.dop.model.SearchResponse.Document;
import ee.hm.dop.model.SearchResponse.Response;
import ee.hm.dop.service.SolrService;

@RunWith(EasyMockRunner.class)
public class SolrServiceTest {

    @Mock
    private Client client;

    @Mock
    private Configuration configuration;

    @TestSubject
    private SolrService solrService = new SolrService();

    @Mock
    private WebTarget target;

    @Mock
    private Builder builder;

    private final String serverUrl = "server/url/";
    
    private static final int RESULTS_PER_PAGE = 24;

    @Test
    public void search() {
        int start = 0;
        String urlQuery = "select?q=math&wt=json&start=" + start + "&rows=" + RESULTS_PER_PAGE;
        String query = "math";

        Document doc1 = new Document();
        doc1.setId("1");

        Document doc2 = new Document();
        doc2.setId("2");

        Document doc3 = new Document();
        doc3.setId("3");

        Document doc4 = new Document();
        doc4.setId("4");

        setUpSearch(urlQuery, doc3, doc4, doc1, doc2);

        replayAll();

        List<Long> result = solrService.search(query, start);

        verifyAll();

        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals(Long.valueOf(3), result.get(0));
        assertEquals(Long.valueOf(4), result.get(1));
        assertEquals(Long.valueOf(1), result.get(2));
        assertEquals(Long.valueOf(2), result.get(3));
    }
    
    @Test
    public void searchGetSecondPage() {      
        int start = RESULTS_PER_PAGE;
        String urlQuery = "select?q=math&wt=json&start=" + start + "&rows=" + RESULTS_PER_PAGE;
        String query = "math";

        Document doc1 = new Document();
        doc1.setId("1");

        Document doc2 = new Document();
        doc2.setId("2");

        setUpSearch(urlQuery, doc2, doc1);

        replayAll();

        List<Long> result = solrService.search(query, start);

        verifyAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(Long.valueOf(2), result.get(0));
        assertEquals(Long.valueOf(1), result.get(1));
    }

    @Test
    public void searchNoResult() {
        String urlQuery = "select?q=math&wt=json&start=0&rows=" + RESULTS_PER_PAGE;
        String query = "math";

        noResultSearch(urlQuery, query);
    }

    @Test
    public void searchURLEncodingQuotes() {
        String urlQuery = "select?q=%22&wt=json&start=0&rows=" + RESULTS_PER_PAGE;
        String query = "\"";

        noResultSearch(urlQuery, query);
    }

    @Test
    public void searchURLEncodingQuotesAndSpace() {
        String urlQuery = "select?q=%22this+search%22&wt=json&start=0&rows=" + RESULTS_PER_PAGE;
        String query = "\"this search\"";

        noResultSearch(urlQuery, query);
    }

    @Test
    public void searchURLEncodingPlusAndMinus() {
        String urlQuery = "select?q=%2Bmath+-port&wt=json&start=0&rows=" + RESULTS_PER_PAGE;
        String query = "+math -port";

        noResultSearch(urlQuery, query);
    }

    @Test
    public void searchErrorInSearch() {
        String urlQuery = "select?q=%2Bmath+-port&wt=json&start=0&rows=" + RESULTS_PER_PAGE;
        String query = "+math -port";

        SearchResponse searchResponse = new SearchResponse();

        expect(configuration.getString(SEARCH_SERVER)).andReturn(serverUrl);

        expect(client.target(serverUrl + urlQuery)).andReturn(target);
        expect(target.request(MediaType.APPLICATION_JSON)).andReturn(builder);
        expect(builder.get(eq(SearchResponse.class))).andReturn(searchResponse);

        replayAll();

        List<Long> result = solrService.search(query, 0);

        verifyAll();

        assertNotNull(result);
        assertEquals(0, result.size());
    }
    
    @Test
    public void countResults() {
        String urlQuery = "select?q=math&wt=json&start=0&rows=" + RESULTS_PER_PAGE;
        String query = "math";
        long resultCount = 10;
        
        setUpCountResults(urlQuery, resultCount);

        replayAll();

        long result = solrService.countResults(query);

        verifyAll();

        assertNotNull(result);
        assertEquals(resultCount, result);
        
    }

    private void noResultSearch(String urlQuery, String query) {
        setUpSearch(urlQuery);

        replayAll();

        List<Long> result = solrService.search(query, 0);

        verifyAll();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    private void setUpSearch(String urlQuery, Document... docs) {
        List<Document> documents = new ArrayList<>();

        for (Document document : docs) {
            documents.add(document);
        }

        Response response = new Response();
        response.setDocuments(documents);

        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setResponse(response);

        expect(configuration.getString(SEARCH_SERVER)).andReturn(serverUrl);

        expect(client.target(serverUrl + urlQuery)).andReturn(target);
        expect(target.request(MediaType.APPLICATION_JSON)).andReturn(builder);
        expect(builder.get(eq(SearchResponse.class))).andReturn(searchResponse);
    }
    
    private void setUpCountResults(String urlQuery, long resultCount) {
        Response response = new Response();
        response.setNumFound(resultCount);

        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setResponse(response);

        expect(configuration.getString(SEARCH_SERVER)).andReturn(serverUrl);

        expect(client.target(serverUrl + urlQuery)).andReturn(target);
        expect(target.request(MediaType.APPLICATION_JSON)).andReturn(builder);
        expect(builder.get(eq(SearchResponse.class))).andReturn(searchResponse);
    }

    private void verifyAll() {
        verify(client, configuration, target, builder);
    }

    private void replayAll() {
        replay(client, configuration, target, builder);
    }

}
