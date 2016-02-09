package ee.hm.dop.service;

import static ee.hm.dop.service.SolrService.SOLR_DATAIMPORT_STATUS;
import static ee.hm.dop.service.SolrService.SOLR_IMPORT_PARTIAL;
import static ee.hm.dop.service.SolrService.SOLR_STATUS_BUSY;
import static ee.hm.dop.utils.ConfigurationProperties.SEARCH_SERVER;
import static org.easymock.EasyMock.createMock;
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
import ee.hm.dop.model.solr.ResponseHeader;
import ee.hm.dop.model.solr.SearchResponse;

@RunWith(EasyMockRunner.class)
public class SolrServiceTest {

    private final String serverUrl = "server/url/";

    private static final int RESULTS_PER_PAGE = 24;

    private static final String SOLR_STATUS_IDLE = "idle";

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

    @Test
    public void search() {
        long start = 0;
        String urlQuery = "select?q=math&sort=&wt=json&start=" + start + "&rows=" + RESULTS_PER_PAGE;
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

        SearchResponse searchResponse = solrService.search(query, 0, null);
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
        assertEquals(0, searchResponse.getResponseHeader().getStatus());
    }

    @Test
    public void searchWithSorting() {
        long start = 0;
        String urlQuery = "select?q=math&sort=author+asc&wt=json&start=" + start + "&rows=" + RESULTS_PER_PAGE;
        String query = "math";

        Document doc5 = new Document();
        doc5.setId("5");

        setUpSearch(urlQuery, 1L, start, doc5);

        replayAll();

        SearchResponse searchResponse = solrService.search(query, 0, "author asc");
        List<Document> result = searchResponse.getResponse().getDocuments();

        verifyAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Long.valueOf(5), Long.valueOf(result.get(0).getId()));
        assertEquals(Long.valueOf(1), Long.valueOf(searchResponse.getResponse().getTotalResults()));
        assertEquals(Long.valueOf(start), Long.valueOf(searchResponse.getResponse().getStart()));
        assertEquals(0, searchResponse.getResponseHeader().getStatus());
    }

    @Test
    public void searchGetSecondPage() {
        int start = RESULTS_PER_PAGE;
        String urlQuery = "select?q=math&sort=&wt=json&start=" + start + "&rows=" + RESULTS_PER_PAGE;
        String query = "math";

        Document doc1 = new Document();
        doc1.setId("1");

        Document doc2 = new Document();
        doc2.setId("2");

        setUpSearch(urlQuery, 2L, Long.valueOf(start), doc2, doc1);

        replayAll();

        SearchResponse searchResponse = solrService.search(query, start, null);
        List<Document> result = searchResponse.getResponse().getDocuments();

        verifyAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(Long.valueOf(2), Long.valueOf(result.get(0).getId()));
        assertEquals(Long.valueOf(1), Long.valueOf(result.get(1).getId()));
        assertEquals(Long.valueOf(2), Long.valueOf(searchResponse.getResponse().getTotalResults()));
        assertEquals(Long.valueOf(start), Long.valueOf(searchResponse.getResponse().getStart()));
        assertEquals(0, searchResponse.getResponseHeader().getStatus());
    }

    @Test
    public void searchNoResult() {
        String urlQuery = "select?q=math&sort=&wt=json&start=0&rows=" + RESULTS_PER_PAGE;
        String query = "math";

        noResultSearch(urlQuery, query, 0L);
    }

    @Test
    public void searchURLEncodingQuotes() {
        String urlQuery = "select?q=%22&sort=&wt=json&start=0&rows=" + RESULTS_PER_PAGE;
        String query = "\"";

        noResultSearch(urlQuery, query, 0L);
    }

    @Test
    public void searchURLEncodingQuotesAndSpace() {
        String urlQuery = "select?q=%22this+search%22&sort=&wt=json&start=0&rows=" + RESULTS_PER_PAGE;
        String query = "\"this search\"";

        noResultSearch(urlQuery, query, 0L);
    }

    @Test
    public void searchURLEncodingPlusAndMinus() {
        String urlQuery = "select?q=%2Bmath+-port&sort=&wt=json&start=0&rows=" + RESULTS_PER_PAGE;
        String query = "+math -port";

        noResultSearch(urlQuery, query, 0L);
    }

    @Test
    public void searchErrorInSearch() {
        String urlQuery = "select?q=%2Bmath+-port&sort=&wt=json&start=0&rows=" + RESULTS_PER_PAGE;
        String query = "+math -port";

        ResponseHeader responseHeader = new ResponseHeader();
        responseHeader.setStatus(555);

        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setResponseHeader(responseHeader);

        expect(configuration.getString(SEARCH_SERVER)).andReturn(serverUrl).times(2);

        expect(client.target(serverUrl + urlQuery)).andReturn(target);
        expect(target.request(MediaType.APPLICATION_JSON)).andReturn(builder);
        expect(builder.get(eq(SearchResponse.class))).andReturn(searchResponse);

        replayAll();

        SearchResponse resultResponse = solrService.search(query, 0, null);

        verifyAll();

        assertNotNull(resultResponse);
        assertEquals(555, searchResponse.getResponseHeader().getStatus());

    }

    private void noResultSearch(String urlQuery, String query, Long start) {
        setUpSearch(urlQuery, 0L, start);

        replayAll();

        SearchResponse searchResponse = solrService.search(query, 0, null);
        List<Document> result = searchResponse.getResponse().getDocuments();

        verifyAll();

        assertNotNull(result);
        assertEquals(0, result.size());
        assertEquals(0, searchResponse.getResponseHeader().getStatus());
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

        ResponseHeader responseHeader = new ResponseHeader();
        responseHeader.setStatus(0);

        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setResponse(response);
        searchResponse.setResponseHeader(responseHeader);

        expect(configuration.getString(SEARCH_SERVER)).andReturn(serverUrl).anyTimes();

        expect(client.target(serverUrl + urlQuery)).andReturn(target);
        expect(target.request(MediaType.APPLICATION_JSON)).andReturn(builder);
        expect(builder.get(eq(SearchResponse.class))).andReturn(searchResponse);
    }

    @Test
    public void updateIndex() throws Exception {
        ResponseHeader responseHeader = new ResponseHeader();
        responseHeader.setStatus(0);

        SearchResponse importResponse = new SearchResponse();
        importResponse.setResponseHeader(responseHeader);

        SearchResponse statusResponseBusy = new SearchResponse();
        statusResponseBusy.setStatus(SOLR_STATUS_BUSY);
        statusResponseBusy.setResponseHeader(responseHeader);

        SearchResponse statusResponseIdle = new SearchResponse();
        statusResponseIdle.setStatus(SOLR_STATUS_IDLE);
        statusResponseIdle.setResponseHeader(responseHeader);

        String solrLocation = "http://solr.example.com/";

        expect(configuration.getString(SEARCH_SERVER)).andReturn(solrLocation).anyTimes();
        expect(client.target(solrLocation + SOLR_IMPORT_PARTIAL)).andReturn(target);
        expect(target.request(MediaType.APPLICATION_JSON)).andReturn(builder);
        expect(builder.get(eq(SearchResponse.class))).andReturn(importResponse);

        expect(client.target(solrLocation + SOLR_DATAIMPORT_STATUS)).andReturn(target);
        expect(target.request(MediaType.APPLICATION_JSON)).andReturn(builder);
        expect(builder.get(eq(SearchResponse.class))).andReturn(statusResponseBusy);

        expect(client.target(solrLocation + SOLR_DATAIMPORT_STATUS)).andReturn(target);
        expect(target.request(MediaType.APPLICATION_JSON)).andReturn(builder);
        expect(builder.get(eq(SearchResponse.class))).andReturn(statusResponseIdle);

        replayAll();

        solrService.updateIndex();

        Thread.sleep(3000);

        verifyAll();
    }

    @Test
    public void executeCommand() throws NoSuchMethodException {
        long status = 6;
        String command = "solr?command=thing";

        ResponseHeader responseHeader = new ResponseHeader();
        responseHeader.setStatus(status);

        SearchResponse searchResponse = createMock(SearchResponse.class);

        expect(configuration.getString(SEARCH_SERVER)).andReturn(serverUrl).times(2);
        expect(client.target(serverUrl + command)).andReturn(target);
        expect(target.request(MediaType.APPLICATION_JSON)).andReturn(builder);
        expect(builder.get(eq(SearchResponse.class))).andReturn(searchResponse);

        expect(searchResponse.getResponseHeader()).andReturn(responseHeader).anyTimes();

        replayAll(searchResponse);

        SearchResponse result = solrService.executeCommand(command);

        verifyAll(searchResponse);

        assertEquals(searchResponse, result);
        assertEquals(status, result.getResponseHeader().getStatus());
    }

    private void verifyAll(Object... mocks) {
        verify(client, configuration, target, builder);

        if (mocks != null) {
            for (Object object : mocks) {
                verify(object);
            }
        }
    }

    private void replayAll(Object... mocks) {
        replay(client, configuration, target, builder);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

}
