package ee.hm.dop.rest;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.SearchResult;

public class SearchResourceTest extends ResourceIntegrationTestBase {

    private static final int RESULTS_PER_PAGE = 3;

    @Test
    public void search() {
        String query = "المدرسية";
        SearchResult searchResult = doGet(buildQueryURL(query, 0, null, null), SearchResult.class);

        assertEquals(1, searchResult.getMaterials().size());
        assertEquals(Long.valueOf(3), searchResult.getMaterials().get(0).getId());
        assertEquals(Long.valueOf(1), Long.valueOf(searchResult.getTotalResults()));
        assertEquals(Long.valueOf(0), Long.valueOf(searchResult.getStart()));
    }

    @Test
    public void searchGetSecondPage() {
        String query = "thishasmanyresults";
        int start = RESULTS_PER_PAGE;
        SearchResult searchResult = doGet(buildQueryURL(query, start, null, null), SearchResult.class);

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
        SearchResult searchResult = doGet(buildQueryURL(query, 0, subject, null), SearchResult.class);

        assertEquals(1, searchResult.getMaterials().size());
        assertEquals(Long.valueOf(5), searchResult.getMaterials().get(0).getId());
        assertEquals(Long.valueOf(1), Long.valueOf(searchResult.getTotalResults()));
        assertEquals(Long.valueOf(0), Long.valueOf(searchResult.getStart()));
    }

    @Test
    public void searchWithResourceTypeFilter() {
        String query = "beethoven";
        String resourceType = "Audio";
        String queryURL = buildQueryURL(query, 0, null, resourceType);
        SearchResult searchResult = doGet(queryURL, SearchResult.class);

        assertEquals(1, searchResult.getMaterials().size());
        assertEquals(Long.valueOf(4), searchResult.getMaterials().get(0).getId());
        assertEquals(Long.valueOf(1), Long.valueOf(searchResult.getTotalResults()));
        assertEquals(Long.valueOf(0), Long.valueOf(searchResult.getStart()));
    }

    @Test
    public void searchWithAllFilters() {
        String query = "john";
        String subject = "Mathematics";
        String resourceType = "Audio";
        SearchResult searchResult = doGet(buildQueryURL(query, 0, subject, resourceType), SearchResult.class);

        assertEquals(1, searchResult.getMaterials().size());
        assertEquals(Long.valueOf(2), searchResult.getMaterials().get(0).getId());
        assertEquals(Long.valueOf(1), Long.valueOf(searchResult.getTotalResults()));
        assertEquals(Long.valueOf(0), Long.valueOf(searchResult.getStart()));
    }

    @Test
    public void searchNoResult() {
        String query = "no+results";
        SearchResult searchResult = doGet(buildQueryURL(query, 0, null, null), SearchResult.class);

        assertEquals(0, searchResult.getMaterials().size());
    }

    private String buildQueryURL(String query, int start, String subject, String resourceType) {
        String queryURL = "search?";
        if (query != null) {
            queryURL += "q=" + query;
        }
        if (start != 0) {
            queryURL += "&start=" + start;
        }
        if (subject != null) {
            queryURL += "&subject=" + subject;
        }
        if (resourceType != null) {
            queryURL += "&resource_type=" + resourceType;
        }
        return queryURL;
    }

}
