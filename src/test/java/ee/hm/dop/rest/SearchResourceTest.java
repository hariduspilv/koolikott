package ee.hm.dop.rest;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.SearchResult;

public class SearchResourceTest extends ResourceIntegrationTestBase {

    private static final int RESULTS_PER_PAGE = 3;
    private static final String QUERY_URL = "search?q=%s";
    private static final String QUERY_URL_WITH_SUBJECT = "search?q=%s&subject=%s";
    private static final String QUERY_URL_WITH_START = "search?q=%s&start=%d";

    @Test
    public void search() {
        String query = "المدرسية";
        SearchResult searchResult = doGet(format(QUERY_URL, query), SearchResult.class);

        assertEquals(1, searchResult.getMaterials().size());
        assertEquals(Long.valueOf(3), searchResult.getMaterials().get(0).getId());
        assertEquals(Long.valueOf(1), Long.valueOf(searchResult.getTotalResults()));
        assertEquals(Long.valueOf(0), Long.valueOf(searchResult.getStart()));
    }

    @Test
    public void searchGetSecondPage() {
        String query = "thishasmanyresults";
        int start = RESULTS_PER_PAGE;
        SearchResult searchResult = doGet(format(QUERY_URL_WITH_START, query, start), SearchResult.class);

        assertEquals(RESULTS_PER_PAGE, searchResult.getMaterials().size());
        for (int i = 0; i < RESULTS_PER_PAGE; i++) {
            assertEquals(Long.valueOf(i + start), searchResult.getMaterials().get(i).getId());
        }
        assertEquals(Long.valueOf(RESULTS_PER_PAGE), Long.valueOf(searchResult.getTotalResults()));
        assertEquals(Long.valueOf(start), Long.valueOf(searchResult.getStart()));

    }

    @Test
    public void searchWithSubjectFilter() {
        String query = "filteredquery";
        String subject = "Mathematics";
        SearchResult searchResult = doGet(format(QUERY_URL_WITH_SUBJECT, query, subject), SearchResult.class);

        assertEquals(1, searchResult.getMaterials().size());
        assertEquals(Long.valueOf(5), searchResult.getMaterials().get(0).getId());
        assertEquals(Long.valueOf(1), Long.valueOf(searchResult.getTotalResults()));
        assertEquals(Long.valueOf(0), Long.valueOf(searchResult.getStart()));
    }

    @Test
    public void searchNoResult() {
        String query = "no+results";
        SearchResult searchResult = doGet(format(QUERY_URL, query), SearchResult.class);

        assertEquals(0, searchResult.getMaterials().size());
    }

}
