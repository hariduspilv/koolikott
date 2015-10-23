package ee.hm.dop.rest;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.SearchFilter;
import ee.hm.dop.model.SearchResult;
import ee.hm.dop.model.Searchable;

public class SearchResourceTest extends ResourceIntegrationTestBase {

    private static final int RESULTS_PER_PAGE = 3;

    @Test
    public void search() {
        String query = "المدرسية";
        SearchResult searchResult = doGet(buildQueryURL(query, 0, new SearchFilter()), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 3L);
        assertEquals(1, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchGetSecondPage() {
        String query = "thishasmanyresults";
        int start = RESULTS_PER_PAGE;
        SearchResult searchResult = doGet(buildQueryURL(query, start, new SearchFilter()), SearchResult.class);

        assertEquals(RESULTS_PER_PAGE, searchResult.getItems().size());
        for (int i = 0; i < RESULTS_PER_PAGE; i++) {
            assertEquals(Long.valueOf(i + start), searchResult.getItems().get(i).getId());
        }
        assertEquals(8, searchResult.getTotalResults());
        assertEquals(start, searchResult.getStart());
    }

    @Test
    public void searchNoResult() {
        String query = "no+results";
        SearchResult searchResult = doGet(buildQueryURL(query, 0, new SearchFilter()), SearchResult.class);

        assertEquals(0, searchResult.getItems().size());
    }

    @Test
    public void searchWithNullQueryAndNullFilter() {
        Response response = doGet(buildQueryURL(null, 0, new SearchFilter()));
        assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void searchWithNullQueryAndSubjectFilter() {
        String query = null;
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setSubject("InterestingSubject");
        SearchResult searchResult = doGet(buildQueryURL(query, 0, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 5L, 1L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithSubjectFilter() {
        String query = "filteredquery";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setSubject("Mathematics");
        SearchResult searchResult = doGet(buildQueryURL(query, 0, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 5L);
        assertEquals(1, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithResourceTypeFilter() {
        String query = "beethoven";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setResourceType("Audio");
        String queryURL = buildQueryURL(query, 0, searchFilter);
        SearchResult searchResult = doGet(queryURL, SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 4L);
        assertEquals(1, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithSubjectAndResourceTypeFilter() {
        String query = "beethoven";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setSubject("Mathematics");
        searchFilter.setResourceType("Audio");
        String queryURL = buildQueryURL(query, 0, searchFilter);
        SearchResult searchResult = doGet(queryURL, SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 7L);
        assertEquals(1, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithEducationalContextFilter() {
        String query = "beethoven";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setEducationalContext("preschool");
        String queryURL = buildQueryURL(query, 0, searchFilter);
        SearchResult searchResult = doGet(queryURL, SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 6L);
        assertEquals(1, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithSubjectAndEducationalContextFilter() {
        String query = "beethoven";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setSubject("Mathematics");
        searchFilter.setEducationalContext("Preschool");
        String queryURL = buildQueryURL(query, 0, searchFilter);
        SearchResult searchResult = doGet(queryURL, SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 8L);
        assertEquals(1, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithResourceTypeAndEducationalContextFilter() {
        String query = "beethoven";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setResourceType("audio");
        searchFilter.setEducationalContext("preschool");
        String queryURL = buildQueryURL(query, 0, searchFilter);
        SearchResult searchResult = doGet(queryURL, SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 7L, 8L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithSubjectAndResourceTypeAndEducationalContextFilters() {
        String query = "john";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setSubject("Mathematics");
        searchFilter.setResourceType("Audio");
        searchFilter.setEducationalContext("Preschool");
        SearchResult searchResult = doGet(buildQueryURL(query, 0, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 2L);
        assertEquals(1, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    // Tests with License type

    @Test
    public void searchWithLicenseType() {
        String query = "database";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setLicenseType("CC");
        SearchResult searchResult = doGet(buildQueryURL(query, 0, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 2L, 1L);
        assertEquals(0L, searchResult.getStart());
        assertEquals(2L, searchResult.getTotalResults());
    }

    @Test
    public void searchWithSubjectAndLicenseTypeFilter() {
        String query = "filteredquery";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setSubject("Mathematics");
        searchFilter.setLicenseType("CCBY");
        SearchResult searchResult = doGet(buildQueryURL(query, 0, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 2L, 1L, 3L);
        assertEquals(3, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithResourceTypeAndLicenseTypeFilter() {
        String query = "beethoven";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setResourceType("Audio");
        searchFilter.setLicenseType("CCBYSA");
        String queryURL = buildQueryURL(query, 0, searchFilter);
        SearchResult searchResult = doGet(queryURL, SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 2L, 3L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithSubjectAndResourceTypeAndLicenseTypeFilter() {
        String query = "beethoven";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setSubject("Mathematics");
        searchFilter.setResourceType("Audio");
        searchFilter.setLicenseType("CCBYND");
        String queryURL = buildQueryURL(query, 0, searchFilter);
        SearchResult searchResult = doGet(queryURL, SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 2L, 4L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithEducationalContextAndLicenseTypeFilter() {
        String query = "beethoven";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setEducationalContext("preschool");
        searchFilter.setLicenseType("CCBYSA");
        String queryURL = buildQueryURL(query, 0, searchFilter);
        SearchResult searchResult = doGet(queryURL, SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 2L, 5L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithSubjectAndEducationalContextAndLicenseTypeFilter() {
        String query = "beethoven";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setSubject("Mathematics");
        searchFilter.setEducationalContext("Preschool");
        searchFilter.setLicenseType("CCBYNC");
        String queryURL = buildQueryURL(query, 0, searchFilter);
        SearchResult searchResult = doGet(queryURL, SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 2L, 6L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithResourceTypeAndEducationalContextAndLicenseTypeFilter() {
        String query = "beethoven";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setResourceType("audio");
        searchFilter.setEducationalContext("preschool");
        searchFilter.setLicenseType("CCBYND");
        String queryURL = buildQueryURL(query, 0, searchFilter);
        SearchResult searchResult = doGet(queryURL, SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 2L, 7L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithAllFilters() {
        String query = "john";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setSubject("Mathematics");
        searchFilter.setResourceType("Audio");
        searchFilter.setEducationalContext("Preschool");
        searchFilter.setLicenseType("other");
        searchFilter.setTitle("smith");
        searchFilter.setAuthor("mary");
        SearchResult searchResult = doGet(buildQueryURL(query, 0, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 2L, 8L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    // Tests with title (not all combinations)

    @Test
    public void searchWithTitleFilter() {
        String query = "web";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setTitle("www");
        SearchResult searchResult = doGet(buildQueryURL(query, 0, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 3L, 1L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithSubjectAndTitleFilter() {
        String query = "web";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setSubject("algebra");
        searchFilter.setTitle("www");
        SearchResult searchResult = doGet(buildQueryURL(query, 0, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 3L, 2L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithLicenseTypeAndResourceTypeAndTitleFilter() {
        String query = "web";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setResourceType("video");
        searchFilter.setLicenseType("other");
        searchFilter.setTitle("www");
        SearchResult searchResult = doGet(buildQueryURL(query, 0, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 3L, 4L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    // Tests with author

    @Test
    public void searchWithAuthorFilter() {
        String query = "books";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setAuthor("Mary");
        SearchResult searchResult = doGet(buildQueryURL(query, 0, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 4L, 1L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithResourceTypeAndAuthorFilter() {
        String query = "books";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setResourceType("unknown");
        searchFilter.setAuthor("Mary");
        SearchResult searchResult = doGet(buildQueryURL(query, 0, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 4L, 2L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithEducationalContextAndTitleAndAuthorFilter() {
        String query = "other books";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setEducationalContext("TEACHEREDUCATION");
        searchFilter.setTitle("Cool title");
        searchFilter.setAuthor("Mary");
        SearchResult searchResult = doGet(buildQueryURL(query, 0, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 4L, 3L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    // Tests with combined description

    @Test
    public void searchWithCombinedDescriptionFilter() {
        String query = "material";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setCombinedDescription("The description.");
        SearchResult searchResult = doGet(buildQueryURL(query, 0, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 5L, 1L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithLicenseTypeAndCombinedDescriptionFilter() {
        String query = "material";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setLicenseType("CCBYNCND");
        searchFilter.setCombinedDescription("Another description.");
        SearchResult searchResult = doGet(buildQueryURL(query, 0, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 5L, 2L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithEducationalContextAndAuthorAndCombinedDescriptionFilter() {
        String query = "material";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setEducationalContext("HIGHEREDUCATION");
        searchFilter.setAuthor("Mary");
        searchFilter.setCombinedDescription("More descriptions.");
        SearchResult searchResult = doGet(buildQueryURL(query, 0, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 5L, 3L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    // Tests with paid/free

    @Test
    public void searchWithPaidFilterTrue() {
        String query = "dop";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPaid(true);
        SearchResult searchResult = doGet(buildQueryURL(query, 0, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 6L, 4L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithPaidFilterFalse() {
        String query = "dop";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPaid(false);
        SearchResult searchResult = doGet(buildQueryURL(query, 0, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 6L, 1L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithEducationalContextAndPaidFilterFalse() {
        String query = "dop";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setEducationalContext("specialeducation");
        searchFilter.setPaid(false);
        SearchResult searchResult = doGet(buildQueryURL(query, 0, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 6L, 2L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithSubjectAndTitleAndAndPaidFilterFalse() {
        String query = "dop";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setSubject("music");
        searchFilter.setTitle("Opera");
        searchFilter.setPaid(false);
        SearchResult searchResult = doGet(buildQueryURL(query, 0, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 6L, 3L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    // Tests with type

    @Test
    public void searchWithTypeFilter() {
        String query = "weird";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setType("portfolio");
        int start = RESULTS_PER_PAGE;
        SearchResult searchResult = doGet(buildQueryURL(query, start, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 4L);
        assertEquals(4, searchResult.getTotalResults());
        assertEquals(start, searchResult.getStart());
    }

    @Test
    public void searchWithCombinedDescriptionAndTypeFilter() {
        String query = "weird";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setCombinedDescription("aliens");
        searchFilter.setType("material");
        SearchResult searchResult = doGet(buildQueryURL(query, 0, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 7L, 1L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithAuthorAndPaidFalseAndTypeFilter() {
        String query = "weird";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setAuthor("mati");
        searchFilter.setPaid(false);
        searchFilter.setType("material");
        SearchResult searchResult = doGet(buildQueryURL(query, 0, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 7L, 2L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    private String buildQueryURL(String query, int start, SearchFilter searchFilter) {
        String queryURL = "search?";
        if (query != null) {
            queryURL += "q=" + encodeQuery(query);
        }
        if (start != 0) {
            queryURL += "&start=" + start;
        }
        if (searchFilter.getSubject() != null) {
            queryURL += "&subject=" + encodeQuery(searchFilter.getSubject());
        }
        if (searchFilter.getResourceType() != null) {
            queryURL += "&resource_type=" + encodeQuery(searchFilter.getResourceType());
        }
        if (searchFilter.getEducationalContext() != null) {
            queryURL += "&educational_context=" + encodeQuery(searchFilter.getEducationalContext());
        }
        if (searchFilter.getLicenseType() != null) {
            queryURL += "&license_type=" + encodeQuery(searchFilter.getLicenseType());
        }
        if (searchFilter.getTitle() != null) {
            queryURL += "&title=" + encodeQuery(searchFilter.getTitle());
        }
        if (searchFilter.getAuthor() != null) {
            queryURL += "&author=" + encodeQuery(searchFilter.getAuthor());
        }
        if (searchFilter.getCombinedDescription() != null) {
            queryURL += "&combined_description=" + encodeQuery(searchFilter.getCombinedDescription());
        }
        if (searchFilter.isPaid() == false) {
            queryURL += "&paid=false";
        }
        if (searchFilter.getType() != null) {
            queryURL += "&type=" + encodeQuery(searchFilter.getType());
        }
        return queryURL;
    }

    private void assertMaterialIdentifiers(List<Searchable> objects, Long... materialIdentifiers) {
        assertEquals(materialIdentifiers.length, objects.size());

        for (int i = 0; i < materialIdentifiers.length; i++) {
            Searchable searchable = objects.get(i);
            assertEquals(materialIdentifiers[i], searchable.getId());

            if (searchable.getType().equals("material")) {
                assertTrue(searchable instanceof Material);
            } else if (searchable.getType().equals("portfolio")) {
                assertTrue(searchable instanceof Portfolio);
            } else {
                fail("No such Searchable type: " + searchable.getType());
            }
        }
    }

    private String encodeQuery(String query) {
        String encodedQuery;
        try {
            encodedQuery = URLEncoder.encode(query, UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return encodedQuery;
    }

}
