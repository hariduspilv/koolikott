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

import ee.hm.dop.model.solr.Document;
import ee.hm.dop.model.solr.Response;
import ee.hm.dop.model.solr.SearchResponse;

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
        long start = 0;
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

        setUpSearch(urlQuery, 4L, start, doc3, doc4, doc1, doc2);

        replayAll();

        SearchResponse searchResponse = solrService.search(query, 0);
        List<Document> result = searchResponse.getResponse().getDocuments();

        verifyAll();

        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals(Long.valueOf(3), Long.valueOf(result.get(0).getId()));
        assertEquals(Long.valueOf(4), Long.valueOf(result.get(1).getId()));
        assertEquals(Long.valueOf(1), Long.valueOf(result.get(2).getId()));
        assertEquals(Long.valueOf(2), Long.valueOf(result.get(3).getId()));
        assertEquals(Long.valueOf(4), Long.valueOf(searchResponse.getResponse().getTotalResults()));
        assertEquals(Long.valueOf(start), Long.valueOf(searchResponse.getResponse().getStart()));
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

        setUpSearch(urlQuery, 2L, Long.valueOf(start), doc2, doc1);

        replayAll();

        SearchResponse searchResponse = solrService.search(query, start);
        List<Document> result = searchResponse.getResponse().getDocuments();

        verifyAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(Long.valueOf(2), Long.valueOf(result.get(0).getId()));
        assertEquals(Long.valueOf(1), Long.valueOf(result.get(1).getId()));
        assertEquals(Long.valueOf(2), Long.valueOf(searchResponse.getResponse().getTotalResults()));
        assertEquals(Long.valueOf(start), Long.valueOf(searchResponse.getResponse().getStart()));
    }

    @Test
    public void searchNoResult() {
        String urlQuery = "select?q=math&wt=json&start=0&rows=" + RESULTS_PER_PAGE;
        String query = "math";

        noResultSearch(urlQuery, query, 0L);
    }

    @Test
    public void searchURLEncodingQuotes() {
        String urlQuery = "select?q=%22&wt=json&start=0&rows=" + RESULTS_PER_PAGE;
        String query = "\"";

        noResultSearch(urlQuery, query, 0L);
    }

    @Test
    public void searchURLEncodingQuotesAndSpace() {
        String urlQuery = "select?q=%22this+search%22&wt=json&start=0&rows=" + RESULTS_PER_PAGE;
        String query = "\"this search\"";

        noResultSearch(urlQuery, query, 0L);
    }

    @Test
    public void searchURLEncodingPlusAndMinus() {
        String urlQuery = "select?q=%2Bmath+-port&wt=json&start=0&rows=" + RESULTS_PER_PAGE;
        String query = "+math -port";

        noResultSearch(urlQuery, query, 0L);
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

        SearchResponse resultResponse = solrService.search(query, 0);

        verifyAll();

        assertNotNull(resultResponse);

    }

    private void noResultSearch(String urlQuery, String query, Long start) {
        setUpSearch(urlQuery, 0L, start);

        replayAll();

        SearchResponse searchResponse = solrService.search(query, 0);
        List<Document> result = searchResponse.getResponse().getDocuments();

        verifyAll();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    private void setUpSearch(String urlQuery, Long totalResults, Long start, Document... docs) {
        List<Document> documents = new ArrayList<>();

        for (Document document : docs) {
            documents.add(document);
        }

        Response response = new Response();
        response.setDocuments(documents);
        response.setTotalResults(totalResults);
        response.setStart(start);

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
