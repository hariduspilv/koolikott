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
        SearchResult searchResult = doGet(buildQueryURL(query, 0, null, null, null), SearchResult.class);

        assertEquals(1, searchResult.getMaterials().size());
        assertEquals(Long.valueOf(3), searchResult.getMaterials().get(0).getId());
        assertEquals(Long.valueOf(1), Long.valueOf(searchResult.getTotalResults()));
        assertEquals(Long.valueOf(0), Long.valueOf(searchResult.getStart()));
    }

    @Test
    public void searchGetSecondPage() {
        String query = "thishasmanyresults";
        int start = RESULTS_PER_PAGE;
        SearchResult searchResult = doGet(buildQueryURL(query, start, null, null, null), SearchResult.class);

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
        SearchResult searchResult = doGet(buildQueryURL(query, 0, subject, null, null), SearchResult.class);

        assertEquals(1, searchResult.getMaterials().size());
        assertEquals(Long.valueOf(5), searchResult.getMaterials().get(0).getId());
        assertEquals(Long.valueOf(1), Long.valueOf(searchResult.getTotalResults()));
        assertEquals(Long.valueOf(0), Long.valueOf(searchResult.getStart()));
    }

    @Test
    public void searchWithResourceTypeFilter() {
        String query = "beethoven";
        String resourceType = "Audio";
        String queryURL = buildQueryURL(query, 0, null, resourceType, null);
        SearchResult searchResult = doGet(queryURL, SearchResult.class);

        assertEquals(1, searchResult.getMaterials().size());
        assertEquals(Long.valueOf(4), searchResult.getMaterials().get(0).getId());
        assertEquals(Long.valueOf(1), Long.valueOf(searchResult.getTotalResults()));
        assertEquals(Long.valueOf(0), Long.valueOf(searchResult.getStart()));
    }

    @Test
    public void searchWithSubjectAndResourceTypeFilter() {
        String query = "beethoven";
        String subject = "Mathematics";
        String resourceType = "Audio";
        String queryURL = buildQueryURL(query, 0, subject, resourceType, null);
        SearchResult searchResult = doGet(queryURL, SearchResult.class);

        assertEquals(1, searchResult.getMaterials().size());
        assertEquals(Long.valueOf(7), searchResult.getMaterials().get(0).getId());
        assertEquals(Long.valueOf(1), Long.valueOf(searchResult.getTotalResults()));
        assertEquals(Long.valueOf(0), Long.valueOf(searchResult.getStart()));
    }

    @Test
    public void searchWithEducationalContextFilter() {
        String query = "beethoven";
        String educationalContext = "preschool";
        String queryURL = buildQueryURL(query, 0, null, null, educationalContext);
        SearchResult searchResult = doGet(queryURL, SearchResult.class);

        assertEquals(1, searchResult.getMaterials().size());
        assertEquals(Long.valueOf(6), searchResult.getMaterials().get(0).getId());
        assertEquals(Long.valueOf(1), Long.valueOf(searchResult.getTotalResults()));
        assertEquals(Long.valueOf(0), Long.valueOf(searchResult.getStart()));
    }

    @Test
    public void searchWithSubjectAndEducationalContextFilter() {
        String query = "beethoven";
        String subject = "Mathematics";
        String educationalContext = "Preschool";
        String queryURL = buildQueryURL(query, 0, subject, null, educationalContext);
        SearchResult searchResult = doGet(queryURL, SearchResult.class);

        assertEquals(1, searchResult.getMaterials().size());
        assertEquals(Long.valueOf(8), searchResult.getMaterials().get(0).getId());
        assertEquals(Long.valueOf(1), Long.valueOf(searchResult.getTotalResults()));
        assertEquals(Long.valueOf(0), Long.valueOf(searchResult.getStart()));
    }

    @Test
    public void searchWithResourceTypeAndEducationalContextFilter() {
        String query = "beethoven";
        String resourceType = "audio";
        String educationalContext = "preschool";
        String queryURL = buildQueryURL(query, 0, null, resourceType, educationalContext);
        SearchResult searchResult = doGet(queryURL, SearchResult.class);

        assertEquals(2, searchResult.getMaterials().size());
        assertEquals(Long.valueOf(7), searchResult.getMaterials().get(0).getId());
        assertEquals(Long.valueOf(8), searchResult.getMaterials().get(1).getId());
        assertEquals(Long.valueOf(2), Long.valueOf(searchResult.getTotalResults()));
        assertEquals(Long.valueOf(0), Long.valueOf(searchResult.getStart()));
    }

    @Test
    public void searchWithAllFilters() {
        String query = "john";
        String subject = "Mathematics";
        String resourceType = "Audio";
        String educationalContext = "Preschool";
        SearchResult searchResult = doGet(buildQueryURL(query, 0, subject, resourceType, educationalContext), SearchResult.class);

        assertEquals(1, searchResult.getMaterials().size());
        assertEquals(Long.valueOf(2), searchResult.getMaterials().get(0).getId());
        assertEquals(Long.valueOf(1), Long.valueOf(searchResult.getTotalResults()));
        assertEquals(Long.valueOf(0), Long.valueOf(searchResult.getStart()));
    }

    @Test
    public void searchNoResult() {
        String query = "no+results";
        SearchResult searchResult = doGet(buildQueryURL(query, 0, null, null, null), SearchResult.class);

        assertEquals(0, searchResult.getMaterials().size());
    }

    private String buildQueryURL(String query, int start, String subject, String resourceType, String educationalContext) {
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
        if (educationalContext != null) {
            queryURL += "&educational_context=" + educationalContext;
        }
        return queryURL;
    }

}
