package ee.hm.dop.rest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.solr.SearchResult;

public class SearchResourceTest extends ResourceIntegrationTestBase {

    private static final int RESULTS_PER_PAGE = 3;

    @Test
    public void search() {
        String query = "المدرسية";
        Response response = doGet("search?q=" + query);
        SearchResult searchResult = response.readEntity(new GenericType<SearchResult>() {
        });

        assertEquals(1, searchResult.getMaterials().size());
        assertEquals(Long.valueOf(3), searchResult.getMaterials().get(0).getId());
        assertEquals(Long.valueOf(1), Long.valueOf(searchResult.getTotalResults()));
        assertEquals(Long.valueOf(0), Long.valueOf(searchResult.getStart()));
    }

    @Test
    public void searchGetSecondPage() {
        String query = "thishasmanyresults";
        int start = RESULTS_PER_PAGE;
        Response response = doGet("search?q=" + query + "&start=" + start);
        SearchResult searchResult = response.readEntity(new GenericType<SearchResult>() {
        });

        assertEquals(RESULTS_PER_PAGE, searchResult.getMaterials().size());
        for (int i = 0; i < RESULTS_PER_PAGE; i++) {
            assertEquals(Long.valueOf(i + start), searchResult.getMaterials().get(i).getId());
        }
        assertEquals(Long.valueOf(RESULTS_PER_PAGE), Long.valueOf(searchResult.getTotalResults()));
        assertEquals(Long.valueOf(start), Long.valueOf(searchResult.getStart()));

    }

    @Test
    public void searchNoResult() {
        String query = "no+results";
        Response response = doGet("search?q=" + query);
        SearchResult searchResult = response.readEntity(new GenericType<SearchResult>() {
        });

        assertEquals(0, searchResult.getMaterials().size());
    }

}
